package com.icecream.good.service;

import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.good.mapper.GoodsSpecMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public int update(GoodsSpec goodsSpec){return goodsSpecMapper.updateByPrimaryKeySelective(goodsSpec);}

}
