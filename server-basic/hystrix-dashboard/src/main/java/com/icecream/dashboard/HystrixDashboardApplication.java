package com.icecream.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;


/**
 * @author mr_h
 * 监控平台
 * @version 1.0
 */
@SpringBootApplication
@EnableHystrixDashboard
public class HystrixDashboardApplication {
   public static void main(String[] args){SpringApplication.run(HystrixDashboardApplication.class); }
}
