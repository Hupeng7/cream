package com.icecream.goods.mapper;

import com.icecream.common.model.pojo.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsMapper {
    int deleteByPrimaryKey(String id);

    int insert(Goods goods);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Integer id);

    Goods selectByPrimaryKeySimpleInfo(Integer id);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKey(Goods record);

    List<Goods> select(Goods goods);

    int reductionGoodsNum(@Param("count") Integer buyNum,@Param("goods_sn")String goodsSn,
                          @Param("sid")Integer sid);
    Goods getGoodsNum(@Param("goodsSn")String goodsSn, @Param("sid")Integer sid);

    List<Goods> getAll();

    int updateByGoodsSnAndGoodsNum(@Param("sid")Integer sid,@Param("goodsSn")String goodsSn
            ,@Param("goodsNum")Integer goodsNum);
}