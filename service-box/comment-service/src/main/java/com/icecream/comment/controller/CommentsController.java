package com.icecream.comment.controller;

import com.alibaba.fastjson.JSON;
import com.icecream.comment.model.Address;
import com.icecream.comment.model.User;
import com.icecream.comment.redis.RedisHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mr_h
 * @version 1.0
 * 评论相关api
 */
@Slf4j
@RestController
@RequestMapping("comment")
public class CommentsController {

    @Autowired
    private RedisHandler redisHandler;


    @RequestMapping("go")
    public String testRedis() {
        String key = "mr_h";
        redisHandler.set(key, 1);
        Object o = redisHandler.get(key);
        if(o!=null)
            log.info(o.toString());
        User user = getUser();
        String hsshKey = "hash";
        String fieldKey = "field";
        redisHandler.addMap(hsshKey,fieldKey,JSON.toJSONString(user));
        Object mapField = redisHandler.getMapField(hsshKey, fieldKey);
        if(mapField!=null){
            User result = JSON.parseObject(mapField.toString(), User.class);
            log.info("取出hash的值----{}",result);
        }
        return "ok";
    }

    @RequestMapping("hi")
    @Cacheable(value = "comment",key = "comment2")
    public String backComments() {
        log.info("进来了");
        return "ok";
    }


    @RequestMapping("cache/{headline}")
    @Cacheable(value = "comment", key = "'comment'.concat(#headline.toString())")
    public User springBootCache(@PathVariable("headline") Integer headline) {
        log.info("findUserById query from db, id: {}", headline);
        User user = getUser();
        return user;
    }

    private User getUser() {
        User user = new User();
        user.setName("mr_h");
        user.setAge(999);
        Address address = new Address();
        address.setAddress("北京");
        address.setDoorNum(601);
        Address address1 = new Address();
        address1.setAddress("上海");
        address1.setDoorNum(401);
        List<Address> list = new ArrayList<>();
        list.add(address);
        list.add(address1);
        user.setAddressList(list);
        return user;
    }

}
