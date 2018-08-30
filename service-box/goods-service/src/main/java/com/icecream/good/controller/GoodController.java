package com.icecream.good.controller;

import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.requstbody.CreateOrderModel;
import com.icecream.common.model.requstbody.GoodsUpdateMessage;
import com.icecream.common.util.res.ResultVO;
import com.icecream.good.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/9 0009
 */
@RestController
@RequestMapping("Goods")
public class GoodController {

    @Autowired
    private GoodService goodService;


    /**
     * 发现频道 优品&活动
     *
     * @param discoverId  发现频道的id
     * @param sid         app端标识
     * @param lastGoodsSn 上一页最后一条的商品数据
     * @param count       一页展示数据量
     * @return
     */
    @GetMapping("{discoverId}/{sid}")
    public ResultVO getDiscoverGoods(@PathVariable("discoverId") Integer discoverId,
                                     @PathVariable("sid") Integer sid,
                                     @RequestParam(value = "lastGoodsSn", required = true) String lastGoodsSn,
                                     @RequestParam(value = "count", required = true) Integer count,
                                     @Param("specialTokenId") String specialTokenId) {
        return goodService.getDiscoverGoods(discoverId, sid, lastGoodsSn, count, specialTokenId);
    }

    @GetMapping("getDiscoverLabelList")
    public ResultVO getDiscoverLabelList() {
        return goodService.getDiscoverLabelList();
    }


    /**
     * 添加商品(多规格和无规格的商品)
     *
     * @param good 商品对象
     * @return resultVo
     */
    @PostMapping("save")
    public ResultVO save(@RequestBody Good good) {
        return goodService.save(good);
    }

    /**
     * feign调用根据商品编号获取商品信息
     *
     * @return
     */
    @GetMapping("get")
    public Good get(String goodsSn) {
        return goodService.get(goodsSn);
    }

    /**
     * 根据goodsSn获取商品详情
     *
     * @param goodsSn
     * @return resultVo
     */
    @GetMapping("{goodsSn}")
    public ResultVO getGoodsByGoodsSn(@PathVariable("goodsSn") String goodsSn,
    @Param("specialTokenId") String specialTokenId) {
        return goodService.getGoodsByGoodsSn(goodsSn, specialTokenId);
    }

    /**
     * 先行更新单规格商品库存或者多规格商品库存，再根据商品限购，用户限购判断是否能下单
     *
     * @param createOrderModel
     * @return
     */
    @RequestMapping("check")
    public void reduceStoreAndCheck(@RequestBody CreateOrderModel createOrderModel) {
        goodService.reduceStoreAndCheck(createOrderModel);
    }

    //获取redis中的商品的数据
    @RequestMapping("getRedis/{goodsSn}")
    public ResultVO getRedis(@PathVariable("goodsSn") String goodsSn) {
        return goodService.getRedis(goodsSn);
    }

    @RequestMapping("updateGoodsNum")
    public int updateGoodsNum(@RequestBody GoodsUpdateMessage goodsUpdateMessage) {
        return goodService.updateGoodsNum(goodsUpdateMessage);
    }

}
