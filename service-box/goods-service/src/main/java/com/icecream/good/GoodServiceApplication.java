package com.icecream.good;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * @author  mr_h
 * @version 1.0
 * 商品服务
 */
@EnableFeignClients
@EnableHystrix
@EnableDiscoveryClient
@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan(basePackages = {"com.icecream.common","com.icecream.good"})
public class GoodServiceApplication {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


    public static void main(String[] args){SpringApplication.run(GoodServiceApplication.class);}
}
