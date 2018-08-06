package com.icecream.order.mapper;

import com.icecream.common.model.pojo.ScoreRule;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

@Mapper
public interface ScoreRuleMapper extends tk.mybatis.mapper.common.Mapper<ScoreRule>,MySqlMapper<ScoreRule> {
}