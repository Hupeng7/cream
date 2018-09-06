package com.icecream.good.controller;

import com.icecream.good.service.GoodsLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/15 0015
 */
@RestController
@RequestMapping("Goods/limit")
public class GoodsLimitController {

    @Autowired
    private GoodsLimitService goodsLimitService;

}
