package com.icecream.good.mapper;

import com.icecream.common.model.pojo.GoodsSpec;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface GoodsSpecMapper {
    int deleteByPrimaryKey(String id);

    int insert(GoodsSpec record);

    int insertSelective(GoodsSpec record);

    GoodsSpec selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoodsSpec record);

    int updateByPrimaryKey(GoodsSpec record);

    int batchInsert(@Param("list") List<GoodsSpec> goodsSpecList);
}