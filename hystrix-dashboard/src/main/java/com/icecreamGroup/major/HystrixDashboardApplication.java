package com.icecreamGroup.major;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;


/**
 * @author mr_h
 * 监控平台
 * 实现实时的集群监控
 * @version 1.0
 */
@SpringBootApplication
@EnableHystrixDashboard
public class HystrixDashboardApplication {
   public static void main(String[] args){SpringApplication.run(HystrixDashboardApplication.class); }
}
