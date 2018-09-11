package com.icecream.comment.rabbitmq.receiver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.icecream.common.util.constant.SysConstants.COMMENT_CHANNEL_QUEUE;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/9/11 0011
 */
@Slf4j
@Component
@RabbitListener(queues =COMMENT_CHANNEL_QUEUE)
public class ChannelQueueReceiver {

    @RabbitHandler
    public void process(String msg) {
        log.info("这里是评论频道队列，收到消息: [" + msg+"]");
    }
}
