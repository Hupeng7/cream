package com.icecream.good.service;

import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.pojo.GoodsSpec;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.uuid.UUIDFactory;
import com.icecream.good.mapper.GoodMapper;
import com.icecream.good.mapper.GoodStoreMapper;
import com.icecream.good.mapper.GoodsSpecMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public ResultVO findAll() {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO save(Good good) {
        if (good == null) {
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }
        List<GoodsSpec> goodsSpec = good.getGoodsSpec();
        int count = goodMapper.insertSelective(good);
        if (null != goodsSpec) {
            Good result = goodMapper.selectByPrimaryKey(good.getId());
            goodsSpec.forEach(gs -> {
                gs.setGoodId(result.getId());
                gs.setId(UUIDFactory.create());
            });
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


}
