package com.icecream.good.mapper;

import com.icecream.common.model.pojo.GoodStore;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodStoreMapper {
    int deleteByPrimaryKey(String id);

    int insert(GoodStore record);

    int insertSelective(GoodStore record);

    GoodStore selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoodStore record);

    int updateByPrimaryKey(GoodStore record);
}