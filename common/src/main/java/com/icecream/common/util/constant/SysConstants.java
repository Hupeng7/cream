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
    String GOODS_ROUTING_KEY = "goods_queue";
    String CHARGE_ROUTING_KEY = "charge-queue";


    /**
     * goods系统redis前缀
     */
    String GOODS_PREFIX = "goods";
    String GOODS_STOCK_PREFIX = "goods_stock";
    String GOODS_SPEC_PREFIX = "goods_spec";
    String HAS_BEEN_BOUGHT_PREFIX = "user_buy_limit";
    String USER_WALLET_PREFIX = "user_wallet";
    String USER_EXP = "user_exp";


    /**
     * order系统redis前缀
     */
    String ORDER_HASH_PREFIX = "orders";
    String ORDER_ZSET_PREFIX = "order_";

    /**
     * 分割符号
     */
    String SYMBOL_UNDERLINE = "_";
    String SYMBOL_WHIPPLETREE = "-";
    String SYMBOL_COLON = ":";
    String SYMBOL_SPRIT = "/";
}
