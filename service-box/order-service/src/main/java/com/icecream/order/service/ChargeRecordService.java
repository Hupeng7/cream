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
            //支付宝的单位是元，与微信有区别
            BigDecimal charge = alipayNotifyRecord.getTotal_amount().multiply(new BigDecimal(100));
            int warn = charge.compareTo(BigDecimal.ZERO);
            if (warn < 0) {
                throw new Exception();
            }
            //插入支付宝记录表
            int countOne = alipayNotifyRecordMapper.insertSelective(alipayNotifyRecord);
            //插入流水表
            int countTwo = pointInoutMapper.insertSelective(caseToPointOut(alipayNotifyRecord));
            Wallet slecetArgs = new Wallet();
            slecetArgs.setUid(alipayNotifyRecord.getUid());
            Wallet wallet = walletMapper.selectOne(slecetArgs);
            //如果该用户从未充值过，数据库操作为insert
            if (wallet == null) {
                int row = walletMapper.insertSelective(buildWallet(-1, alipayNotifyRecord.getUid(), charge));
                return row > 0 ? "ok" : "";
            }
            //用户充值过，则在原来钱包的基础上加钱,数据库操作为update
            BigDecimal money = wallet.getBalance().add(charge);

            int row = walletMapper.updateByPrimaryKeySelective(buildWallet(
                    wallet.getId(), alipayNotifyRecord.getUid(), money));
            return row > 0 ? "ok" : "";
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
        orderService.updateOrderForCharge(buildOrder(wechatpayNotifyRecord));
        wechatpayNotifyRecord.setCtime(DateUtil.getNowSecondIntTime());
        wechatpayNotifyRecordMapper.insertSelective(wechatpayNotifyRecord);
        pointInoutMapper.insertSelective(caseToPointOut(wechatpayNotifyRecord));
        walletService.insertOrUpateHandler(wechatpayNotifyRecord.getUid(),
                new BigDecimal(wechatpayNotifyRecord.getTotal_fee() / 100));

    }

    public Wallet buildWallet(Integer id, Integer uid, BigDecimal money) {
        Wallet inserArgs = new Wallet();
        if (id != -1) {
            inserArgs.setId(id);
        }
        inserArgs.setUid(uid);
        inserArgs.setBalance(money);
        inserArgs.setCtime(Integer.parseInt(DateUtil.getNowSecond()));
        inserArgs.setSid(1);
        inserArgs.setStatus(1);
        return inserArgs;
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


    private PointInout caseToPointOut(
            AlipayNotifyRecord alipayNotifyRecord) {
        PointInout pointInout = new PointInout();
        pointInout.setCtime(Integer.parseInt(DateUtil.getNowSecond()));
        pointInout.setIntout(ADD);
        pointInout.setIsInuse(1);
        pointInout.setObjectId(alipayNotifyRecord.getId().toString());
        pointInout.setObjectType(TYPE_CHARGE);
        pointInout.setPoint(Integer.parseInt(alipayNotifyRecord.getTotal_amount().toString()));
        return pointInout;
    }

    private PointInout caseToPointOut(
            WechatpayNotifyRecord wechatpayNotifyRecord) {
        PointInout pointInout = new PointInout();
        pointInout.setId(UUIDFactory.create());
        pointInout.setCtime(Integer.parseInt(DateUtil.getNowSecond()));
        pointInout.setIntout(ADD);
        pointInout.setIsInuse(1);
        pointInout.setObjectId(wechatpayNotifyRecord.getId().toString());
        pointInout.setObjectType(TYPE_CHARGE);
        pointInout.setPoint(Integer.parseInt(wechatpayNotifyRecord.getTotal_fee().toString()));
        return pointInout;
    }


    private Order buildOrder(WechatpayNotifyRecord wechatpayNotifyRecord) {
        Order order = new Order();
        order.setIsPay(1);
        order.setPaymentType(2);
        order.setOrderStatus(4);
        order.setPayPrice(new BigDecimal(wechatpayNotifyRecord.getTotal_fee() / 100));
        order.setPayTime(DateUtil.getNowSecondIntTime());
        order.setMtime(DateUtil.getNowSecondIntTime());
        return order;
    }
}
