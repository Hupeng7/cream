package com.icecream.goods.mapper;

import com.icecream.common.model.pojo.GoodsLimit;
import org.apache.ibatis.annotations.*;

@Mapper
public interface GoodsLimitMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsLimit record);

    int insertSelective(GoodsLimit record);

    GoodsLimit selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsLimit record);

    int updateByPrimaryKey(GoodsLimit record);

    GoodsLimit selectByGoodsSnAndUid(@Param("goodsSn")String goodsSn,@Param("uid")Integer uid);

    int updateGoodsCount(@Param("sid")Integer sid, @Param("uid")Integer uid,
                         @Param("goods_sn")String goodsSn, @Param("goods_count")Integer goodsCount,
                         @Param("ctime")Integer ctime);
}