package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class WechatpayNotifyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer uid;

    private String appid;

    private String attach;

    private Integer ctime;

    private Integer mtime;

    private String bankType;

    private String feeType;

    private String isSubscribe;

    private String mchId;

    private String nonceStr;

    private String openid;

    private String outTradeNo;

    private String resultCode;

    private String returnCode;

    private String sign;

    private String timeEnd;

    private Integer totalFee;

    private String tradeType;

    private String transactionId;

    private String returnMsg;

    private String deviceInfo;

    private String errCode;

    private String errCodeDes;

    private Integer cashFee;

    private String cashFeeType;

    private Integer couponFee;

    private Integer couponCount;

    private String couponIdN;

    private Integer couponFeeN;
}