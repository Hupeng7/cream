package com.icecreamGroup.major.Mapper;

import com.icecreamGroup.major.Model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {

    Order get(@Param("order_no") String orderNo);

    int insertSelective(Order order);
}
