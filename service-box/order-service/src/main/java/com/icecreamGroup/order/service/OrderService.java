package com.icecreamGroup.order.service;

import com.codingapi.tx.annotation.ITxTransaction;
import com.icecreamGroup.order.mapper.OrderMapper;
import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.order.utils.builder.OrderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class OrderService implements ITxTransaction {

    @Autowired
    private OrderMapper orderMapper;

    public Order getOrderByOrderNo(String orderNo){
        Order order = new Order();
        order.setOrderNo(orderNo);
        Order order1 = orderMapper.selectOne(order);
        log.info("tk-mapper:{}"+order1);
        return order1;
    }

    @Transactional
    public int insert(){
        return orderMapper.insertSelective(OrderBuilder.buildOrder());
    }
}
