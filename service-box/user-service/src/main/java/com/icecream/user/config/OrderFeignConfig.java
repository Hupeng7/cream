package com.icecream.user.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Mr_h
 * {@link Logger.Level}
 * NONE:不打印日志（默认）
 * BASIC:只打印基本信息
 * HEADERS:打印http头部信息和基本信息
 * FULL:打印全部信息包括返回值
 * order Feign的配置类，它用于打印请求日志
 * create by 2018/5/28 0028
 */
@Configuration
public class OrderFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
