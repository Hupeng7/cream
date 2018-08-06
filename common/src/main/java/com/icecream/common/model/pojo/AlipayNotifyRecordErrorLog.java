package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class AlipayNotifyRecordErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer notifyTime;

    private String notifyType;

    private String notifyId;

    private String appId;

    private String charset;

    private String version;

    private String signType;

    private String sign;

    private String tradeNo;

    private String outTradeNo;

    private Integer ctime;

}