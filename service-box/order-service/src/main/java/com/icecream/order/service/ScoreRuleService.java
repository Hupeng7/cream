package com.icecream.order.service;

import com.icecream.common.model.pojo.ScoreRule;
import com.icecream.order.mapper.ScoreRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.icecream.order.contants.Contants.SIGN_IN;

/**
 * @author Mr_h
 * @version 1.0
 * description: 积分规则表
 * create by Mr_h on 2018/8/7 0007
 */
@Service
@SuppressWarnings("all")
public class ScoreRuleService {

    @Autowired
    private ScoreRuleMapper scoreRuleMapper;

    //获取签到规则信息
    public List<ScoreRule> getRule(){
        ScoreRule scoreRule = new ScoreRule();
        scoreRule.setCategoryId(SIGN_IN);
        return scoreRuleMapper.select(scoreRule);
    }

    //获取签到信息的map映射表 规则名为键，+/-积分作为值
    public Map<String,BigDecimal> getRuleMapping(){
        Map<String, BigDecimal> mapping = new HashMap<>();
        List<ScoreRule> rule = getRule();
        rule.forEach(s -> mapping.put(s.getDispalyName(), s.getPrice()));
        return mapping;
    }
}
