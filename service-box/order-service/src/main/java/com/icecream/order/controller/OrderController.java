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

    @RequestMapping("/{sid}/{order_no}")
    public Order getOrderByOrderNo(@PathVariable("sid")Integer sid,@PathVariable("order_no") String orderNo){
        return orderService.getOrderByOrderNo(sid,orderNo);
    }

    //测试订单插入
    @RequestMapping("insert")
    public int insert(){
        return orderService.insert();
    }



}
