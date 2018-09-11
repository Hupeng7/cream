package com.icecream.goods.rabbitmq.receiver;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.model.GoodsUpdateMessage;
import com.icecream.goods.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.icecream.common.util.constant.SysConstants.GOODS_QUEUE;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/9/11 0011
 */
@Slf4j
@Component
@RabbitListener(queues = GOODS_QUEUE)
public class GoodsQueueReceiver {

    @RabbitHandler
    public void process(String msg) {
        log.info("Receive" + msg);
    }
}

