package com.icecream.user.rabbitmq;

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
 * description: 用户队列
 * create by Mr_h on 2018/8/10 0010
 */
@Configuration
public class RabbitMqConfig {
    /**
     * 用户队列
     * @return
     */
    @Bean
    public Queue UserQueue() {
        return new Queue(USER_QUEUE);
    }

    /**
     * 声明交换机
     * @return
     */
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(USER_EXCHANGE);
    }

    /**
     * 绑定队列
     * @param userQueue 用户队列
     * @param exchange 交换机
     * @return
     */
    @Bean
    Binding bindingExchangeMessageForUserQueue(Queue userQueue, DirectExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with(USER_QUEUE);
    }
}
