package com.icecreamGroup.user.feignClients.FeiginFallBack;

import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.user.feignClients.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderFeignFallBack implements OrderFeignClient {

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        log.info("调用order-service服务getOrderByOrderNo方法时发生异常/调用超时");
        return new Order();
    }
    @Override
    public int insert() {
        log.info("调用order-service服务insert方法时发生异常/调用超时");
        throw new RuntimeException("order-service处理失败，进入回退并开启断路由！");
    }
}
