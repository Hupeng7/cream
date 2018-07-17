package com.icecream.good.controller;

import com.icecream.common.model.pojo.Good;
import com.icecream.common.util.res.ResultVO;
import com.icecream.good.service.GoodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("Goods")
public class GoodController {

    @Autowired
    private GoodService goodService;


    @GetMapping("{discoverId}/{sid}")
    public ResultVO findAllGoods(@PathVariable("discoverId")String discoverId,
                                 @PathVariable("sid") String sid,
                                 @Param("lastGoods_sn") String lastGoodsSn,
                                 @Param("count") Integer count){
        return goodService.findAll();
    }

}
