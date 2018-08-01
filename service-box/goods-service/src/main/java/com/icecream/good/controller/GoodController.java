package com.icecream.good.controller;

import com.icecream.common.model.pojo.Good;
import com.icecream.common.util.res.ResultVO;
import com.icecream.good.service.GoodService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/9 0009
 */
@Slf4j
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
                                     @RequestParam(value = "count", required = true) Integer count) {
        return goodService.getDiscoverGoods(discoverId, sid, lastGoodsSn, count);
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
     * 根据goodsSn获取商品详情
     *
     * @param goodsSn
     * @return resultVo
     */
    @GetMapping("{goodsSn}")
    public ResultVO getGoodsByGoodsSn(@PathVariable("goodsSn") String goodsSn) {
        return goodService.getGoodsByGoodsSn(goodsSn);
    }

}
