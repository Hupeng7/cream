package com.icecreamGroup.major.feignClients;

import com.icecreamGroup.major.Model.Order;
import com.icecreamGroup.major.feignClients.FeiginFallBack.OrderFeignFallBack;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Feign调用服务栗子
 * name:调用的服务在Eureka上面的虚拟主机名
 * fallback: 此服务调用不成功的降级处理
 */
@FeignClient(name ="order-service",fallback = OrderFeignFallBack.class)
public interface OrderFeignClient {
    @RequestMapping("order/detail/{orderNo}")
    Order getOrderByOrderNo(@PathVariable("orderNo") String orderNo);

}
