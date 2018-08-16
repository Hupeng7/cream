package com.icecream.good.service;

import com.codingapi.tx.annotation.ITxTransaction;
import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.pojo.GoodsLimit;
import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.common.model.requstbody.CreateOrderModel;
import com.icecream.common.util.time.DateUtil;
import com.icecream.good.mapper.GoodsLimitMapper;
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
public class GoodsLimitService implements ITxTransaction {

    @Autowired
    private GoodsLimitMapper goodsLimitMapper;

    @Autowired
    private GoodService goodService;

    @Autowired
    private GoodsSpecService goodsSpecService;

    public int update(String goodsSn, Integer buyNum, Integer sid, Integer uid, String specId) {
        Good good = goodService.get(goodsSn);
        Integer goodsNum = good.getGoodsNum();
        goodsNum -= buyNum;
        good.setGoodsNum(goodsNum);
        int specRow = 0;
        if (null != specId) {
            GoodsSpec goodsSpec = goodsSpecService.get(specId);
            goodsSpec.setStock(goodsNum);
            goodsSpec.setGoodsSn(goodsSn);
            specRow = goodsSpecService.update(goodsSpec);
        }
        int goodsRow = goodService.update(good);
        GoodsLimit userLimit = goodsLimitMapper.selectByGoodsSn(goodsSn);
        if (null != userLimit) {
            Integer goodsCount = userLimit.getGoodsCount();
            goodsCount += buyNum;
            userLimit.setGoodsCount(goodsCount);
            int limitRow = goodsLimitMapper.updateByPrimaryKey(userLimit);
            if (specRow == 0) {
                if (goodsRow > 0 & limitRow > 0) {
                    return limitRow | goodsRow | specRow;
                }
            } else {
                if (goodsRow > 0 & limitRow > 0 & specRow > 0) {
                    return limitRow | goodsRow | specRow;
                }
            }
        } else {
            GoodsLimit goodsLimit = buildGoodsLimit(goodsSn, buyNum, sid, uid);
            int insert = insert(goodsLimit);
            if (specRow == 0) {
                if (goodsRow > 0 & insert > 0) {
                    return insert | goodsRow;
                }
            } else {
                if (goodsRow > 0 & insert > 0 & specRow > 0) {
                    return insert | goodsRow | specRow;
                }
            }
        }
        return 0;
    }

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

    public boolean checkCount(CreateOrderModel createOrderModel) {
        Integer goodsCount = 0;
        Integer store = 0;
        String goodsSn = createOrderModel.getGoodsSn();
        Integer buyNum = createOrderModel.getGoodsCount();
        String specId = createOrderModel.getSpecId();
        GoodsLimit userLimit = goodsLimitMapper.selectByGoodsSn(goodsSn);
        Good good = goodService.get(goodsSn);
        //如果是多规格商品
        if (null != specId) {
            GoodsSpec goodsSpec = goodsSpecService.get(createOrderModel.getSpecId());
            //多规格商品的库存
            store = goodsSpec.getStock();
        } else {
            //单规格商品的库存
            store = good.getGoodsNum();
        }
        //如果下的订单商品数量大于库存不允许购买
        if (createOrderModel.getGoodsCount() > store) {
            return false;
        }
        //每个用户能够购买的数量
        Integer buylimit = good.getBuylimit();
        //用户已经购买的数量
        if (userLimit != null) {
            goodsCount = userLimit.getGoodsCount();
        }
        //当前用户还能购买的件数
        Integer canBuyNum = buylimit - goodsCount;
        if (buylimit < buyNum | canBuyNum < buyNum) {
            return false;
        }
        return true;
    }
}
