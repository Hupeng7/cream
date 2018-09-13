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
     * user系统的redis前缀
     */
    String USER_HASH_PREFIX = "users";
    String USER_STAR_HASH_PREFIX = "stars";

    /**
     * 分割符号
     */
    String SYMBOL_UNDERLINE = "_";
    String SYMBOL_WHIPPLETREE = "-";
    String SYMBOL_COLON = ":";
    String SYMBOL_SPRIT = "/";

    /**
     * order 分布式锁 key
     */
    String DISTRIBUTED_LOCK_IDENTIFICATION = "order_lock";

    /**
     * 系统头像框的redis前缀
     */
    String SYS_PHOTOFRAME = "sysphotoframe";

    /**
     * 用户当前佩戴头像框的redis前缀
     */
    String USER_PHOTOFRAME = "userphotoframe";

    /**
     * 头像框img前缀 分为开发和生产环境
     */
    String DEV_SYS_PHOTOFRAME_IMG_PREFIX = "http://pa8qtq20a.bkt.clouddn.com/";
    String PRODUCT_SYS_PHOTOFRAME_IMG_PREFIX = "http://pa7ckvkli.bkt.clouddn.com/";

    /**
     * 接口权限设定
     * 1明星 2粉丝
     */
    String STAR = "STAR";
    String FANS = "FANS";


    /**
     * 评论系统声明队列
     */
    String COMMENT_QUEUE ="comment-queue";
    String COMMENT_HEADLINE_QUEUE="comment-headline-queue";
    String COMMENT_CHANNEL_QUEUE = "comment-channel-queue";

    /**
     * 评论系统声明交换机
     */
    String COMMENT_EXCHANGE = "comment-dir-exchange";

    /**
     * 用户系统队列
     */
    String USER_QUEUE = "user-queue";

    /**
     * 用户系统交换机
     */
    String USER_EXCHANGE = "user-dir-exchange";

    /**
     * 订单系统队列
     */
    String ORDER_QUEUE = "order-queue";
    String CHARGE_QUEUE = "charge-queue";
    String ERROR_ORDER_QUEUE = "error-order-queue";

    /**
     * 订单系统交换机
     */
    String ORDER_EXCHANGE = "order-dir-exchange";


    /**
     * 商品系统队列
     */
    String GOODS_QUEUE = "goods-queue";

    /**
     *商品系统交换机
     */
    String GOODS_EXCHANGE = "goods-dir-exchange";
}
