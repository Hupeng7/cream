package com.icecreamGroup.user.feignClients.FeiginFallBack;

import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.user.feignClients.OrderFeignClient;
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
