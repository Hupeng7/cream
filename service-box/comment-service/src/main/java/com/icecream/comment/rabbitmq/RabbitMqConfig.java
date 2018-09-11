package com.icecream.comment.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.icecream.common.util.constant.SysConstants.*;

/**
 * @author Mr_h
 * @version 1.0
 * description: rabbitMq队列配置类
 * create by Mr_h on 2018/8/10 0010
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 评论队列
     * @return
     */
    @Bean
    public Queue commentQueue() {
        return new Queue(COMMENT_QUEUE);
    }

    /**
     * 头条队列
     * @return
     */
    @Bean
    public Queue commentHeadlineQueue() {
        return new Queue(COMMENT_HEADLINE_QUEUE);
    }

    /**
     * 频道队列
     * @return
     */
    @Bean
    public Queue commentChannelQueue() {
        return new Queue(COMMENT_CHANNEL_QUEUE);
    }


    /**
     * 声明交换机
     * @return
     */
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(COMMENT_EXCHANGE);
    }

    /**
     * 绑定队列
     * @param commentQueue 评论队列
     * @param exchange 交换机
     * @return
     */
    @Bean
    Binding bindingExchangeMessageForCommentQueue(Queue commentQueue, DirectExchange exchange) {
        return BindingBuilder.bind(commentQueue).to(exchange).with(COMMENT_QUEUE);
    }

    /**
     * 绑定队列
     * @param commentHeadlineQueue 头条队列
     * @param exchange 交换机
     * @return
     */
    @Bean
    Binding bindingExchangeMessageBForHeadlineQueue(Queue commentHeadlineQueue, DirectExchange exchange) {
        return BindingBuilder.bind(commentHeadlineQueue).to(exchange).with(COMMENT_HEADLINE_QUEUE);
    }

    /**
     * 绑定队列
     * @param commentChannelQueue 频道队列
     * @param exchange 交换机
     * @return
     */
    @Bean
    Binding bindingExchangeMessageBForChannelQueue(Queue commentChannelQueue, DirectExchange exchange) {
        return BindingBuilder.bind(commentChannelQueue).to(exchange).with(COMMENT_CHANNEL_QUEUE);
    }

}
