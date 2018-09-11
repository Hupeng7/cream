package com.icecream.order.rabbitmq;

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
 * description: 订单系统队列
 * create by Mr_h on 2018/8/10 0010
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 订单队列
     *
     * @return
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE);
    }

    /**
     * 充值队列
     * @return
     */
    @Bean
    public Queue chargeQueue() {
        return new Queue(CHARGE_QUEUE);
    }

    /**
     * 声明交换机
     *
     * @return
     */
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    /**
     * 绑定队列
     *
     * @param orderQueue 订单队列
     * @param exchange     交换机
     * @return
     */
    @Bean
    Binding bindingExchangeMessageForCommentQueue(Queue orderQueue, DirectExchange exchange) {
        return BindingBuilder.bind(orderQueue).to(exchange).with(ORDER_QUEUE);
    }

    /**
     * 绑定队列
     *
     * @param chargeQueue 充值队列
     * @param exchange             交换机
     * @return
     */
    @Bean
    Binding bindingExchangeMessageBForHeadlineQueue(Queue chargeQueue, DirectExchange exchange) {
        return BindingBuilder.bind(chargeQueue).to(exchange).with(CHARGE_QUEUE);
    }

}