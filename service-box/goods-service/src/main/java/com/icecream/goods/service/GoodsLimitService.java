package com.icecream.goods.service;

import com.icecream.common.model.pojo.GoodsLimit;
import com.icecream.common.util.time.DateUtil;
import com.icecream.goods.mapper.GoodsLimitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private GoodsService goodsService;

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
