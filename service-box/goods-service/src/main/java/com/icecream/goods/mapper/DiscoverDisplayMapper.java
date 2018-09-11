package com.icecream.goods.mapper;

import com.icecream.common.model.pojo.DiscoverDisplay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscoverDisplayMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DiscoverDisplay record);

    int insertSelective(DiscoverDisplay record);

    DiscoverDisplay selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DiscoverDisplay record);

    int updateByPrimaryKey(DiscoverDisplay record);

    List<DiscoverDisplay> getSortList();

    DiscoverDisplay selectByDiscoverId(@Param("discoverId")Integer discoverId);
}