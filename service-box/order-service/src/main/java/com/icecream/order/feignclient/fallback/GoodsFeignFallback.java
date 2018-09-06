package com.icecream.order.feignclient.fallback;

import com.icecream.common.model.pojo.Goods;
import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.model.model.CreateOrderModel;
import com.icecream.common.model.model.GoodsUpdateMessage;
import com.icecream.order.feignclient.GoodsFeignClient;
import org.springframework.stereotype.Component;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/15 0015
 */
@Component
public class GoodsFeignFallback implements GoodsFeignClient {
    @Override
    public Goods get(String goodsSn) {
        return null;
    }

    @Override
    public int updateGoodsCount(Order order) {
        throw new RuntimeException("更新失败");
    }

    @Override
    public void reduceStoreAndCheck(CreateOrderModel createOrderModel) {}
    @Override
    public GoodsSpec getSpec(String specId) {
        return null;
    }

    @Override
    public int updateGoodsNum(GoodsUpdateMessage goodsUpdateMessage) {
        throw new RuntimeException("更新失败");
    }

}
