package com.icecream.order.mapper;

import com.icecream.common.model.pojo.UserExp;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/15 0015
 */
@Mapper
public interface ExpMapper extends tk.mybatis.mapper.common.Mapper<UserExp>, MySqlMapper<UserExp> {
}
