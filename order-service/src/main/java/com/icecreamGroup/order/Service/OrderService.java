package com.icecreamGroup.order.Service;

import com.icecreamGroup.order.Mapper.OrderMapper;
import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.order.utils.OrderBuilder;
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
