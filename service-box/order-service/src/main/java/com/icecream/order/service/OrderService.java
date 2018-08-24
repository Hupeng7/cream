package com.icecream.order.service;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.model.requstbody.*;
import com.icecream.common.redis.RedisHandler;
import com.icecream.common.util.idbuilder.staticfactroy.SnowflakeGlobalIdFactory;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.time.DateUtil;
import com.icecream.order.feignclient.GoodsFeignClient;
import com.icecream.order.mapper.OrderMapper;
import com.icecream.order.rabbit.RabbitSender;
import com.icecream.order.utils.math.TradesNoCreater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.icecream.common.util.constant.SysConstants.*;


@Slf4j
@Service
@SuppressWarnings("all")
public class OrderService{

    @Value("${thread.isSync}")
    private boolean isSync;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ExpService expService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private PointInoutService pointInoutService;

    @Autowired
    private GoodsFeignClient goodsFeignClient;

    @Autowired
    private SnowflakeGlobalIdFactory snowflakeGlobalIdFactory;

    @Autowired
    private RabbitSender rabbitSender;

    //获取订单详情
    public Order getOrderByOrderNo(Integer sid, String orderNo) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        return Optional.ofNullable(orderMapper.selectOne(order)).orElse(null);
    }

    //更新订单地址
    public ResultVO updateOrderAddress(Integer sid, String orderNo, String address) {
        Order order = getOrderByOrderNo(sid, orderNo);
        if ("".equals(address) | null == address) {
            return ResultUtil.error("address参数不能为空", ResultEnum.PARAMS_ERROR);
        }
        order.setAddress(address);
        AddressInfo addressInfo = getAddressInfo(address);
        order.setCity(addressInfo.getCity());
        order.setPhone(addressInfo.getPhone());
        order.setProvince(addressInfo.getProvince());
        order.setDistrict(addressInfo.getDistrict());
        order.setAddressee(addressInfo.getAddressSee());
        return ResultUtil.success(orderMapper.updateByPrimaryKeySelective(order));
    }

    //创建订单
    public ResultVO create(Integer uid, CreateOrderModel createOrderModel) {
        MitGoodsRedis goodsRedis = JSON.parseObject(RedisHandler
                .getMapField(GOODS_PREFIX, createOrderModel.getGoodsSn()), MitGoodsRedis.class);
        log.info("获取到商品信息,{}", goodsRedis);
        log.info("开始创建预下单对象...");
        Order order = buildPreOrder(createOrderModel, uid, TradesNoCreater.create(),
                getAddressInfo(createOrderModel.getAddress()),
                goodsRedis.getGood().getGoodsPrice(),"");
        log.info("已经创建预下单对象...{}",order);
        RedisHandler.addMap("orders",order.getOrderNo(),JSON.toJSONString(order));
        log.info("预下单对象已经加入缓存");

        log.info("开始进行预减数据操作");
        Long decrNum = RedisHandler.decr(GOODS_PREFIX + createOrderModel.getGoodsSn(), createOrderModel.getGoodsCount());
        log.info("还剩余的库存{}", decrNum);
        Integer hasBeenBoughtCount = RedisHandler.get(HAS_BEEN_BOUGHT_PREFIX + createOrderModel.getGoodsSn())
                != null ? Integer.parseInt(RedisHandler.get(HAS_BEEN_BOUGHT_PREFIX + createOrderModel.getGoodsSn()).toString()) : -1;
        log.info("用户已经购买的件数,{}", hasBeenBoughtCount);
        Integer buyLimit = Optional.ofNullable(goodsRedis)
                .map(MitGoodsRedis::getGood)
                .map(Good::getBuylimit)
                .get();
        log.info("商品限制用户购买的数量,{}", buyLimit);
        Integer canBuyNum = buyLimit - hasBeenBoughtCount;
        log.info("用户能够购买的件数,{}", canBuyNum);

        if (decrNum < 0) {
            log.info("库存不够手动回滚");
            RedisHandler.set(GOODS_PREFIX + createOrderModel.getGoodsSn(), 0);
            RedisHandler.remove(order.getOrderNo());
            return ResultUtil.error("商品抢完啦~ 请下次再来", ResultEnum.CREATE_ORDER_FAILED);
        }

        BigDecimal balance = new BigDecimal(RedisHandler.get(USER_WALLET_PREFIX + uid).toString());
        Integer hasBeenBought = Integer.parseInt(RedisHandler.get(HAS_BEEN_BOUGHT_PREFIX + createOrderModel.getGoodsSn()).toString());
        Integer exp = Integer.parseInt(RedisHandler.get(USER_EXP + uid).toString());

        if (balance.subtract(order.getChangePrice()).compareTo(BigDecimal.ZERO)<0) {
            log.info("星星不够手动回滚");
            RedisHandler.removeMapField("orders",order.getOrderNo());
            RedisHandler.removeZSet("order_"+uid);
            return ResultUtil.error("星星不够哦，请先充值", ResultEnum.CREATE_ORDER_FAILED);
        }

        if (canBuyNum < createOrderModel.getGoodsCount()) {
            log.info("商品购买超过限购");
            RedisHandler.removeMapField("orders",order.getOrderNo());
            RedisHandler.removeZSet("order_"+uid);
            return ResultUtil.error("商品购买超过限制", ResultEnum.CREATE_ORDER_FAILED);
        }
        balance = balance.subtract(order.getChangePrice());
        exp =exp+order.getChangePrice().intValue();
        hasBeenBought = hasBeenBoughtCount+order.getGoodsCount();

        log.info("开始写回redis");
        RedisHandler.set(USER_WALLET_PREFIX + uid,balance);
        RedisHandler.set(HAS_BEEN_BOUGHT_PREFIX + createOrderModel.getGoodsSn(),hasBeenBought);
        RedisHandler.set(USER_EXP + uid,exp);

        log.info("您就是万中无一的幸运儿-->{}", uid);
        log.info("开始更改订单状态");
        Order finalOrder = updateOrderStatus(order);
        SkillUpdateModel skillUpdateModel = new SkillUpdateModel();
        GoodsUpdateMessage goodsUpdateMessage = new GoodsUpdateMessage();
        goodsUpdateMessage.setSid(order.getSid());
        goodsUpdateMessage.setUid(uid);
        goodsUpdateMessage.setGoodsNum(decrNum.intValue());
        goodsUpdateMessage.setBought(createOrderModel.getGoodsCount());
        goodsUpdateMessage.setGoodsSn(createOrderModel.getGoodsSn());
        skillUpdateModel.setGoodsUpdateMessage(goodsUpdateMessage);
        skillUpdateModel.setOrder(finalOrder);
        log.info("开始加入队列..");
        log.info("开始向订单系统发送数据..");
        rabbitSender.sendOrderData(JSON.toJSONString(skillUpdateModel));
        log.info("向redis写回下单成功的订单");
        RedisHandler.addMap("orders",order.getOrderNo(),JSON.toJSONString(finalOrder));
        RedisHandler.addZSet("order_"+uid,order.getCtime(),order.getOrderNo());
        return ResultUtil.success("创建订单成功");
    }

    private Order createOrder(Integer uid, CreateOrderModel createOrderModel, GoodsStoreModel goodsStoreModel, BigDecimal goodsPrice) {
        String orderNo = TradesNoCreater.create();
        AddressInfo addressInfo = getAddressInfo(createOrderModel.getAddress());
        String spec = goodsStoreModel.getSpec();
        return buildPreOrder(createOrderModel, uid, orderNo, addressInfo, goodsPrice, spec);
    }

    //创建订单涉及到多张表
    @Transactional
    private boolean transactionInsert(Order order) {
        Integer uid = order.getUid();
        String goodsId = order.getGoodsId();
        int orderRow = insert(order);
        expService.insertOrUpdateHandler(order.getUid(), order.getSid(), order.getGoodsPrice());
        int pointRow = pointInoutService.insertPointInoutOrder(order.getGoodsPrice(), uid, order.getOrderNo());
        return orderRow > 0 & pointRow > 0;
    }


    //异步创建订单
    private void toAsynCreate(Order order) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                transactionInsert(order);
            }
        };
        executor.submit(task);
    }

    //插入订单数据
    public int insert(Order order) {
        return orderMapper.insertSelective(order);
    }


    public int updateOrderForCharge(Order order) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uid", order.getUid());
        criteria.andEqualTo("isPay", 0);
        criteria.andEqualTo("status", 1);
        criteria.andEqualTo("orderStatus", 0);
        criteria.andEqualTo("paymentType", 2);
        criteria.andEqualTo("isDigital", 1);
        return orderMapper.updateByExampleSelective(order, example);
    }

    public ResultVO getOrderDetail(Integer sid, String orderNo, Integer uid) {
        Order orderDetail = orderMapper.getOrderDetail(sid, orderNo, uid);

        return orderDetail == null ? ResultUtil.error(null, ResultEnum.PARAMS_ERROR) :
                ResultUtil.success(orderDetail);
    }

    public ResultVO getOrderListSort(Integer count, Integer lastTime, Integer sort, Integer uid) {
        Example example = new Example(Order.class);
        example.setOrderByClause(sort == -1 ? "ctime desc limit " + count : " ctime asc limit " + count);
        example.setCountProperty(count.toString());
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uid", uid);
        criteria.andGreaterThan("ctime", lastTime);
        List<Order> orders = orderMapper.selectByExample(example);
        return ResultUtil.success(Optional.ofNullable(orders).orElse(null));
    }

    private AddressInfo getAddressInfo(String address) {
        AddressInfo addressInfo = new AddressInfo();
        String[] outer = address.replace("|", "&").split("&");
        if (outer.length == 2) {
            String[] inner1 = outer[0].split(",");
            addressInfo.setAddressSee(inner1[0]);
            addressInfo.setPhone(inner1[1]);
            addressInfo.setProvince(inner1[2]);
            addressInfo.setCity(addressInfo.getProvince());
            addressInfo.setDistrict(outer[1]);
        } else {
            String[] inner1 = outer[0].split(",");
            String[] inner2 = outer[1].split(",");
            addressInfo.setAddressSee(inner1[0]);
            addressInfo.setPhone(inner1[1]);
            addressInfo.setProvince(inner1[2]);
            addressInfo.setCity(inner2[0]);
            addressInfo.setDistrict(outer[2]);
        }
        return addressInfo;
    }


    //创建预下单对象
    private Order buildPreOrder(CreateOrderModel createOrderModel,
                             Integer uid, String orderNo, AddressInfo addressInfo,
                             BigDecimal goodsPrice,String spec) {
        Order order = new Order();
        order.setUid(uid);
        order.setGoodsId(createOrderModel.getGoodsSn());
        order.setSid(createOrderModel.getSid()); //粉丝端
        order.setCreater(-1);//创建者系统
        order.setPaymentType(4);//支付类型 4星星
        order.setStatus(1); //状态为正常
        order.setOrderStatus(0);//订单状态为未付款
        order.setIsDigital(createOrderModel.getIsDigital());
        order.setIsPay(0);//未支付
        order.setSpecId(createOrderModel.getSpecId());
        order.setSpec(spec);
        order.setGoodsCount(createOrderModel.getGoodsCount());
        order.setIsDigital(createOrderModel.getIsDigital());
        order.setAddress(createOrderModel.getAddress());
        order.setAddressee(addressInfo.getAddressSee());
        order.setDistrict(addressInfo.getDistrict());
        order.setPhone(addressInfo.getPhone());
        order.setCity(addressInfo.getCity());
        order.setProvince(addressInfo.getProvince());
        order.setAmount(new BigDecimal(1));
        order.setOrderNo(orderNo);
        order.setGoodsId(createOrderModel.getGoodsSn());
        order.setGoodsPrice(goodsPrice);
        order.setReportType(2);//商品账单
        order.setAccount("-1");
        order.setOrderType(1);//平台交易
        order.setChangePrice(goodsPrice.multiply(new BigDecimal(createOrderModel.getGoodsCount())));
        order.setPayPrice(order.getChangePrice());//实际支付价格
        order.setPayTime(0);//支付时间
        order.setChangeTime(0);
        order.setCtime(DateUtil.getNowSecondIntTime());
        return order;
    }

    private Order updateOrderStatus(Order order){
        order.setOrderStatus(4);
        order.setIsPay(1);
        order.setPayTime(DateUtil.getNowSecondIntTime());
        return order;
    }
}

