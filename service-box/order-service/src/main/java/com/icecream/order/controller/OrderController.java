package com.icecream.order.controller;

import com.icecream.common.model.pojo.Order;
import com.icecream.order.service.ChargeRecordService;
import com.icecream.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RefreshScope
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ChargeRecordService chargeRecordService;

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
