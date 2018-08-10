package com.icecream.common.util.constant;

/**
 * @author Mr_h
 * {@link}
 * 描述:系统常量类
 * 前缀
 * US ---->user-service
 * OS ---->order-service
 * ...
 * create by 2018/6/7 0007
 * @version 1.0
 */
public interface SysConstants {
    /**
     * code
     */
    String SMS_CHUANGLAN_ACCOUNT = "sms.chuanglan.account";
    String SMS_CHUANGLAN_PASSWORD = "sms.chuanglan.password";
    String SMS_CHUANGLAN_URL = "sms.chuanglan.url";
    String SMS_CHUANGLAN_CODE_TIMEOUT = "sms.chuanglan.code-time-out";


    /**
     * user_type
     */
    String USER_STAR = "star";
    String USER_CONSUMER = "consumer";


    String ORDER_ROUTING_KEY = "order-queue";

}
