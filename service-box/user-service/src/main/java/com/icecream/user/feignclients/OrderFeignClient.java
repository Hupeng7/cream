package com.icecream.user.feignclients;

import com.icecream.common.model.pojo.*;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.config.OrderFeignConfig;
import com.icecream.user.feignclients.fallback.OrderFeignFallBack;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
    int insert(Order order);

    @RequestMapping(value = "charge/insertAliChargeRecord",method = RequestMethod.POST)
    String insertAliChargeRecord(AlipayNotifyRecord alipayNotifyRecord);

    @RequestMapping(value = "charge/insertAliChargeErrorRecord",method = RequestMethod.POST)
    String insertAliChargeErrorRecord(AlipayNotifyRecordErrorLog alipayNotifyRecordErrorLog);

    @RequestMapping(value = "charge/insertWxChargeRecord",method = RequestMethod.POST)
    String insertWxChargeRecord(WechatpayNotifyRecord wechatpayNotifyRecord);

    @RequestMapping(value = "charge/meal/get",method = RequestMethod.GET)
    ResultVO getMeal();

    @RequestMapping(value = "wallet/getBalance",method = RequestMethod.GET)
    Wallet getWallet(Integer uid);

    @RequestMapping(value = "rule/get",method = RequestMethod.GET)
    ScoreRule getRule(@RequestParam("type")Integer type,
                      @RequestParam("changePrice")BigDecimal changePrice,
                      @RequestParam("status")Integer status);

}
