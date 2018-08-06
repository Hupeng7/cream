package com.icecream.order.service;

import com.icecream.common.model.pojo.ScoreRule;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.order.mapper.ScoreRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/3 0003
 */
@Service
@SuppressWarnings("all")
public class ChargeMealService {


    @Autowired
    private ScoreRuleMapper scoreRuleMapper;

    public ResultVO getMeal(){
        ScoreRule scoreRule = new ScoreRule();
        //设置为充值套餐类型
        scoreRule.setCategoryId(6);
        //设置为启用状态
        scoreRule.setStatus(1);
        List<ScoreRule> select = scoreRuleMapper.select(scoreRule);
        return ResultUtil.success(select);
    }
}
