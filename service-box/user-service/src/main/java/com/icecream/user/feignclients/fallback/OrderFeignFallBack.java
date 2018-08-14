package com.icecream.user.feignclients.fallback;

import com.icecream.common.model.pojo.*;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.feignclients.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class OrderFeignFallBack implements OrderFeignClient {

    @Override
    public Order getOrderByOrderNo(String orderNo){
        log.info("调用order-service服务getOrderByOrderNo方法时发生异常/调用超时");
        throw new RuntimeException("order-service查询失败，进入回退");
    }

    @Override
    public int insert(Order order) {
        return 0;
    }

    @Override
    public String insertAliChargeRecord(AlipayNotifyRecord alipayNotifyRecord) {
        return "支付宝成功记录录入异常";
    }

    @Override
    public String insertAliChargeErrorRecord(AlipayNotifyRecordErrorLog alipayNotifyRecordErrorLog) {
        return "支付宝错误记录录入异常";
    }

    @Override
    public String insertWxChargeRecord(WechatpayNotifyRecord wechatpayNotifyRecord) {
        return null;
    }

    @Override
    public ResultVO getMeal() {
        log.info("获取充值列表失败。");
        return null;
    }

    @Override
    public Wallet getWallet(Integer uid) {
        log.info("获取我的钱包失败");
        Wallet wallet = new Wallet();
        wallet.setUid(uid);
        wallet.setBalance(new BigDecimal(0));
        return wallet;
    }

    @Override
    public ScoreRule getRule(Integer type, BigDecimal changePrice, Integer status) {
        return null;
    }

}
