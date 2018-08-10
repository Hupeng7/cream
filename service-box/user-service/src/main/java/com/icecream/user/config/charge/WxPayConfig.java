package com.icecream.user.config.charge;

/**
 * @author Mr_h
 * @version 1.0
 * description: 微信支付配置类
 * create by Mr_h on 2018/8/8 0008
 */
public class WxPayConfig {

    /**
     * 预支付请求地址
     */
    public static final String  PrepayUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 商户APPID
     */
    public static final String  AppId = "wx98460a75fbd4b6c0";

    /**
     * 商户账户 获取支付能力后，从邮件中得到
     */
    public static final String  MchId = "1493054912";

    /**
     * 商户秘钥  32位，在微信商户平台中设置
     */
    public static final String  AppSercret = "715fab3d2a5a03f16cacb56deabc8081";

    /**
     * 服务器异步通知页面路径
     */
    public static String notify_url = "www.baidu.com";

    /**
     * 调用api的秘钥
     */
    public static String api_key = "XfPRXUoOgXl6KWHb3CJw59V96ovUGqiV";

}
