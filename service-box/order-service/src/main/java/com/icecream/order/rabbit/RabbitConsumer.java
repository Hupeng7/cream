package com.icecream.order.rabbit;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.model.pojo.ScoreRule;
import com.icecream.common.util.constant.SysConstants;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.common.util.time.DateUtil;
import com.icecream.order.service.OrderService;
import com.icecream.order.service.ScoreRuleService;
import com.icecream.order.utils.OrderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.ORB;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.icecream.order.contants.Contants.CHARGE;

/**
 * @author Mr_h
 * @version 1.0
 * description:队列的消费者
 * create by Mr_h on 2018/8/9 0009
 */
@Slf4j
@Component
@RabbitListener(queues = SysConstants.ORDER_ROUTING_KEY)
public class RabbitConsumer {

    @Autowired
    private OrderService orderService;


    @RabbitHandler
    public void process(String json) {
        log.info("Receiver  : " + json);
        Order order = JSON.parseObject(json, Order.class);
        orderService.insert(order);
    }
}
