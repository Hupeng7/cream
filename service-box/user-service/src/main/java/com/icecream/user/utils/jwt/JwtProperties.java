package com.icecream.user.utils.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/21 0021
 */
@Data
@ConfigurationProperties("token")
@Component
public class JwtProperties {

    private long validTime;
    private String starSecret;
    private String customerSecret;

}
