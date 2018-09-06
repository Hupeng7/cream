package com.icecream.good.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/21 0021
 */
@Component
@Slf4j
public class seckillHandler /*implements InitializingBean*/ {

   /* @Autowired
    private GoodService goodService;

    @Autowired
    private GoodsSpecService goodsSpecService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("初始化库存");
        List<Goods> list = goodService.getAll();
        if (list == null) {
            return;
        }
        log.info("正常获取到商品数据,数量为{}",list.size());
        for (Goods g : list) {
            MitGoodsRedis goodsRedis = new MitGoodsRedis();
            if (null != g.getSpecGroup()) {
                List<GoodsSpec> specList = goodsSpecService.getSpecList(g.getGoodsSn());
                goodsRedis.setGoods(g);
                goodsRedis.setGoodsSpec(specList);
                for (GoodsSpec gs:specList) {
                    RedisHandler.set(GOODS_SPEC_PREFIX + gs.getId(), gs.getStock());
                }
            }
            goodsRedis.setGoods(g);
            RedisHandler.set(GOODS_PREFIX+g.getGoodsSn(),g.getGoodsNum());
            RedisHandler.addMap(GOODS_PREFIX,g.getGoodsSn(),JSON.toJSONString(goodsRedis));
        }
        }
*/
}

