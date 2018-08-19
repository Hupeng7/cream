package com.icecream.good.mapper;

import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.requstbody.GoodsStoreModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodMapper {
    int deleteByPrimaryKey(String id);

    int insert(Good good);

    int insertSelective(Good record);

    Good selectByPrimaryKey(Integer id);

    Good selectByPrimaryKeySimpleInfo(Integer id);

    int updateByPrimaryKeySelective(Good record);

    int updateByPrimaryKey(Good record);

    List<Good> select(Good good);

    int reductionGoodsNum(@Param("count") Integer buyNum,@Param("goods_sn")String goodsSn,
                          @Param("sid")Integer sid);
    Good getGoodsNum(@Param("goods_sn")String goodsSn, @Param("sid")Integer sid);
}