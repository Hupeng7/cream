package com.icecream.good.controller;

import com.icecream.common.redis.RedisHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr_h
 * @version 1.0
 * description:商品服务controller
 * create by Mr_h on 2018/7/9 0009
 */
@Slf4j
@RestController
@RequestMapping("good")
public class GoodController {


    @GetMapping("redis")
    public void redisTest(){
        RedisHandler.set("wo","mr_h");
        log.info("redis---->"+RedisHandler.get("wo"));
    }

    @GetMapping("test")
    public String wo(){
        return "wo";
    }
}
