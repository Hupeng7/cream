package com.icecreamGroup.major.utils;

import com.icecreamGroup.major.Model.Order;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "order-service")
public interface OrderFeignClent {

    @RequestMapping("order/detail/{orderNo}")
    Order getOrderByOrderNo(@PathVariable("orderNo") String orderNo);

}
