package com.icecream.good.controller;

import com.icecream.common.model.pojo.Good;
import com.icecream.common.util.res.ResultVO;
import com.icecream.good.service.GoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("test")
    public String test(@Param("test")String test,@Param("specialTokenId")String specialTokenId){
        log.info(test+"------"+specialTokenId);
        return "OK";
    }

    @PostMapping("test2")
    public String test2( @RequestBody Good good, @Param("specialTokenId")String specialTokenId){
        log.info(good+"-----"+specialTokenId);
        return "ok";
    }


}
