package com.icecream.order.controller;

import com.icecream.common.util.res.ResultVO;
import com.icecream.order.service.ChargeMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/3 0003
 */
@RestController
@RequestMapping("charge/meal")
public class ChargeMealController {


    @Autowired
    private ChargeMealService chargeMealService;

    /**
     * 用户获取充值星星的套餐列表
     * @return resultVo
     */
    @GetMapping("get")
    public ResultVO getMeal(){
        return chargeMealService.getMeal();
    }
}
