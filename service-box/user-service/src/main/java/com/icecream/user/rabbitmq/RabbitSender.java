package com.icecream.user.rabbitmq;

import com.icecream.common.util.constant.SysConstants;
import com.icecream.user.config.rabbitmq.RabbitMqConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author Mr_h
 * @version 1.0
 * description:消息发送者
 * create by Mr_h on 2018/8/9 0009
 */
@Component
public class RabbitSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send(String json) {
        this.rabbitTemplate.convertAndSend(SysConstants.ORDER_ROUTING_KEY, json);
    }

}
