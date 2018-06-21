package com.icecreamGroup.common.util.constant;

/**
 * @version 1.0
 * @author Mr_h
 * {@link}
 * 描述:常量池
 * 前缀
 * US ---->user-service
 * OS ---->order-service
 * ...
 * create by 2018/6/7 0007
 */
public interface ConstantVal {
    Integer US_QUERY_NO_RESULT_CODE = 11003;
    Integer US_QUERY_FAILED_CODE = 11006;
    Integer US_INSERT_FAILED_CODE = 11002;
    Integer US_UPDATE_FAILED_CODE =11004;
    Integer US_DELETE_FAILED_CODE=11005;
    String US_QUERY_FAILED ="查询失败";
    String US_QUERY_NO_RESULT = "查询结果为空";
    String US_INSERT_FAILED = "插入失败";
    String US_UPDATE_FAILED="更新失败";
    String US_DELETE_FAILED= "删除失败";
    String WEIBO_OPEN_API_URL="login.weibo-open-api-url";


    /**
     * sms
     */
    String SMS_CHUANGLAN_ACCOUNT="sms.chuanglan.account";
    String SMS_CHUANGLAN_PASSWORD="sms.chuanglan.password";
    String SMS_CHUANGLAN_URL= "sms.chuanglan.url";
    String SMS_CHUANGLAN_CODE_TIMEOUT ="sms.chuanglan.code-time-out";

    /**
     * token
     */
    String JWT_TOKEN_VALID_TIME = "jwt.token.valid-time";
    String JWT_TOKEN_STAR_SECRET ="jwt.token.star-secret";
    String JWT_TOKEN_CUSTOMER_SECRET="jwt.token.customer-secret";
}
