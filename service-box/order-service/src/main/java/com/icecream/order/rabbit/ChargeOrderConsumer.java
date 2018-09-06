package com.icecream.order.rabbit;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.util.constant.SysConstants;
import com.icecream.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Mr_h
 * @version 1.0
 * description:队列的消费者
 * create by Mr_h on 2018/8/9 0009
 */
@Slf4j
@Component
@RabbitListener(queues = {SysConstants.CHARGE_ROUTING_KEY})
public class ChargeOrderConsumer {

    @Autowired
    private OrderService orderService;


    @RabbitHandler
    public void process(String json) {
        log.info("Receiver  : " + json);
        Order order = JSON.parseObject(json, Order.class);
        orderService.insert(order);
    }
}
