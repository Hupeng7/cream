package com.icecream.comment.rabbitmq.receiver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.icecream.common.util.constant.SysConstants.COMMENT_HEADLINE_QUEUE;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/9/11 0011
 */
@Slf4j
@Component
@RabbitListener(queues = COMMENT_HEADLINE_QUEUE)
public class HeadlineQueueReceiver {

    @RabbitHandler
    public void process(String msg) {
        log.info("这里是评论头条队列，收到消息: [" + msg+"]");
    }
}
