package com.icecreamGroup.user.controller;

import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.user.service.UserService;
import com.icecreamGroup.user.feignClients.CommentsClient;
import com.icecreamGroup.user.feignClients.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private CommentsClient commentsClient;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private UserService userService;

    @RequestMapping("user-order/{orderNo}")
    public Order selectOrderByUserId(@PathVariable("orderNo")String orderNo){
        return orderFeignClient.getOrderByOrderNo(orderNo);
    }

    @RequestMapping("zuul")
    public String zuul(){
        return "zuul 测试2";
    }

    @RequestMapping("user-comment")
    public String selectCommentList(){
        return commentsClient.backComments();
    }

    @RequestMapping("tx")
    public void insert(){
        userService.insert();
    }


}
