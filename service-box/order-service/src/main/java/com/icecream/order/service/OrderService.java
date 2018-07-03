package com.icecream.order.service;

import com.codingapi.tx.annotation.ITxTransaction;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icecream.order.utils.builder.OrderBuilder;
import com.icecream.order.mapper.OrderMapper;
import com.icecream.common.model.pojo.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static com.icecream.order.contants.Contants.DEFAULT_PAGE_CURRENT;
import static com.icecream.order.contants.Contants.DEFAULT_PAGE_SIZE;


@Slf4j
@Service
@SuppressWarnings("all")
public class OrderService implements ITxTransaction {

    @Autowired
    private OrderMapper orderMapper;

    public Order getOrderByOrderNo(String orderNo){
        Order order = new Order();
        order.setOrderNo(orderNo);
        Order order1 = orderMapper.selectOne(order);
        return order1;
    }

    public PageInfo<Order> list(){
        List<Order> orderList = orderMapper.selectAll();
        PageHelper.startPage(DEFAULT_PAGE_CURRENT,DEFAULT_PAGE_SIZE);
        PageInfo<Order> pageInfo = new PageInfo<>(orderList);
        log.info("分表查询--》",pageInfo);
        return pageInfo;
    }

    @Transactional
    public int insert(){
        return orderMapper.insertSelective(OrderBuilder.buildOrder());
    }
}
