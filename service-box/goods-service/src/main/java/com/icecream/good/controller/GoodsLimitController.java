package com.icecream.good.controller;

import com.icecream.common.model.pojo.Order;
import com.icecream.common.model.requstbody.CreateOrderModel;
import com.icecream.common.model.requstbody.GoodsStoreModel;
import com.icecream.good.service.GoodsLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
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
