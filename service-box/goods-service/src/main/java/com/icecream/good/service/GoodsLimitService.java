package com.icecream.good.service;

import com.codingapi.tx.annotation.ITxTransaction;
import com.codingapi.tx.annotation.TxTransaction;
import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.pojo.GoodsLimit;
import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.common.model.requstbody.CreateOrderModel;
import com.icecream.common.model.requstbody.GoodsStoreModel;
import com.icecream.common.util.time.DateUtil;
import com.icecream.good.mapper.GoodsLimitMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/15 0015
 */
@Service
@SuppressWarnings("all")
public class GoodsLimitService{

    @Autowired
    private GoodsLimitMapper goodsLimitMapper;

    @Autowired
    private GoodService goodService;

    @Autowired
    private GoodsSpecService goodsSpecService;


    public int insert(GoodsLimit goodsLimit) {
        return goodsLimitMapper.insert(goodsLimit);
    }

    private GoodsLimit buildGoodsLimit(String goodsSn, Integer buyNum, Integer sid, Integer uid) {
        GoodsLimit goodsLimit = new GoodsLimit();
        goodsLimit.setGoodsCount(buyNum);
        goodsLimit.setGoodsSn(goodsSn);
        goodsLimit.setCtime(DateUtil.getNowSecondIntTime());
        goodsLimit.setSid(sid);
        goodsLimit.setUid(uid);
        return goodsLimit;
    }

}
