package com.icecream.goods.rabbitmq.sender;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Mr_h
 * @version 1.0
 * description: 队列发送
 * create by Mr_h on 2018/9/11 0011
 */
@Component
public class Sender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send(String exchangeName,String routingKey,String msg){
      rabbitTemplate.convertAndSend(exchangeName,routingKey,msg);
    }

}
