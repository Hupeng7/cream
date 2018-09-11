package com.icecream.goods.rabbitmq;

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
 * description: rabbitMq的简单实现
 * create by Mr_h on 2018/8/10 0010
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 商品队列
     * @return
     */
    @Bean
    public Queue GoodsQueue() {
        return new Queue(GOODS_QUEUE);
    }

    /**
     * 声明交换机
     * @return
     */
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(GOODS_EXCHANGE);
    }


    /**
     * 绑定队列
     * @param commentHeadlineQueue 头条队列
     * @param exchange 交换机
     * @return
     */
    @Bean
    Binding bindingExchangeMessageBForGoodsQueue(Queue commentHeadlineQueue, DirectExchange exchange) {
        return BindingBuilder.bind(commentHeadlineQueue).to(exchange).with(GOODS_QUEUE);
    }
}
