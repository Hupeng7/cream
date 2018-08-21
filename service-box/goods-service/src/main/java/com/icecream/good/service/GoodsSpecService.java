package com.icecream.good.service;

import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.common.model.requstbody.CreateOrderModel;
import com.icecream.good.mapper.GoodsSpecMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr_h
 * @version 1.0
 * description: 商品规格表
 * create by Mr_h on 2018/8/16 0016
 */
@Service
@SuppressWarnings("all")
public class GoodsSpecService {

    @Autowired
    private GoodsSpecMapper goodsSpecMapper;

    public GoodsSpec get(String id){
        return goodsSpecMapper.selectByPrimaryKey(id);
    }

    public List<GoodsSpec> getSpecList(String goodsSn){
        GoodsSpec goodsSpec = new GoodsSpec();
        goodsSpec.setGoodsSn(goodsSn);
        return goodsSpecMapper.select(goodsSpec);
    }


    public int update(GoodsSpec goodsSpec){return goodsSpecMapper.updateByPrimaryKeySelective(goodsSpec);}

    public int inventoryReduction(Integer count,String id){return goodsSpecMapper.reduceSpecStock(count,id);}

}
