package com.icecream.good.service;

import com.icecream.common.model.pojo.DiscoverDisplay;
import com.icecream.common.model.pojo.DiscoverGoods;
import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.pojo.GoodsSpec;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public ResultVO getDiscoverGoods(Integer discoverId, Integer sid,
                                     String lastGoodsSn, Integer count) {
        Good arg = new Good();
        arg.setGoodsSn(lastGoodsSn.valueOf(lastGoodsSn));
        List<Good> select = goodMapper.select(arg);
        if (select.isEmpty()) return ResultUtil.error("lastGoodsSn为空或者无此商品", ResultEnum.PARAMS_ERROR);
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

    public int update(Good good){
        return goodMapper.updateByPrimaryKey(good);
    }

}
