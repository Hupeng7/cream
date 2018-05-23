package com.icecreamGroup.order.Controller;

import com.icecreamGroup.order.FeignClient.CommentsClient;
import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.order.Service.OrderService;
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

    @Autowired
    private CommentsClient commentsClient;

    @RequestMapping("comments")
    public String getComments(){
          return commentsClient.backComments();
    }

    @RequestMapping("zuul")
    public String zuul(){
        return "zuul 测试";
    }

    
    @RequestMapping("/detail/{orderNo}")
    public Order getOrderByOrderNo(@PathVariable("orderNo") String orderNo){

        return orderService.getOrderByOrderNo(orderNo);
    }

    /**
     * 测试订单插入
     */
    @RequestMapping("insert")
    public int insert(){
        return orderService.insert();

    }



}
