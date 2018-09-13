package com.icecream.user;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * @author  mr_h
 * @version 1.0
 * 用户服务
 */
@EnableFeignClients//开启feign客户端远程调用
@SpringCloudApplication
@EnableConfigurationProperties//开启自定义配置属性扫描
@ComponentScan(basePackages = {"com.icecream.common","com.icecream.user"})//托管类扫描路径
public class UserServiceApplication {


    //注入可以手动设定固定网址（如第三方登录、第三方支付）的httpClient对象
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


    public static void main(String[] args){
        SpringApplication.run(UserServiceApplication.class);
    }
}
