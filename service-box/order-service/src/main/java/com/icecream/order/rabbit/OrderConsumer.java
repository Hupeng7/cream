package com.icecream.order.rabbit;

import com.alibaba.fastjson.JSON;
import com.codingapi.tx.annotation.TxTransaction;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.model.requstbody.GoodsUpdateMessage;
import com.icecream.common.model.requstbody.SkillUpdateModel;
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

/**
 * @author Mr_h
 * @version 1.0
 * description:队列的消费者
 * create by Mr_h on 2018/8/9 0009
 */
@Slf4j
@Component
@SuppressWarnings("all")
@RabbitListener(queues = {SysConstants.ORDER_ROUTING_KEY})
public class OrderConsumer {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ExpMapper expMapper;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private GoodsFeignClient goodsFeignClient;

    @Autowired
    private PointInoutService pointInoutService;

    @RabbitHandler
    @TxTransaction(isStart = true)
    @Transactional
    public void process(String json) {
        log.info("Receiver  : " + json);
        SkillUpdateModel skillUpdateModel = JSON.parseObject(json, SkillUpdateModel.class);
        Order order = skillUpdateModel.getOrder();
        GoodsUpdateMessage goodsUpdateMessage = skillUpdateModel.getGoodsUpdateMessage();
        Integer row1 = goodsFeignClient.updateGoodsNum(goodsUpdateMessage);
        Integer row2 = expMapper.concurrentInsertExp(order.getSid(), order.getUid(), order.getChangePrice().intValue(), DateUtil.getNowSecondIntTime());
        Integer row3 = walletMapper.reduceWalletBalance(order.getChangePrice(), order.getUid(), order.getSid());
        Integer row4 = orderService.insert(order);
        Integer row5 = pointInoutService.insertPointInoutOrder(order.getChangePrice(), order.getUid(), order.getOrderNo());
        log.info("更新状态，{},{},{},{},{}",row1,row2,row3,row4,row5);
        if(row1<0|row2<0|row3<0|row4<0&row5<0){
            throw new RuntimeException("事务更新失败，回滚");
        }
        //todo 插入错误数据表
    }
}
