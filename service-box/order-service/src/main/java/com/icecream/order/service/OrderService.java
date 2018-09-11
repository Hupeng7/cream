package com.icecream.order.service;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.model.AddressInfo;
import com.icecream.common.model.model.CreateOrderModel;
import com.icecream.common.model.model.GoodsUpdateMessage;
import com.icecream.common.model.model.SkillUpdateModel;
import com.icecream.common.model.pojo.*;
import com.icecream.common.util.idbuilder.staticfactroy.SnowflakeGlobalIdFactory;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.time.DateUtil;
import com.icecream.order.feignclient.GoodsFeignClient;
import com.icecream.order.mapper.OrderMapper;
import com.icecream.order.rabbit.RabbitSender;
import com.icecream.order.redis.RedisHandler;
import com.icecream.order.utils.math.TradesNoCreater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.icecream.common.util.constant.SysConstants.*;


@Slf4j
@Service
@SuppressWarnings("all")
public class OrderService {

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

    @Autowired
    private RedisHandler redisHandler;

    public void initRedisBuyerInfo(Integer uid) {
        initWallet(uid);
        initExp(uid);
    }

    private void initWallet(Integer uid) {
        Wallet wallet = walletService.get(uid);
        if (wallet == null) {
            RedisHandler.set(USER_WALLET_PREFIX + SYMBOL_COLON + uid, 0);
        } else {
            RedisHandler.set(USER_WALLET_PREFIX + SYMBOL_COLON + uid, wallet.getBalance());
        }
    }


    private void initExp(Integer uid) {
        UserExp query = expService.query(uid);
        if (query == null) {
            RedisHandler.set(USER_EXP + SYMBOL_COLON + uid, 0);
        } else {
            RedisHandler.set(USER_EXP + SYMBOL_COLON + uid, query.getExp());
        }
    }

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
        log.info("检查商品缓存库存");
        Integer goodCount;
        if (null != createOrderModel.getSpecId()) {
            goodCount = (Integer) RedisHandler.get(GOODS_STOCK_PREFIX + SYMBOL_COLON + createOrderModel.getGoodsSn() + SYMBOL_COLON + createOrderModel.getSpecId());
        } else {
            goodCount = (Integer) RedisHandler.get(GOODS_STOCK_PREFIX + SYMBOL_COLON + createOrderModel.getGoodsSn());
        }
        if (createOrderModel.getGoodsCount() > goodCount) {
            return ResultUtil.error("商品库存不足", ResultEnum.CREATE_ORDER_FAILED);
        }

        log.info("开始准备数据");
        String goodsSn = createOrderModel.getGoodsSn();
        Goods goods = getGoodsCache(goodsSn);
        Integer hasBeenBought = getHasBeenBoughtCache(uid, goodsSn);
        BigDecimal balance = walletService.getBalance(uid);
        BigDecimal exp = expService.getExp(uid);
        Integer ifBuyNum = createOrderModel.getGoodsCount() + hasBeenBought;
        BigDecimal ifEnough;
        if (null == createOrderModel.getSpecId()) {
            ifEnough = balance.subtract(new BigDecimal(ifBuyNum).multiply(goods.getGoodsPrice()));
        } else {
            List<GoodsSpec> goodsSpec = goods.getGoodsSpec();
            List<GoodsSpec> collect = goodsSpec.stream().filter(gs -> gs.getId().equals(createOrderModel.getSpecId())).collect(Collectors.toList());
            if (collect.size() > 0) {
                GoodsSpec result = collect.get(0);
                ifEnough = balance.subtract(new BigDecimal(ifBuyNum).multiply(new BigDecimal(result.getPrice())));
            } else {
                return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
            }
        }
        log.info("数据准备完毕,开始数据验证");
        boolean bingo = doorkeeper(uid, goodsSn, goods, hasBeenBought, balance, exp, ifBuyNum, ifEnough);
        if (bingo) {
            GoodsUpdateMessage goodsUpdateMessage = new GoodsUpdateMessage();
            Order order = buildPreOrder(createOrderModel, uid, TradesNoCreater.create(),
                    getAddressInfo(createOrderModel.getAddress()),
                    goods);
            log.info("已经创建预下单对象...{}", order);
            log.info("开始进行预减数据操作");
            String IDcard = RedisHandler.lockWithTimeout(DISTRIBUTED_LOCK_IDENTIFICATION, 2, 5000);
            String limitKey = uid + SYMBOL_COLON + "have_done";
            Object limit = RedisHandler.get(limitKey);
            if (limit != null) {
                return ResultUtil.error("to fast request", ResultEnum.CREATE_ORDER_FAILED);
            } else {
                RedisHandler.set(limitKey, true);
                RedisHandler.setExpireTime(limitKey, 30, TimeUnit.SECONDS);
            }
            if (null != createOrderModel.getSpecId()) {
                String key = GOODS_STOCK_PREFIX + SYMBOL_COLON + createOrderModel.getGoodsSn() + SYMBOL_COLON + createOrderModel.getSpecId();
                Long decrSpecNum = RedisHandler.decr(key, createOrderModel.getGoodsCount());
                log.info("还剩余的多规格商品库存{}", decrSpecNum);
                if (decrSpecNum < 0) {
                    log.info("规格商品库存不够手动回滚");
                    Long incrNum = RedisHandler.incr(key, createOrderModel.getGoodsCount());
                    RedisHandler.set(key, incrNum.intValue());
                    RedisHandler.remove(order.getOrderNo());
                    return ResultUtil.error("商品库存不足", ResultEnum.CREATE_ORDER_FAILED);
                }
                goodsUpdateMessage.setGoodsNum(decrSpecNum.intValue());
            } else {
                Long decrNum = RedisHandler.decr(GOODS_PREFIX + createOrderModel.getGoodsSn(), createOrderModel.getGoodsCount());
                log.info("还剩余的单规格商品库存{}", decrNum);
                if (decrNum < 0) {
                    log.info("总库存不够手动回滚");
                    Long incrNum = RedisHandler.incr(GOODS_PREFIX + createOrderModel.getGoodsSn(), createOrderModel.getGoodsCount());
                    RedisHandler.set(GOODS_PREFIX + createOrderModel.getGoodsSn(), incrNum.intValue());
                    RedisHandler.remove(order.getOrderNo());
                    return ResultUtil.error("商品库存不足", ResultEnum.CREATE_ORDER_FAILED);
                }
                goodsUpdateMessage.setGoodsNum(decrNum.intValue());
            }
            RedisHandler.releaseLock(DISTRIBUTED_LOCK_IDENTIFICATION,IDcard);
            log.info("开始构建预修改数据");
            balance = balance.subtract(order.getChangePrice());
            exp = exp.add(order.getChangePrice());
            hasBeenBought = hasBeenBought + order.getGoodsCount();

            log.info("开始写回redis");
            RedisHandler.set(USER_WALLET_PREFIX + SYMBOL_COLON + uid, balance);
            RedisHandler.set(HAS_BEEN_BOUGHT_PREFIX + SYMBOL_COLON + uid + SYMBOL_COLON + goodsSn, hasBeenBought);
            RedisHandler.set(USER_EXP + SYMBOL_COLON + uid, exp);

            log.info("开始更改订单状态");
            Order finalOrder = updateOrderStatus(order);
            SkillUpdateModel skillUpdateModel = getSkillUpdateModel(uid, createOrderModel, goodsUpdateMessage, order, finalOrder);

            log.info("开始加入队列..");
            rabbitSender.sendOrderData(JSON.toJSONString(skillUpdateModel));

            log.info("向redis写回下单成功的订单");
            RedisHandler.addMap(ORDER_HASH_PREFIX, order.getOrderNo(), JSON.toJSONString(finalOrder));
            RedisHandler.addZSet(ORDER_ZSET_PREFIX, order.getCtime(), order.getOrderNo());

            return ResultUtil.success("创建订单成功");
        } else {
            return ResultUtil.error("条件不足，无法创建订单", ResultEnum.CREATE_ORDER_FAILED);
        }

    }

    private SkillUpdateModel getSkillUpdateModel(Integer uid, CreateOrderModel createOrderModel, GoodsUpdateMessage goodsUpdateMessage, Order order, Order finalOrder) {
        SkillUpdateModel skillUpdateModel = new SkillUpdateModel();
        goodsUpdateMessage.setSid(order.getSid());
        goodsUpdateMessage.setUid(uid);
        goodsUpdateMessage.setBought(createOrderModel.getGoodsCount());
        goodsUpdateMessage.setGoodsSn(createOrderModel.getGoodsSn());
        goodsUpdateMessage.setSpecId(createOrderModel.getSpecId());
        goodsUpdateMessage.setCount(order.getGoodsCount());
        skillUpdateModel.setGoodsUpdateMessage(goodsUpdateMessage);
        skillUpdateModel.setOrder(finalOrder);
        return skillUpdateModel;
    }

    //获取商品hash表信息
    private Goods getGoodsCache(String goodsSn) {
        Goods goods = null;
        try {
            Object mapField = RedisHandler.getMapField(GOODS_PREFIX, goodsSn);
            goods = JSON.parseObject(mapField.toString(), Goods.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从redis中获取商品信息失败");
            return null;
        }
        return goods;
    }

    //获取商品购买记录缓存信息
    private Integer getHasBeenBoughtCache(Integer uid, String goodsSn) {
        Integer count = 0;
        try {
            String key = HAS_BEEN_BOUGHT_PREFIX + SYMBOL_COLON + uid + SYMBOL_COLON + goodsSn;
            Object record = RedisHandler.get(key);
            if (record != null) {
                count = Integer.parseInt(record.toString());
                return count;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从redis中获取商品信息失败");
            return -1;
        }
        return -1;
    }

    //验证是否可以进行下单操作
    private boolean doorkeeper(Integer uid, String goodsSn, Goods goods
            , Integer hasBeenBought, BigDecimal balance, BigDecimal exp, Integer ifBuyNum,
                               BigDecimal ifEnough) {
        //验证星星是否足够
        if (ifEnough.compareTo(BigDecimal.ZERO) == -1) {
            return false;
        }
        //验证已经购买数
        if (hasBeenBought == -1) {
            return false;
        }
        //验证余额
        if (balance.intValue() == 0) {
            return false;
        }
        //验证经验
        if (exp.intValue() == -1) {
            return false;
        }
        //验证商品
        if (null != goods) {
            Integer onsaleTime = goods.getOnsaleTime();
            Integer offsaleTime = goods.getOffsaleTime();
            int now = (int) (LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
            if (goods.getOnsaleTime() <= now & now <= goods.getOffsaleTime() & goods.getIsSale() == 1) {
                if (ifBuyNum <= goods.getBuylimit() | goods.getBuylimit() == -1) {
                    return true;
                }
            }
        }
        return false;
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
                                Goods goods) {
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
        order.setPayTime(0);//支付时间
        order.setChangeTime(0);
        order.setCtime(DateUtil.getNowSecondIntTime());
        order.setReportType(2);//商品账单
        order.setAccount("-1");
        order.setOrderType(1);//平台交易

        if (null != createOrderModel.getSpecId()) {
            List<GoodsSpec> goodsSpecs = goods.getGoodsSpec();
            if (goodsSpecs.size() > 0) {
                List<GoodsSpec> list = goodsSpecs.stream().filter(g -> g.getId().equals(createOrderModel.getSpecId())).collect(Collectors.toList());
                if (list.size() > 0) {
                    GoodsSpec goodsSpec = list.get(0);
                    order.setSpec(goodsSpec.getSpec());
                    order.setSpecId(goodsSpec.getId());
                    order.setGoodsPrice(new BigDecimal(goodsSpec.getPrice()));
                    order.setChangePrice(new BigDecimal(goodsSpec.getPrice() * createOrderModel.getGoodsCount()));
                    order.setPayPrice(order.getChangePrice());//实际支付价格
                }
            }
        } else {
            BigDecimal goodsPrice = goods.getGoodsPrice();
            order.setGoodsPrice(goodsPrice);
            order.setChangePrice(goodsPrice.multiply(new BigDecimal(createOrderModel.getGoodsCount())));
            order.setPayPrice(order.getChangePrice());//实际支付价格
        }
        return order;
    }

    private Order updateOrderStatus(Order order) {
        order.setOrderStatus(4);
        order.setIsPay(1);
        order.setPayTime(DateUtil.getNowSecondIntTime());
        return order;
    }
}

