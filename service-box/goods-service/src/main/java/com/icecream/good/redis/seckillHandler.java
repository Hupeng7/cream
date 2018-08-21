package com.icecream.good.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.common.model.requstbody.MitGoodsRedis;
import com.icecream.common.redis.RedisHandler;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.good.service.GoodService;
import com.icecream.good.service.GoodsSpecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/21 0021
 */
@Component
@Slf4j
public class seckillHandler implements InitializingBean {

    @Autowired
    private GoodService goodService;

    @Autowired
    private GoodsSpecService goodsSpecService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("初始化库存");
        List<Good> list = goodService.getAll();
        if (list == null) {
            return;
        }
        for (Good g : list) {
            MitGoodsRedis goodsRedis = new MitGoodsRedis();
            if (null != g.getSpecGroup()) {
                List<GoodsSpec> specList = goodsSpecService.getSpecList(g.getGoodsSn());
                goodsRedis.setGood(g);
                goodsRedis.setGoodsSpec(specList);
            }
            goodsRedis.setGood(g);
            RedisHandler.addMap(GoodsRedisProfix.GOODS_PREFIX,g.getGoodsSn(),JSON.toJSONString(goodsRedis));
            Map<String, Object> map = RedisHandler.getMap(GoodsRedisProfix.GOODS_PREFIX);
            String json = JSON.toJSONString(map);
            JSONObject jsonObject = JSON.parseObject(json);
            String string = jsonObject.getString(g.getGoodsSn());
            log.info(json);
            log.info("map"+map.toString());
        }
        }


}

