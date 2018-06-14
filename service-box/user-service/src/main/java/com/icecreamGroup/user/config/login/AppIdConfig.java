package com.icecreamGroup.user.config.login;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Mr_h
 * @version 1.0
 * description: 第三方登陆配置类
 * create by Mr_h on 2018/6/14 0014
 */
@Data
@Component
@ConfigurationProperties("login")
public class AppIdConfig {

    private String QQappId;

    private String QQsecret;

    private String WechatAppId;

    private String WechatSecret;

    private String WeiboAppId;

    private String WeiboSecret;
}
