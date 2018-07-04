package com.icecream.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author mr_h
 * @version 1.0
 * 订单服务
 * EnableFeignClients开启feign客户端
 * ServletComponentScan 开启ServletComponentScan扫描
 * SpringCloudApplication 开启支持springCloud
 * create by mr_h 2018/6/6
 */
@EnableFeignClients
@ServletComponentScan
@SpringBootApplication
@EnableHystrix
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.icecream.common","com.icecream.order"})
public class OrderServiceApplication {

    public static void main(String[] args){SpringApplication.run(OrderServiceApplication.class,args);}
}
