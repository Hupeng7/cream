package com.icecream.order.rabbitmq.receiver;

import com.alibaba.fastjson.JSON;
import com.codingapi.tx.annotation.TxTransaction;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.model.model.GoodsUpdateMessage;
import com.icecream.common.model.model.SkillUpdateModel;
import com.icecream.common.util.constant.SysConstants;
import com.icecream.common.util.time.DateUtil;
import com.icecream.order.feignclient.GoodsFeignClient;
import com.icecream.order.mapper.ExpMapper;
import com.icecream.order.mapper.WalletMapper;
import com.icecream.order.service.OrderService;
import com.icecream.order.service.PointInoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.icecream.common.util.constant.SysConstants.ORDER_QUEUE;

/**
 * @author Mr_h
 * @version 1.0
 * description:队列的消费者
 * create by Mr_h on 2018/8/9 0009
 */
@Slf4j
@Component
@SuppressWarnings("all")
@RabbitListener(queues = ORDER_QUEUE)
public class OrderQueueReceiver {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void process(String msg) {
        log.info("Receiver  : " + msg);
        SkillUpdateModel skillUpdateModel = JSON.parseObject(msg, SkillUpdateModel.class);
        GoodsUpdateMessage goodsUpdateMessage = skillUpdateModel.getGoodsUpdateMessage();
        Order order = skillUpdateModel.getOrder();
        orderService.preInserOrder(order);
        orderService.distributedTransactionInserting(goodsUpdateMessage,order);
    }

}
