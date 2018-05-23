package com.icecreamGroup.order.Service;

import com.codingapi.tx.annotation.ITxTransaction;
import com.icecreamGroup.order.Mapper.OrderMapper;
import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.order.utils.OrderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService implements ITxTransaction {

    @Autowired
    private OrderMapper orderMapper;

    public Order getOrderByOrderNo(String orderNo){
        return orderMapper.get(orderNo);
    }

    @Transactional
    public int insert(){
        return orderMapper.insertSelective(OrderBuilder.buildOrder());
    }
}
