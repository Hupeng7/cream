package com.icecream.order.service;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.pojo.*;
import com.icecream.common.util.constant.SysConstants;
import com.icecream.common.util.time.DateUtil;
import com.icecream.common.util.uuid.UUIDFactory;
import com.icecream.order.mapper.*;
import com.icecream.order.redis.RedisHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.icecream.common.util.constant.SysConstants.ORDER_ZSET_PREFIX;
import static com.icecream.common.util.constant.SysConstants.SYMBOL_COLON;
import static com.icecream.common.util.constant.SysConstants.USER_WALLET_PREFIX;
import static com.icecream.order.contants.Contants.ADD;
import static com.icecream.order.contants.Contants.TYPE_CHARGE;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/2 0002
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class ChargeRecordService {

    @Autowired
    private AlipayNotifyRecordMapper alipayNotifyRecordMapper;

    @Autowired
    private AlipayNotifyRecordErrorLogMapper alipayNotifyRecordErrorLogMapper;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private PointInoutMapper pointInoutMapper;

    @Autowired
    private WechatpayNotifyRecordMapper wechatpayNotifyRecordMapper;

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderService orderService;

    @Transactional(rollbackFor = Exception.class)
    public String insert(AlipayNotifyRecord alipayNotifyRecord) {
        try {
            Order order = orderService.getOrderByOrderNo(1,alipayNotifyRecord.getOut_tradeNo());
            BigDecimal goodsPrice = order.getGoodsPrice();
            orderService.updateOrderForCharge(buildOrder(alipayNotifyRecord, order.getPaymentType()));
            alipayNotifyRecordMapper.insertSelective(alipayNotifyRecord);
            pointInoutMapper.insertSelective(caseToPointOut(alipayNotifyRecord, goodsPrice, order.getPaymentType()));
            walletService.insertOrUpateHandler(alipayNotifyRecord.getUid(), goodsPrice);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            log.info("错误的数据为------>", alipayNotifyRecord);
            AlipayNotifyRecordErrorLog alipayNotifyRecordErrorLog = caseToAlipayNotifyRecordErrorLog(alipayNotifyRecord);
            insert(alipayNotifyRecordErrorLog);
            return "";
        }
    }

    public String insert(AlipayNotifyRecordErrorLog alipayNotifyRecordErrorLog) {
        int count = alipayNotifyRecordErrorLogMapper.insert(alipayNotifyRecordErrorLog);
        return count > 0 ? "ok" : "";
    }


    @Transactional(rollbackFor = Exception.class)
    public Integer insert(WechatpayNotifyRecord wechatpayNotifyRecord) {
        Order order = orderService.getOrderByOrderNo(1,wechatpayNotifyRecord.getOut_trade_no());
        //如果订单已经支付
        if(order.getIsPay()==1)return 0;
        BigDecimal goodsPrice = order.getGoodsPrice();
        int row1 = orderService.updateOrderForCharge(buildOrder(wechatpayNotifyRecord, order.getPaymentType()));
        //微信重复发通知
        int row2 = wechatpayNotifyRecordMapper.insertSelective(wechatpayNotifyRecord);
        int row3 = pointInoutMapper.insertSelective(caseToPointOut(wechatpayNotifyRecord, goodsPrice, order.getPaymentType()));
        int row4 = walletService.insertOrUpateHandler(wechatpayNotifyRecord.getUid(), goodsPrice);
        if(row1>0&row2>0&row3>0&row4>0){
            Wallet wallet = walletService.get(wechatpayNotifyRecord.getUid());
            RedisHandler.addMap(SysConstants.ORDER_HASH_PREFIX,order.getOrderNo(), JSON.toJSONString(order));
            RedisHandler.addZSet(ORDER_ZSET_PREFIX, order.getCtime(), order.getOrderNo());
            RedisHandler.set(USER_WALLET_PREFIX + SYMBOL_COLON + wechatpayNotifyRecord.getUid(),wallet.getBalance());
            return 1;
        }
        return 0;
    }

    private AlipayNotifyRecordErrorLog caseToAlipayNotifyRecordErrorLog(
            AlipayNotifyRecord alipayNotifyRecord) {
        AlipayNotifyRecordErrorLog alipayNotifyRecordErrorLog = new AlipayNotifyRecordErrorLog();
        alipayNotifyRecordErrorLog.setAppId(alipayNotifyRecord.getApp_id());
        alipayNotifyRecordErrorLog.setCharset(alipayNotifyRecord.getCharset());
        alipayNotifyRecordErrorLog.setCtime(Integer.parseInt(DateUtil.getNowSecond()));
        alipayNotifyRecordErrorLog.setNotifyId(alipayNotifyRecord.getNotify_id());
        alipayNotifyRecordErrorLog.setNotifyTime(alipayNotifyRecord.getNotify_time());
        alipayNotifyRecordErrorLog.setOutTradeNo(alipayNotifyRecord.getOut_tradeNo());
        alipayNotifyRecordErrorLog.setNotifyType(alipayNotifyRecord.getNotify_type());
        alipayNotifyRecordErrorLog.setSign(alipayNotifyRecord.getSign());
        alipayNotifyRecordErrorLog.setTradeNo(alipayNotifyRecord.getTradeNo());
        alipayNotifyRecordErrorLog.setOutTradeNo(alipayNotifyRecord.getOut_tradeNo());
        alipayNotifyRecordErrorLog.setCtime(Integer.parseInt(DateUtil.getNowSecond()));
        return alipayNotifyRecordErrorLog;
    }


    private PointInout caseToPointOut(Object o, BigDecimal goodsPrice, Integer paymentType) {
        PointInout pointInout = new PointInout();
        pointInout.setId(UUIDFactory.create());
        pointInout.setCtime(Integer.parseInt(DateUtil.getNowSecond()));
        pointInout.setIntout(ADD);
        pointInout.setIsInuse(1);
        pointInout.setObjectType(TYPE_CHARGE);
        if (paymentType == 1) {
            AlipayNotifyRecord alipayNotifyRecord = (AlipayNotifyRecord) o;
            pointInout.setUid(alipayNotifyRecord.getUid());
            pointInout.setObjectId(alipayNotifyRecord.getId().toString());
            pointInout.setPoint(goodsPrice.intValue());
        } else if (paymentType == 2) {
            WechatpayNotifyRecord wechatpayNotifyRecord = (WechatpayNotifyRecord) o;
            pointInout.setUid(wechatpayNotifyRecord.getUid());
            pointInout.setObjectId(wechatpayNotifyRecord.getId().toString());
            pointInout.setPoint(goodsPrice.intValue());
        }
        return pointInout;
    }

    private Order buildOrder(Object o, Integer paymentType) {
        Order order = new Order();
        order.setIsPay(1);
        order.setPaymentType(2);
        order.setOrderStatus(4);
        order.setPayTime(DateUtil.getNowSecondIntTime());
        order.setMtime(DateUtil.getNowSecondIntTime());
        if (paymentType == 1) {
            AlipayNotifyRecord alipayNotifyRecord = (AlipayNotifyRecord) o;
            order.setPayPrice(alipayNotifyRecord.getTotal_amount());
        } else if (paymentType == 2) {
            WechatpayNotifyRecord wechatpayNotifyRecord = (WechatpayNotifyRecord) o;
            order.setPayPrice(new BigDecimal(wechatpayNotifyRecord.getTotal_fee() / 100));
        }
        return order;
    }
}
