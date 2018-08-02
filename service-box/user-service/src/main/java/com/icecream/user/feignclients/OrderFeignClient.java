package com.icecream.user.feignclients;

import com.icecream.common.model.pojo.AlipayNotifyRecord;
import com.icecream.common.model.pojo.AlipayNotifyRecordErrorLog;
import com.icecream.user.config.OrderFeignConfig;
import com.icecream.common.model.pojo.Order;
import com.icecream.user.feignclients.FeiginFallBack.OrderFeignFallBack;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Feign调用服务栗子
 * name:调用的服务在Eureka上面的虚拟主机名
 * fallback: 此服务调用不成功的降级处理.
 */
@Component
@FeignClient(name ="order-service", configuration =OrderFeignConfig.class, fallback = OrderFeignFallBack.class)
public interface OrderFeignClient {

    @RequestMapping("order/detail/{orderNo}")
    Order getOrderByOrderNo(@PathVariable("orderNo") String orderNo);

    @RequestMapping("order/insert")
    int insert();

    @RequestMapping(value = "order/insertAliChargeRecord",method = RequestMethod.POST)
    String insertAliChargeRecord(AlipayNotifyRecord alipayNotifyRecord);

    @RequestMapping(value = "order/insertAliChargeErrorRecord",method = RequestMethod.POST)
    String insertAliChargeErrorRecord(AlipayNotifyRecordErrorLog alipayNotifyRecordErrorLog);

}
