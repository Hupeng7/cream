package com.icecreamGroup.major;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author mr_h
 * @version 1.0
 * 配置中心
 * 拉取远程仓库git/gitlab文件属性
 * 动态加载到本地${}格式的属性上
 */
@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigServerApplication {

    public static void main(String[] args){SpringApplication.run(ConfigServerApplication.class); }
}
