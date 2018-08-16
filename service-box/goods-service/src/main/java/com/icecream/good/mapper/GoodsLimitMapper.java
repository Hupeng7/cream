package com.icecream.good.mapper;

import com.icecream.common.model.pojo.GoodsLimit;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface GoodsLimitMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsLimit record);

    int insertSelective(GoodsLimit record);

    GoodsLimit selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsLimit record);

    int updateByPrimaryKey(GoodsLimit record);

    GoodsLimit selectByGoodsSn(@Param("goodsSn")String goodsSn);
}