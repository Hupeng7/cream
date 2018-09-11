package com.icecream.order.rabbitmq.receiver;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.util.constant.SysConstants;
import com.icecream.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.icecream.common.util.constant.SysConstants.CHARGE_QUEUE;

/**
 * @author Mr_h
 * @version 1.0
 * description:队列的消费者
 * create by Mr_h on 2018/8/9 0009
 */
@Slf4j
@Component
@RabbitListener(queues = CHARGE_QUEUE)
public class ChargeQueueReceiver {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void process(String json) {
        log.info("Receiver  : " + json);
        Order order = JSON.parseObject(json, Order.class);
        orderService.insert(order);
    }
}
