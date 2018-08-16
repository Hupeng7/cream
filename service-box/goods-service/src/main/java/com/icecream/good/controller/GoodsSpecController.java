package com.icecream.good.controller;

import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.good.service.GoodsSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/16 0016
 */
@RestController
@RequestMapping("Goods/spec")
public class GoodsSpecController {

    @Autowired
    private GoodsSpecService goodsSpecService;

    @RequestMapping("get")
    public GoodsSpec getSpec(String specId){
       return goodsSpecService.get(specId);
    }
}
