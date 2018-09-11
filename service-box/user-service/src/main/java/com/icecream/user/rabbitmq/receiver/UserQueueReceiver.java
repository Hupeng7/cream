package com.icecream.user.rabbitmq.receiver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.icecream.common.util.constant.SysConstants.USER_QUEUE;


/**
 * @author Mr_h
 * @version 1.0
 * description: 用户队列
 * create by Mr_h on 2018/9/11 0011
 */
@Slf4j
@Component
@RabbitListener(queues =USER_QUEUE)
public class UserQueueReceiver {

    @RabbitHandler
    public void process(String msg) {
        log.info("这里是用户队列，收到消息: [" + msg+"]");
    }
}
