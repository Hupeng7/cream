package com.icecream.good.mapper;

import com.icecream.common.model.pojo.DiscoverGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscoverGoodsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DiscoverGoods record);

    int insertSelective(DiscoverGoods record);

    DiscoverGoods selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DiscoverGoods record);

    int updateByPrimaryKey(DiscoverGoods record);

    List<DiscoverGoods> selectGoodsIdByDiscoverId(@Param("discoverId") Integer discoverId,
                                                  @Param("frontScore")Integer score);

}