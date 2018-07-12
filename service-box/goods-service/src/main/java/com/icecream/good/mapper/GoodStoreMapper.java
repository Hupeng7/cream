package com.icecream.good.mapper;

import com.icecream.common.model.pojo.GoodStore;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/12 0012
 */
@Mapper
public interface GoodStoreMapper extends tk.mybatis.mapper.common.Mapper<GoodStore>,MySqlMapper<GoodStore> {
}
