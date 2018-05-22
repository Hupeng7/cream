package com.icecreamGroup.major.Service;

import com.icecreamGroup.major.Mapper.OrderMapper;
import com.icecreamGroup.major.Model.Order;
import com.icecreamGroup.major.utils.OrderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public Order getOrderByOrderNo(String orderNo){
        return orderMapper.get(orderNo);
    }

    public int insert(){
        return orderMapper.insertSelective(OrderBuilder.buildOrder());
    }



}
