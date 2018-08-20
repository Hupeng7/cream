package com.icecream.good.service;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.pojo.DiscoverDisplay;
import com.icecream.common.model.pojo.DiscoverGoods;
import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.common.model.requstbody.CreateOrderModel;
import com.icecream.common.model.requstbody.GoodSpecResponseModel;
import com.icecream.common.model.requstbody.GoodsStoreModel;
import com.icecream.common.util.idbuilder.staticfactroy.SnowflakeGlobalIdFactory;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.uuid.UUIDFactory;
import com.icecream.good.mapper.*;
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

/**
 * @author Mr_h
 * @version 1.0
 * description: 商品服务service
 * create by Mr_h on 2018/7/9 0009
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class GoodService {

    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private GoodStoreMapper goodStoreMapper;

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

    public ResultVO getDiscoverGoods(Integer discoverId, Integer sid,
                                     String lastGoodsSn, Integer count) {
        Good arg = new Good();
        arg.setGoodsSn(lastGoodsSn);
        List<Good> select = goodMapper.select(arg);
        if (select.isEmpty()) {
            return ResultUtil.error("lastGoodsSn为空或者无此商品", ResultEnum.PARAMS_ERROR);
        }
        List<DiscoverGoods> discoverGoods = discoverGoodsMapper.selectGoodsIdByDiscoverId(discoverId, select.get(0).getScore());
        discoverGoods = discoverGoods.stream().limit(count).collect(Collectors.toList());
        List<Good> resultList = new ArrayList<>();
        for (DiscoverGoods dg : discoverGoods) {
            Good good = goodMapper.selectByPrimaryKeySimpleInfo(dg.getGoodsid());
            int now = (int) (LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
            if (good.getOnsaleTime() <= now & now <= good.getOffsaleTime() & good.getIsSale() == 1) {
                resultList.add(good);
            }
            log.info("" + good);
        }
        return ResultUtil.success(resultList);

    }


    public ResultVO getDiscoverLabelList() {
        List<DiscoverDisplay> list = discoverDisplayMapper.getSortList();
        return ResultUtil.success(Optional.ofNullable(list).orElse(null));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO save(Good good) {
        if (good == null) {
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }
        List<GoodsSpec> goodsSpec = good.getGoodsSpec();
        Good args = supplementaryData(good);
        int count = goodMapper.insertSelective(args);
        if (null != goodsSpec) {
            Good result = goodMapper.selectByPrimaryKey(good.getId());
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

    public Good get(String goodsSn){
        Good good = new Good();
        good.setGoodsSn(goodsSn);
        List<Good> select = goodMapper.select(good);
        return select.get(0);
    }

    public ResultVO getGoodsByGoodsSn(String goodsSn) {
        Good arg = new Good();
        arg.setGoodsSn(goodsSn);
        List<Good> select = goodMapper.select(arg);
        if (select.isEmpty()){
            return ResultUtil.error("goodsSn为空或者无此商品", ResultEnum.PARAMS_ERROR);
        }

        GoodsSpec argSpec = new GoodsSpec();
        argSpec.setGoodsSn(goodsSn);
        List<GoodsSpec> selectGoodsSpec = goodsSpecMapper.select(argSpec);
        Good good = select.get(0);
        good.setGoodsSpec(selectGoodsSpec);
        if (good.getSpecGroup() != null && !("").equals(good.getSpecGroup()) && !("-1").equals(good.getSpecGroup())) {
            handleReturnValue(good);
        }
        return ResultUtil.success(good);
    }

    private Good handleReturnValue(Good good) {
        List<GoodSpecResponseModel> specList = new ArrayList<GoodSpecResponseModel>();
        String[] specGroupArray = good.getSpecGroup().split("\\|");
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
            good.setSpecGroup(JSON.toJSONString(specList));
            return good;
        } else if (specGroupArray.length > 2) {
            GoodSpecResponseModel goodSpecResponseModel = new GoodSpecResponseModel();
            goodSpecResponseModel.setSpecName("规格");
            List<String> goodSpecList = new ArrayList<String>();
            for (int j = 0; j < good.getGoodsSpec().size(); j++) {
                goodSpecList.add(good.getGoodsSpec().get(j).getSpec());
            }
            goodSpecResponseModel.setSpecList(goodSpecList);
            specList.add(goodSpecResponseModel);
            good.setSpecGroup(JSON.toJSONString(specList));
            return good;
        } else {
            return good;
        }
    }

    private Good supplementaryData(Good good) {
        good.setGoodsSn(String.valueOf((snowflakeGlobalIdFactory.create().nextId())));
        good.setCategoryId(1);
        good.setGoodsMarketPrice(new BigDecimal(0.5));
        good.setCtime((int) (LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) / 1000));
        return good;
    }

    private GoodsSpec supplementaryData(GoodsSpec goodsSpec, Good good) {
        goodsSpec.setGoodsSn(good.getGoodsSn());
        goodsSpec.setId(UUIDFactory.create());
        goodsSpec.setCtime((int) (LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) / 1000));
        return goodsSpec;
    }

    public int update(Good good) {
        return goodMapper.updateByPrimaryKey(good);
    }

    public int inventoryReduction(Integer buyNum, String goodsSn, Integer sid) {
        return goodMapper.reductionGoodsNum(buyNum, goodsSn, sid);
    }

    //获取某个单规格商品的库存和限购
    public Good getGoodsNum(String goodsSn, Integer sid) {
        return goodMapper.getGoodsNum(goodsSn, sid);
    }

    //先行减去库存(单规格商品)
    @Transactional(rollbackFor = Exception.class)
    public GoodsStoreModel reduceGoodsNumOrRollBack(CreateOrderModel createOrderModel) {
        try {
            String goodsSn = createOrderModel.getGoodsSn();
            Integer sid = createOrderModel.getSid();
            int row = inventoryReduction(createOrderModel.getGoodsCount(), goodsSn, sid);
            Good good = getGoodsNum(goodsSn, sid);
            GoodsStoreModel goodsStoreModel = new GoodsStoreModel();
            goodsStoreModel.setFinalPrice(good.getGoodsPrice());
            goodsStoreModel.setLimit(good.getBuylimit());
            goodsStoreModel.setStore(good.getGoodsNum());
            return Optional.ofNullable(goodsStoreModel).filter(g -> goodsStoreModel.getStore() >= 0).orElseThrow(Exception::new);
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
            int specRows = goodsSpecService.inventoryReduction(specId);
            int goodsRows = inventoryReduction(createOrderModel.getGoodsCount(), goodsSn, sid);
            Good good = getGoodsNum(goodsSn, sid);
            GoodsSpec goodsSpec = goodsSpecService.get(specId);
            Integer store = Arrays.asList(good.getGoodsNum(), goodsSpec.getStock()).stream().min(Comparator.comparing(num -> num)).get();
            GoodsStoreModel goodsStoreModel = new GoodsStoreModel();
            goodsStoreModel.setFinalPrice(new BigDecimal(goodsSpec.getPrice()));
            goodsStoreModel.setStore(store);
            return Optional.ofNullable(goodsStoreModel).filter(g -> goodsStoreModel.getStore() > 0).orElseThrow(Exception::new);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }


}
