package com.icecream.goods.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign调用服务栗子
 * name:调用的服务在Eureka上面的虚拟主机名
 * fallback: 此服务调用不成功的降级处理.
 */
@Component
@FeignClient(name ="order-service")
public interface OrderFeignClient {

    @RequestMapping(value = "order/init/buyerInfo",method = RequestMethod.GET)
    void initRedisBuyerInfo(@RequestParam("uid") Integer uid);

}
