package com.icecreamGroup.major.feignClients.FeiginFallBack;

import com.icecreamGroup.major.Model.Order;
import com.icecreamGroup.major.feignClients.OrderFeignClient;
import org.springframework.stereotype.Component;

@Component
public class OrderFeignFallBack implements OrderFeignClient {

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        Order order = new Order();
        order.setSid(1111111);
        return order;
    }
}
