package com.icecream.order.mapper;


import com.icecream.common.model.pojo.Order;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

@org.apache.ibatis.annotations.Mapper
public interface OrderMapper extends Mapper<Order>, MySqlMapper<Order> { }
