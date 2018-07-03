package com.icecream.order.controller;

import com.icecream.common.model.pojo.Order;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.order.service.OrderService;
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

    //测试查询分表后的全部订单
    @RequestMapping("/list")
    public ResultVO getOrderByOrderNo(){
        return ResultUtil.success(orderService.list());
    }

    //测试订单插入
    @RequestMapping("insert")
    public int insert(){
        return orderService.insert();
    }



}
