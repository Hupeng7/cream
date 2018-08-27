package com.icecream.good.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.codingapi.tx.annotation.ITxTransaction;
import com.codingapi.tx.annotation.TxTransaction;
import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.pojo.GoodsLimit;
import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.common.model.requstbody.GoodsUpdateMessage;
import com.icecream.common.util.constant.SysConstants;
import com.icecream.common.util.time.DateUtil;
import com.icecream.good.mapper.GoodMapper;
import com.icecream.good.mapper.GoodsLimitMapper;
import com.icecream.good.mapper.GoodsSpecMapper;
import com.icecream.good.service.GoodService;
import com.icecream.good.service.GoodsSpecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mr_h
 * @version 1.0
 * description: 减库存的消费者
 * create by Mr_h on 2018/8/21 0021
 */
@Slf4j
@Component
@SuppressWarnings("all")
@RabbitListener(queues = SysConstants.GOODS_ROUTING_KEY)
public class GoodsReduceNumConsumer implements ITxTransaction {

    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private GoodsLimitMapper goodsLimitMapper;

    @Autowired
    private GoodsSpecMapper goodsSpecMapper;

    @RabbitHandler
    @TxTransaction
    @Transactional(rollbackFor = Exception.class)
    public void process(String json) {
        log.info("Receiver  : " + json);
        GoodsUpdateMessage goodsUpdateMessage = JSON.parseObject(json, GoodsUpdateMessage.class);
        goodsLimitMapper.updateGoodsCount(goodsUpdateMessage.getSid(),
                goodsUpdateMessage.getUid(),goodsUpdateMessage.getGoodsSn()
                ,goodsUpdateMessage.getBought(),DateUtil.getNowSecondIntTime());
        if(goodsUpdateMessage.getSpecId()!=null){
            GoodsSpec goodsSpec = new GoodsSpec();
            goodsSpec.setId(goodsUpdateMessage.getSpecId());
            goodsSpec.setStock(goodsUpdateMessage.getGoodsNum());
            goodsSpecMapper.updateByPrimaryKeySelective(goodsSpec);
            Good result = goodMapper.getGoodsNum(goodsSpec.getGoodsSn(), goodsUpdateMessage.getSid());
            goodsUpdateMessage.setGoodsNum(result.getGoodsNum()-goodsUpdateMessage.getCount());
        }
        goodMapper.updateByGoodsSnAndGoodsNum(goodsUpdateMessage.getSid(),
                goodsUpdateMessage.getGoodsSn(),goodsUpdateMessage.getGoodsNum());

    }
}
