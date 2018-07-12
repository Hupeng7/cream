package com.icecream.good.mapper;

import com.icecream.common.model.pojo.Good;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author Mr_h
 * @version 1.0
 * description:商品服务mapper
 * create by Mr_h on 2018/7/9 0009
 */
@Mapper
public interface GoodMapper extends tk.mybatis.mapper.common.Mapper<Good>,MySqlMapper<Good> {
}
