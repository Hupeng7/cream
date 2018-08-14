package com.icecream.order.service;

import com.codingapi.tx.annotation.ITxTransaction;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.util.idbuilder.staticfactroy.SnowflakeGlobalIdFactory;
import com.icecream.order.mapper.OrderMapper;
import com.icecream.order.utils.OrderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;


@Slf4j
@Service
@SuppressWarnings("all")
public class OrderService implements ITxTransaction {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SnowflakeGlobalIdFactory snowflakeGlobalIdFactory;

    public Order getOrderByOrderNo(Integer sid,String orderNo){
        Order order = new Order();
        order.setOrderNo(orderNo);
        return orderMapper.selectOne(order);
    }

    @Transactional
    public int insert(){
        return orderMapper.insertSelective(OrderBuilder.buildOrder());
    }

    @Transactional
    public int insert(Order order){
        return orderMapper.insertSelective(order);
    }

    public int updateOrderForCharge(Order order){
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uid",order.getUid());
        criteria.andEqualTo("isPay",0);
        criteria.andEqualTo("status",1);
        criteria.andEqualTo("orderStatus",0);
        criteria.andEqualTo("paymentType",2);
        criteria.andEqualTo("isDigital",1);
        return orderMapper.updateByExampleSelective(order,example);}
}
