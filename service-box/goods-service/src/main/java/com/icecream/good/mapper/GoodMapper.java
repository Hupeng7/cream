package com.icecream.good.mapper;

import com.icecream.common.model.pojo.Good;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodMapper {
    int deleteByPrimaryKey(String id);

    int insert(Good good);

    int insertSelective(Good record);

    Good selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Good record);

    int updateByPrimaryKey(Good record);
}