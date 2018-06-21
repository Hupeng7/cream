package com.icecream.user.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Mr_h
 * @version 1.0
 * description: redis配置类
 * create by Mr_h on 2018/6/19 0019
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisConfigProperties {
    //   主机地址
    public String host;
    //端口
    public int port;
    //密码没有不填写
    public String password;
    // Redis数据库索引（默认为0）
    public int database;
    //最大活跃连接
    public int maxActive;
    //连接池最大阻塞等待时间（使用负值表示没有限制）
    public int maxWait;
    //连接池中的最大空闲连接
    public int maxIdle;
    //连接池中的最小空闲连接
    public int minIdle;
    //连接超时时间（毫秒）
    public int timeOut;
}
