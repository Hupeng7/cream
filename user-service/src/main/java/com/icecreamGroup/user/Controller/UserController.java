package com.icecreamGroup.user.Controller;

import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.user.feignClients.CommentsClient;
import com.icecreamGroup.user.feignClients.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private CommentsClient commentsClient;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @RequestMapping("user-order/{orderNo}")
    @ResponseBody
    public Order selectOrderByUserId(@PathVariable("orderNo")String orderNo){
        return orderFeignClient.getOrderByOrderNo(orderNo);
    }

    @RequestMapping("zuul")
    @ResponseBody
    public String zuul(){
        return "zuul 测试2";
    }

    @RequestMapping("user-comment")
    @ResponseBody
    public String selectCommentList(){
        return commentsClient.backComments();
    }
}
