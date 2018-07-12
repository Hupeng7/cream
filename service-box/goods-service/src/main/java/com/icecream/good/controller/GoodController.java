package com.icecream.good.controller;

import com.icecream.common.util.res.ResultVO;
import com.icecream.good.service.GoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/9 0009
 */
@Slf4j
@RestController
@RequestMapping("good")
public class GoodController {

    @Autowired
    private GoodService goodService;

    /**
     *
     * @param goodNo
     * @return
     * @throws Exception
     */
    @GetMapping("{goods_sn}")
    public ResultVO getGood(@PathVariable("goods_sn")String goodNo) throws Exception {
        return goodService.getGoodByNo(goodNo);
    }

    @GetMapping("test/{goods_sn}")
    public ResultVO getGood2(@PathVariable("goods_sn")String goodNo){
        return goodService.getGoodByNo2(goodNo);
    }

}
