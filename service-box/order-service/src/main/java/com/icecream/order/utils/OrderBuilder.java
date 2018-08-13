package com.icecream.order.utils;

import com.icecream.common.model.pojo.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class OrderBuilder {


    public static Order buildOrder(){
        Order order = new Order();
        //order.setId(UUIDFactory.create());
        order.setSid(1);
        order.setUid(2082501);
        order.setCreater(-1);
        order.setAmount(new BigDecimal(1));
        order.setOrderNo("001120180119000018887");
        order.setGoodsId("000005");
        order.setGoodsPrice(new BigDecimal(3500));
        order.setParentOrderNo("111111111");
        order.setAccount("-1");
        order.setOrderType(2);
        order.setPayPrice(new BigDecimal(3500));
        Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        order.setPayTime(second.intValue());
        order.setChangePrice(new BigDecimal(0));
        Long second1 = LocalDateTime.now().minusDays(1).toEpochSecond(ZoneOffset.of("+8"));
        order.setChangeTime(second1.intValue());
        order.setCtime(second1.intValue());
        order.setMtime(second1.intValue());
        return order;
    }
}
