package com.icecream.order.rabbit;

import com.icecream.common.util.constant.SysConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mr_h
 * @version 1.0
 * description: rabbitMq的简单实现
 * create by Mr_h on 2018/8/10 0010
 */
@Configuration
public class RabbitMqConfig {


    //创建队列，名字为order-queue
    @Bean
    public Queue createGoodsQueue(){
        return new Queue(SysConstants.GOODS_ROUTING_KEY);
    }

    @Bean
    public Queue createOrderQueue(){
        return new Queue(SysConstants.ORDER_ROUTING_KEY);
    }
}
