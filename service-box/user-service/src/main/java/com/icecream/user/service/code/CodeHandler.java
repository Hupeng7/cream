package com.icecream.user.service.code;

import com.icecream.common.model.model.SmsLoginOrRegisterParams;

/**
 * @version 2.0
 * 短信验证码
 */
public interface CodeHandler {

    /**
     * 发送短信的接口
     * @return json字符串
     */
    String send(String itucode, String phone);


    /**
     * 短信验证码验证
     * @return true 验证成功，false 验证失败
     */
    Boolean check(String key,Integer code);

    /**
     * 上一个方法的具象化
     */
    Boolean check(SmsLoginOrRegisterParams smsLoginOrRegisterParams);


    /**
     * 发送短信时验证手机号是否被注册过
     * @return json字符串
     */
    Boolean check(String itucode, String phone);

}
