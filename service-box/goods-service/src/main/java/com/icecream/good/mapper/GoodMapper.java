package com.icecream.good.mapper;

import com.icecream.common.model.pojo.Good;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodMapper {
    int deleteByPrimaryKey(String id);

    int insert(Good good);

    int insertSelective(Good record);

    Good selectByPrimaryKey(String id);

    Good selectByPrimaryKeySimpleInfo(String id);

    int updateByPrimaryKeySelective(Good record);

    int updateByPrimaryKey(Good record);

    List<Good> select(Good good);
}