package com.icecreamGroup.order.controller;

import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RefreshScope
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    //测试查询订单
    @RequestMapping("/detail/{orderNo}")
    public Order getOrderByOrderNo(@PathVariable("orderNo") String orderNo){
        return orderService.getOrderByOrderNo(orderNo);
    }

    //测试订单插入
    @RequestMapping("insert")
    public int insert(){
        return orderService.insert();
    }



}
