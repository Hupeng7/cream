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

    public GoodsStoreModel checkCount(CreateOrderModel createOrderModel) {
        GoodsStoreModel goodsStoreModel = null;
        //商品库存
        Integer store = 0;
        //用户已经购买了多少件
        Integer yetNum = 0;
        //用户订单中的商品件数
        Integer buyNum = createOrderModel.getGoodsCount();
        //商品唯一标识
        String goodsSn = createOrderModel.getGoodsSn();
        //多规格标识
        String specId = createOrderModel.getSpecId();
        GoodsLimit userLimit = goodsLimitMapper.selectByGoodsSn(goodsSn);
        if (null != userLimit) {
            yetNum = userLimit.getGoodsCount();
        }
        Good result = goodService.get(goodsSn);
        //用户还能买多少件该商品
        Integer canBuyNum = result.getBuylimit() - yetNum;
        if (null == specId) {
            goodsStoreModel = goodService.reduceGoodsNumOrRollBack(createOrderModel);
            if (goodsStoreModel != null) {
                Integer buylimit = goodsStoreModel.getLimit();
                store = goodsStoreModel.getStore();
            }
        } else {
            goodsStoreModel = goodService.reduceGoodsSpecNumOrRollBack(createOrderModel);
            if (goodsStoreModel != null) {
                store = goodsStoreModel.getStore();
            }
        }
        if (store.intValue() >=0 & buyNum <= store & canBuyNum <= buyNum) {
            return goodsStoreModel;
        }
        return goodsStoreModel;
    }

}
