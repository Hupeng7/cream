package com.icecream.order.service;

import com.icecream.common.model.pojo.*;
import com.icecream.common.util.time.DateUtil;
import com.icecream.common.util.uuid.UUIDFactory;
import com.icecream.order.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

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
            Order order = orderService.getOrderByOrderNo(alipayNotifyRecord.getOut_tradeNo());
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
    public void insert(WechatpayNotifyRecord wechatpayNotifyRecord) {
        Order order = orderService.getOrderByOrderNo(wechatpayNotifyRecord.getOut_trade_no());
        BigDecimal goodsPrice = order.getGoodsPrice();
        orderService.updateOrderForCharge(buildOrder(wechatpayNotifyRecord, order.getPaymentType()));
        wechatpayNotifyRecordMapper.insertSelective(wechatpayNotifyRecord);
        pointInoutMapper.insertSelective(caseToPointOut(wechatpayNotifyRecord, goodsPrice, order.getPaymentType()));
        walletService.insertOrUpateHandler(wechatpayNotifyRecord.getUid(), goodsPrice);
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
