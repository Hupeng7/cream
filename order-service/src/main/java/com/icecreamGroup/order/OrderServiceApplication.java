package com.icecreamGroup.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * @author mr_h
 * 订单服务
 */
@SpringBootApplication
@EnableDiscoveryClient
@ServletComponentScan
@EnableHystrix
@EnableFeignClients
public class OrderServiceApplication {

    public static void main(String[] args){SpringApplication.run(OrderServiceApplication.class,args);}
}
