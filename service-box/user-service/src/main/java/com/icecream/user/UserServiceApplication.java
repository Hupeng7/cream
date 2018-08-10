package com.icecream.user;


import com.icecream.user.utils.charge.RSAUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author  mr_h
 * @version 1.0
 * 用户服务
 */
@EnableFeignClients//开启feign客户端远程调用
@EnableHystrix//开启断路器
@EnableDiscoveryClient//开启可被服务中心发现
@SpringBootApplication//springBoot项目，自动配置
@EnableConfigurationProperties//开启自定义配置属性扫描
@ComponentScan(basePackages = {"com.icecream.common","com.icecream.user"})//托管类扫描路径
public class UserServiceApplication {


    //注入可以手动设定固定网址（如第三方登录、第三方支付）的httpClient对象
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


    public static void main(String[] args) throws Exception{
        SpringApplication.run(UserServiceApplication.class);
    }
}
