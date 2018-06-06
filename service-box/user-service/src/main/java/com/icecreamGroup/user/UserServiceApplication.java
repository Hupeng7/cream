package com.icecreamGroup.user;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * @author  mr_h
 */
@EnableFeignClients
@SpringCloudApplication
public class UserServiceApplication {
    public static void main(String[] args){SpringApplication.run(UserServiceApplication.class);}
}
