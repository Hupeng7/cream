package com.icecream.order.service;

import com.codingapi.tx.annotation.ITxTransaction;
import com.codingapi.tx.annotation.TxTransaction;
import com.icecream.common.model.pojo.*;
import com.icecream.common.model.requstbody.AddressInfo;
import com.icecream.common.model.requstbody.CreateOrderModel;
import com.icecream.common.util.idbuilder.staticfactroy.SnowflakeGlobalIdFactory;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.time.DateUtil;
import com.icecream.order.feignclient.GoodsFeignClient;
import com.icecream.order.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Slf4j
@Service
@SuppressWarnings("all")
public class OrderService {

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
    @Transactional(rollbackFor = Exception.class)
    public ResultVO create(Integer uid, CreateOrderModel createOrderModel) {
        boolean allow = goodsFeignClient.checkBuyCount(createOrderModel);
        if (allow) {
            String spec = "";
            String orderNo = LocalDate.now().toString().replace("-", "") + String.valueOf(snowflakeGlobalIdFactory.create().nextId());
            AddressInfo addressInfo = getAddressInfo(createOrderModel.getAddress());
            BigDecimal goodsPrice = BigDecimal.ZERO;
            if ( null != createOrderModel.getSpecId()) {
                GoodsSpec goodsSpec = goodsFeignClient.getSpec(createOrderModel.getSpecId());
                goodsPrice = new BigDecimal(goodsSpec.getPrice());
                spec = goodsSpec.getSpecOpt();
            } else {
                Good good = goodsFeignClient.get(createOrderModel.getGoodsSn());
                goodsPrice = good.getGoodsPrice();
            }
            Wallet wallet = walletService.get(uid);
            int symbol = wallet.getBalance().compareTo(goodsPrice.multiply(new BigDecimal(createOrderModel.getGoodsCount())));
            if (-1 == symbol) {
                return ResultUtil.error("星星不够啦!", ResultEnum.CREATE_ORDER_FAILED);
            }
            Order order = buildOrder(createOrderModel, uid, orderNo, addressInfo, goodsPrice,spec);
            if (isSync) {
                toAsynCreate(order);
                return ResultUtil.success("订单创建成功");
            } else {
                return transactionInsert(order) ? ResultUtil.success("订单创建成功")
                        : ResultUtil.error(null, ResultEnum.CREATE_ORDER_FAILED);
            }
        } else {
            return ResultUtil.error("购买超出限制", ResultEnum.CREATE_ORDER_FAILED);
        }
    }

    //创建订单涉及到多张表
    @TxTransaction(isStart = true)
    @Transactional
    private boolean transactionInsert(Order order) {
        Integer uid = order.getUid();
        String goodsId = order.getGoodsId();
        int orderRow = insert(order);
        int expRow = expService.insertOrUpdateHandler(order.getUid(), order.getSid(), order.getGoodsPrice());
        int limitRow = goodsFeignClient.updateGoodsCount(order);
        int walletRow = walletService.updateForConsume(order.getGoodsPrice(), order.getUid());
        int pointRow = pointInoutService.insertPointInoutOrder(order.getGoodsPrice(), uid, order.getOrderNo());
        return orderRow > 0 & limitRow > 0 & expRow > 0 & walletRow > 0 & pointRow > 0;
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

    private Order buildOrder(CreateOrderModel createOrderModel,
                             Integer uid, String orderNo, AddressInfo addressInfo,
                             BigDecimal goodsPrice,String spec) {
        Order order = new Order();
        order.setUid(uid);
        order.setGoodsId(createOrderModel.getGoodsSn());
        order.setSid(createOrderModel.getSid()); //粉丝端
        order.setCreater(-1);//创建者系统
        order.setPaymentType(4);//支付类型 4星星
        order.setStatus(1); //状态为正常
        order.setOrderStatus(4);//订单状态为已付款
        order.setIsDigital(1);//实物商品
        order.setIsPay(1);//已支付
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
        order.setGoodsPrice(goodsPrice.multiply(new BigDecimal(createOrderModel.getGoodsCount())));
        order.setReportType(2);//商品账单
        order.setAccount("-1");
        order.setOrderType(1);//平台交易
        order.setPayPrice(order.getGoodsPrice());//实际支付价格
        order.setPayTime(DateUtil.getNowSecondIntTime());//支付时间
        order.setChangePrice(new BigDecimal(0));
        order.setChangeTime(0);
        order.setCtime(DateUtil.getNowSecondIntTime());
        return order;
    }
}

