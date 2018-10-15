package com.icecream.comment.controller;

import com.alibaba.fastjson.JSON;
import com.icecream.comment.model.Address;
import com.icecream.comment.model.User;
import com.icecream.comment.rabbitmq.sender.Sender;
import com.icecream.comment.redis.RedisHandler;
import com.icecream.comment.service.CommentsService;
import com.icecream.common.model.model.SendCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

import static com.icecream.common.util.constant.SysConstants.*;

/**
 * @author mr_h
 * @version 1.0
 * 评论相关api
 */
@Slf4j
@RestController
@Validated
@RequestMapping("comment")
public class CommentsController {

    @Autowired
    private RedisHandler redisHandler;

    @Autowired
    private Sender sender;

    @Autowired
    private CommentsService commentsService;

    @RequestMapping("go")
    public String testRedis() {
        String key = "mr_h";
        redisHandler.set(key, 1);
        Object o = redisHandler.get(key);
        if (o != null)
            log.info(o.toString());
        User user = getUser();
        String hsshKey = "hash";
        String fieldKey = "field";
        redisHandler.addMap(hsshKey, fieldKey, JSON.toJSONString(user));
        Object mapField = redisHandler.getMapField(hsshKey, fieldKey);
        if (mapField != null) {
            User result = JSON.parseObject(mapField.toString(), User.class);
            log.info("取出hash的值----{}", result);
        }
        return "ok";
    }

    @RequestMapping("hi")
    public String backComments() {
        log.info("进来了");
        return "ok";
    }

    @RequestMapping("cache/{headline}")
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

    /**
     *
     * 队列发送与接收栗子
     *
     * @return
     */
    @RequestMapping("queue")
    public String toQueue() {
        String msg1 = "hello,world,这里是评论系统，这条消息将发往评论主队列";
        String msg2 = "hello,world,这里是评论系统，这条消息将发往评论频道队列";
        String msg3 = "hello,world,这里是评论系统，这条消息将发往评论头条队列";
        sender.send(COMMENT_EXCHANGE, COMMENT_QUEUE, msg1);
        sender.send(COMMENT_EXCHANGE, COMMENT_CHANNEL_QUEUE, msg2);
        sender.send(COMMENT_EXCHANGE, COMMENT_HEADLINE_QUEUE, msg3);
        return "Send complete";
    }

    /**
     * feign调用外网api
     * get请求栗子
     *
     *
     * @return
     */
    @GetMapping("url")
    public String call() {
        String token = "stareyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOi0yMDMsImV4cCI6MTU1MTk5MjI0NSwibmJmIjoxNTM2MjEzNzgyfQ.ZIE8kVDIi1FGsb5NEoGmV4jDeI5Rt-2s3NVggqqBcWU";
        String call = commentsService.getCallData(token);
        return call;
    }

    /**
     * feign调用外网api
     * 复杂post请求栗子
     *
     * @param sendCode
     * @return
     */
    @PostMapping("url2")
    public String call2(@RequestBody SendCode sendCode) {
        String token = "stareyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOi0yMDMsImV4cCI6MTU1MTk5MjI0NSwibmJmIjoxNTM2MjEzNzgyfQ.ZIE8kVDIi1FGsb5NEoGmV4jDeI5Rt-2s3NVggqqBcWU";
        String call = commentsService.getCallData2(token, sendCode);
        return call;
    }


    /**
     * 参数校验
     */
    @GetMapping("verify")
    public String verify(@NotBlank(message = "count参数不能为空") String count,
                         @NotBlank(message = "comments参数不能为空")String comments,
                         //@NotNull(message = "start参数不能为空")@AssertTrue(message = "非法开关1")Boolean start,
                         @NotNull(message = "max参数不能为空") @Max(value = 10,message = "不能超过最大值")Integer max,
                         @NotNull(message = "min参数不能为空")@Min(value = 1,message = "不能小于最小值")Integer min,
                         @NotBlank(message = "手机号为空")@Pattern(regexp = "^\\d" +
                                 "{11}$", message = "手机号码格式错误")String phone){
        return "ok"+count+comments+max+min;
    }
    @GetMapping("test/{name}")
    public String checkParam(@Pattern(regexp = "^[a-zA-Z_]\\w{4,19}$", message = "用户名必须以字母下划线开头，可由字母数字下划线组成") @PathVariable("name") String name) {
        log.info("进来了");
        return name;
    }

}
