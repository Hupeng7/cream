package com.icecream.user.feignclients.FeiginFallBack;

import com.icecream.user.feignclients.OrderFeignClient;
import com.icecreamGroup.common.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderFeignFallBack implements OrderFeignClient {

    @Override
    public Order getOrderByOrderNo(String orderNo){
        log.info("调用order-service服务getOrderByOrderNo方法时发生异常/调用超时");
        throw new RuntimeException("order-service查询失败，进入回退");
    }
    @Override
    public int insert() {
        log.info("调用order-service服务insert方法时发生异常/调用超时");
        throw new RuntimeException("order-service处理时异常，进入回退");
    }
}
