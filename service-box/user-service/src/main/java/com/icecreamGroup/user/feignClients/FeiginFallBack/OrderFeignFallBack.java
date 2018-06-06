package com.icecreamGroup.user.feignClients.FeiginFallBack;

import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.user.feignClients.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderFeignFallBack implements OrderFeignClient {

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        Order order = new Order();
        order.setSid(1111111);
        return order;
    }

    @Override
    public int insert() {
        log.info("访问超时或者调用服务返回异常，进入断路器逻辑");
        throw new RuntimeException("order-service处理失败，进入回退并开启断路由！");
    }
}
