package com.icecream.good;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.icecream.common.util.time.DateUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * @author  mr_h
 * @version 1.0
 * 商品服务
 */
@EnableFeignClients
@SpringCloudApplication
@EnableConfigurationProperties
@ComponentScan(basePackages = {"com.icecream.common","com.icecream.good"})
public class GoodServiceApplication {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args){
        JSON.DEFFAULT_DATE_FORMAT = DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS;
        SpringApplication.run(GoodServiceApplication.class);
    }
}
