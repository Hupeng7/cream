package com.icecreamGroup.common.util.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/21 0021
 */
@Data
@ConfigurationProperties("jwt.token")
@PropertySource(value = "classpath:jwt-config.yml")
public class JwtProperties {

    private long validTime;
    private String starSecret;
    private String customerSecret;

}
