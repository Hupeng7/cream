package com.icecream.order.controller;

import com.icecream.common.model.pojo.ScoreRule;
import com.icecream.common.util.res.ResultVO;
import com.icecream.order.service.ScoreRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/10 0010
 */
@RestController()
@RequestMapping("rule")
public class ScoreRuleController {

    @Autowired
    private ScoreRuleService scoreRuleService;

    @GetMapping("get")
    public ScoreRule getRule(Integer type, BigDecimal changePrice, Integer status){
        return scoreRuleService.getRuleForCreateOrder(type,changePrice,status);
    }
}
