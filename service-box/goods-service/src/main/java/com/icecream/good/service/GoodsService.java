package com.icecream.good.service;

import com.alibaba.fastjson.JSON;
import com.codingapi.tx.annotation.ITxTransaction;
import com.icecream.common.model.pojo.*;
import com.icecream.common.model.model.*;
import com.icecream.common.util.idbuilder.staticfactroy.SnowflakeGlobalIdFactory;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.time.DateUtil;
import com.icecream.common.util.uuid.UUIDFactory;
import com.icecream.good.feign.OrderFeignClient;
import com.icecream.good.mapper.*;
import com.icecream.good.redis.RedisHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static com.icecream.common.util.constant.SysConstants.*;

/**
 * @author Mr_h
 * @version 1.0
 * description: 商品服务service
 * create by Mr_h on 2018/7/9 0009
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class GoodsService implements ITxTransaction {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsSpecMapper goodsSpecMapper;

    @Autowired
    private DiscoverDisplayMapper discoverDisplayMapper;

    @Autowired
    private DiscoverGoodsMapper discoverGoodsMapper;

    @Autowired
    private SnowflakeGlobalIdFactory snowflakeGlobalIdFactory;

    @Autowired
    private GoodsSpecService goodsSpecService;

    @Autowired
    private GoodsLimitMapper goodsLimitMapper;

    @Autowired
    private OrderFeignClient orderFeignClient;


    public ResultVO getDiscoverGoods(Integer discoverId, Integer sid,
                                     String lastGoodsSn, Integer count, String uid) {
        Goods arg = new Goods();
        arg.setGoodsSn(lastGoodsSn);
        List<Goods> select = goodsMapper.select(arg);
        if (select.isEmpty()) {
            return ResultUtil.error("lastGoodsSn为空或者无此商品", ResultEnum.PARAMS_ERROR);
        }
        List<DiscoverGoods> discoverGoods = discoverGoodsMapper.selectGoodsIdByDiscoverId(discoverId, select.get(0).getScore());
        discoverGoods = discoverGoods.stream().limit(count).collect(Collectors.toList());
        List<Goods> resultList = new ArrayList<>();
        for (DiscoverGoods dg : discoverGoods) {
            ResultVO resultVO = getGoodsByGoodsSn(dg.getGoodsSn(), uid);
            Goods goods = (Goods) resultVO.getResult();
            int now = (int) (LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
            if (goods.getOnsaleTime() <= now & now <= goods.getOffsaleTime() & goods.getIsSale() == 1) {
                resultList.add(goods);
            }
            log.info("" + goods);
        }
        return ResultUtil.success(resultList);

    }


    public ResultVO getDiscoverLabelList() {
        List<DiscoverDisplay> list = discoverDisplayMapper.getSortList();
        return ResultUtil.success(Optional.ofNullable(list).orElse(null));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO save(Goods goods) {
        if (goods == null) {
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }
        List<GoodsSpec> goodsSpec = goods.getGoodsSpec();
        Goods args = supplementaryData(goods);
        int count = goodsMapper.insertSelective(args);
        if (null != goodsSpec) {
            Goods result = goodsMapper.selectByPrimaryKey(goods.getId());
            goodsSpec.forEach(gs -> {
                gs.setGoodsSn(result.getGoodsSn());
                gs.setId(UUIDFactory.create());
            });
            goodsSpec.forEach(gs -> supplementaryData(gs, result));
            int row = goodsSpecMapper.batchInsert(goodsSpec);
            if (row > 0) {
                return ResultUtil.success();
            }
        }
        if (count > 0) {
            return ResultUtil.success();
        }
        return ResultUtil.error(null, ResultEnum.MYSQL_OPERATION_FAILED);
    }

    public Goods get(String goodsSn) {
        Goods goods = new Goods();
        goods.setGoodsSn(goodsSn);
        List<Goods> select = goodsMapper.select(goods);
        return select.get(0);
    }

    /**
     * 获取商品详情
     * 1.取redis
     * 2.没有则取mysql
     * 3.没有则返回null 有则返回 并且写入redis
     */
    public ResultVO getGoodsByGoodsSn(String goodsSn, String uid) {
        log.info("goodsn==============>" + goodsSn);
        log.info("uid==============>" + uid);
        RedisHandler.incr("get_good_count", 1);
        Goods goods = new Goods();
        goods.setGoodsSn(goodsSn);
        Object redisGood = RedisHandler.getMapField(GOODS_PREFIX, goods.getGoodsSn());
        if (redisGood == null) {
            List<Goods> select = goodsMapper.select(goods);
            if (select.isEmpty()) {
                return ResultUtil.error("goodsSn为空或者无此商品", ResultEnum.PARAMS_ERROR);
            }
            GoodsSpec argSpec = new GoodsSpec();
            argSpec.setGoodsSn(goodsSn);
            List<GoodsSpec> specList = goodsSpecService.getSpecList(goodsSn);

            goods = select.get(0);
            goods.setGoodsSpec(specList);

            log.info("初始化商品信息,库存和已经购买数量");
            for (GoodsSpec goodSpec : specList) {
                RedisHandler.set(GOODS_STOCK_PREFIX + SYMBOL_COLON + goods.getGoodsSn() + SYMBOL_COLON + goodSpec.getId(), goodSpec.getStock());
            }
            if (goods.getSpecGroup() == null || "".equals(goods.getSpecGroup())) {
                RedisHandler.set(GOODS_STOCK_PREFIX + SYMBOL_COLON + goods.getGoodsSn(), goods.getGoodsNum());
            }

            RedisHandler.addMap(GOODS_PREFIX, goods.getGoodsSn(), JSON.toJSONString(goods));
        } else {
            goods = JSON.parseObject(redisGood.toString(), Goods.class);
            if (goods.getSpecGroup() == null || "".equals(goods.getSpecGroup())) {
                log.info("goods------>" + redisGood.toString());
                goods.setGoodsNum((int) RedisHandler.get(GOODS_STOCK_PREFIX + SYMBOL_COLON + goods.getGoodsSn()));
            }

            for (GoodsSpec goodSpec : goods.getGoodsSpec()) {
                goodSpec.setStock((int) RedisHandler.get(GOODS_STOCK_PREFIX + SYMBOL_COLON + goods.getGoodsSn() + SYMBOL_COLON + goodSpec.getId()));
            }
        }

        if (goods.getBuylimit() != -1) {
            Object goodsLimitRedis = RedisHandler.get(HAS_BEEN_BOUGHT_PREFIX + SYMBOL_COLON + uid + SYMBOL_COLON + goods.getGoodsSn());
            if (goodsLimitRedis == null) {
                GoodsLimit goodsLimit = goodsLimitMapper.selectByGoodsSnAndUid(goods.getGoodsSn(), Integer.parseInt(uid));
                int goodsLimitMysql = goodsLimit == null ? 0 : goodsLimit.getGoodsCount();
                RedisHandler.set(HAS_BEEN_BOUGHT_PREFIX + SYMBOL_COLON + uid + SYMBOL_COLON + goods.getGoodsSn(), goodsLimitMysql);
            }
        }

        if (goods.getSpecGroup() != null && !("").equals(goods.getSpecGroup()) && !("-1").equals(goods.getSpecGroup())) {
            handleReturnValue(goods);
        }
        return ResultUtil.success(goods);
    }

    private Goods handleReturnValue(Goods goods) {
        List<GoodSpecResponseModel> specList = new ArrayList<GoodSpecResponseModel>();
        String[] specGroupArray = goods.getSpecGroup().split("\\|");
        if (0 < specGroupArray.length && specGroupArray.length <= 2) {
            for (int i = 0; i < specGroupArray.length; i++) {
                GoodSpecResponseModel goodSpecResponseModel = new GoodSpecResponseModel();
                String[] splitString = specGroupArray[i].split(":");
                goodSpecResponseModel.setSpecName(splitString[0]);
                String[] goodSpecArray = splitString[1].split(",");
                List<String> goodSpecSizeTwoList = new ArrayList<String>();
                for (int j = 0; j < goodSpecArray.length; j++) {
                    goodSpecSizeTwoList.add(goodSpecArray[j]);
                }
                goodSpecResponseModel.setSpecList(goodSpecSizeTwoList);
                specList.add(goodSpecResponseModel);
                goodSpecResponseModel = null;
            }
            goods.setSpecGroup(JSON.toJSONString(specList));
            return goods;
        } else if (specGroupArray.length > 2) {
            GoodSpecResponseModel goodSpecResponseModel = new GoodSpecResponseModel();
            goodSpecResponseModel.setSpecName("规格");
            List<String> goodSpecList = new ArrayList<String>();
            for (int j = 0; j < goods.getGoodsSpec().size(); j++) {
                goodSpecList.add(goods.getGoodsSpec().get(j).getSpec());
            }
            goodSpecResponseModel.setSpecList(goodSpecList);
            specList.add(goodSpecResponseModel);
            goods.setSpecGroup(JSON.toJSONString(specList));
            return goods;
        } else {
            return goods;
        }
    }

    private Goods supplementaryData(Goods goods) {
        goods.setGoodsSn(String.valueOf((snowflakeGlobalIdFactory.create().nextId())));
        goods.setCategoryId(1);
        goods.setGoodsMarketPrice(new BigDecimal(0.5));
        goods.setCtime((int) (LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) / 1000));
        return goods;
    }

    private GoodsSpec supplementaryData(GoodsSpec goodsSpec, Goods goods) {
        goodsSpec.setGoodsSn(goods.getGoodsSn());
        goodsSpec.setId(UUIDFactory.create());
        goodsSpec.setCtime((int) (LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) / 1000));
        return goodsSpec;
    }

    public int update(Goods goods) {
        return goodsMapper.updateByPrimaryKey(goods);
    }

    public int inventoryReduction(Integer buyNum, String goodsSn, Integer sid) {
        return goodsMapper.reductionGoodsNum(buyNum, goodsSn, sid);
    }

    @Transactional(rollbackFor = Exception.class)
    public void reduceStoreAndCheck(CreateOrderModel createOrderModel) {
        GoodsStoreModel goodsStoreModel = null;
        //商品库存
        Integer store = 0;
        //用户已经购买了多少件
        Integer yetNum = 0;
        //用户订单中的商品件数
        Integer buyNum = createOrderModel.getGoodsCount();
        //商品唯一标识
        String goodsSn = createOrderModel.getGoodsSn();
        //多规格标识
        String specId = createOrderModel.getSpecId();
        GoodsLimit userLimit = goodsLimitMapper.selectByGoodsSnAndUid(goodsSn, 0);
        if (null != userLimit) {
            yetNum = userLimit.getGoodsCount();
        }
        Goods result = get(goodsSn);
        //用户还能买多少件该商品
        Integer canBuyNum = result.getBuylimit() - yetNum;
        if (null == specId) {
            goodsStoreModel = reduceGoodsNumOrRollBack(createOrderModel);
            if (goodsStoreModel != null) {
                Integer buylimit = goodsStoreModel.getLimit();
                store = goodsStoreModel.getStore();
            }
        } else {
            goodsStoreModel = reduceGoodsSpecNumOrRollBack(createOrderModel);
            if (goodsStoreModel != null) {
                store = goodsStoreModel.getStore();
            }
        }
        if (store.intValue() >= 0 & buyNum <= store & canBuyNum >= buyNum) {
            RedisHandler.set("GoodsStore", JSON.toJSON(goodsStoreModel));
        }

    }

    //获取某个单规格商品的库存和限购
    public Goods getGoodsNum(String goodsSn, Integer sid) {
        return goodsMapper.getGoodsNum(goodsSn, sid);
    }

    //先行减去库存(单规格商品)
    public GoodsStoreModel reduceGoodsNumOrRollBack(CreateOrderModel createOrderModel) {
        try {
            String goodsSn = createOrderModel.getGoodsSn();
            Integer sid = createOrderModel.getSid();
            int row = inventoryReduction(createOrderModel.getGoodsCount(), goodsSn, sid);
            Goods goods = getGoodsNum(goodsSn, sid);
            GoodsStoreModel goodsStoreModel = new GoodsStoreModel();
            goodsStoreModel.setFinalPrice(goods.getGoodsPrice());
            goodsStoreModel.setLimit(goods.getBuylimit());
            goodsStoreModel.setStore(goods.getGoodsNum());
            log.info("库存参数----->" + goodsStoreModel.getStore());
            if (goodsStoreModel.getStore() < 0) {
                throw new Exception();
            }
            return goodsStoreModel;
            //return Optional.ofNullable(goodsStoreModel).filter(g -> goodsStoreModel.getStore() >= 0).orElseThrow(Exception::new);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    //先行减去库存(多规格商品)
    @Transactional(rollbackFor = Exception.class)
    public GoodsStoreModel reduceGoodsSpecNumOrRollBack(CreateOrderModel createOrderModel) {
        try {
            String goodsSn = createOrderModel.getGoodsSn();
            Integer sid = createOrderModel.getSid();
            String specId = createOrderModel.getSpecId();
            int specRows = goodsSpecService.inventoryReduction(createOrderModel.getGoodsCount(), specId);
            int goodsRows = inventoryReduction(createOrderModel.getGoodsCount(), goodsSn, sid);
            Goods goods = getGoodsNum(goodsSn, sid);
            GoodsSpec goodsSpec = goodsSpecService.get(specId);
            Integer store = Arrays.asList(goods.getGoodsNum(), goodsSpec.getStock()).stream().min(Comparator.comparing(num -> num)).get();
            GoodsStoreModel goodsStoreModel = new GoodsStoreModel();
            goodsStoreModel.setFinalPrice(new BigDecimal(goodsSpec.getPrice()));
            goodsStoreModel.setStore(store);
            return Optional.ofNullable(goodsStoreModel).filter(g -> goodsStoreModel.getStore() > 0).orElseThrow(Exception::new);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    public List<Goods> getAll() {
        return goodsMapper.getAll();
    }


    @Transactional
    public int updateGoodsNum(GoodsUpdateMessage goodsUpdateMessage) {
        log.info(goodsUpdateMessage.toString());
        if (goodsUpdateMessage.getSpecId() != null) {
            GoodsSpec goodsSpec = new GoodsSpec();
            goodsSpec.setId(goodsUpdateMessage.getSpecId());
            goodsSpec.setStock(goodsUpdateMessage.getGoodsNum());
            int row0 = goodsSpecMapper.updateByPrimaryKeySelective(goodsSpec);
            int row1 = goodsLimitMapper.updateGoodsCount(goodsUpdateMessage.getSid(),
                    goodsUpdateMessage.getUid(), goodsUpdateMessage.getGoodsSn()
                    , goodsUpdateMessage.getBought(), DateUtil.getNowSecondIntTime());
            Goods result = goodsMapper.getGoodsNum(goodsUpdateMessage.getGoodsSn(), goodsUpdateMessage.getSid());
            goodsUpdateMessage.setGoodsNum(result.getGoodsNum() - goodsUpdateMessage.getCount());
            int row2 = goodsMapper.updateByGoodsSnAndGoodsNum(goodsUpdateMessage.getSid(),
                    goodsUpdateMessage.getGoodsSn(), goodsUpdateMessage.getGoodsNum());
            if (row0 <= 0 | row1 <= 0 | row2 <= 0) {
                throw new RuntimeException("更新失败");
            }
        } else {
            int row1 = goodsLimitMapper.updateGoodsCount(goodsUpdateMessage.getSid(),
                    goodsUpdateMessage.getUid(), goodsUpdateMessage.getGoodsSn()
                    , goodsUpdateMessage.getBought(), DateUtil.getNowSecondIntTime());
            int row2 = goodsMapper.updateByGoodsSnAndGoodsNum(goodsUpdateMessage.getSid(),
                    goodsUpdateMessage.getGoodsSn(), goodsUpdateMessage.getGoodsNum());
            if (row1 <= 0 | row2 <= 0) {
                throw new RuntimeException("更新失败");
            }
        }
        return 1;
    }

}
