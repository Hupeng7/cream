package com.icecream.order.feignclient;


import com.icecream.common.model.pojo.Goods;
import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.model.model.CreateOrderModel;
import com.icecream.common.model.model.GoodsUpdateMessage;
import com.icecream.order.feignclient.fallback.GoodsFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "goods-service",fallback =GoodsFeignFallback.class)
public interface GoodsFeignClient {

    @RequestMapping(value = "Goods/get",method = RequestMethod.GET)
    Goods get(@RequestParam("goodsSn")String goodsSn);

    @RequestMapping(value = "Goods/limit/update",method =RequestMethod.PUT )
    int updateGoodsCount(Order order);

    @RequestMapping(value = "Goods/check")
    void reduceStoreAndCheck(CreateOrderModel createOrderModel);

    @RequestMapping(value = "Goods/spec/get")
    GoodsSpec getSpec(@RequestParam("specId") String specId);

    @RequestMapping(value = "Goods/updateGoodsStore")
    int updateGoodsStore(GoodsUpdateMessage goodsUpdateMessage);
}
