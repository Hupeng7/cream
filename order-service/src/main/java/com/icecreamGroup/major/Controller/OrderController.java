package com.icecreamGroup.major.Controller;

import com.icecreamGroup.major.FeignClient.CommentsClient;
import com.icecreamGroup.major.Model.Order;
import com.icecreamGroup.major.Service.OrderService;
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
    public void insert(){
        int count = orderService.insert();
        if(count>0)
            log.info("order插入成功");
            log.error("order插入失败");
    }

}
