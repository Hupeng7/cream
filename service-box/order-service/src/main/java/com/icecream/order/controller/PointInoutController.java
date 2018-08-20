package com.icecream.order.controller;

import com.icecream.common.util.res.ResultVO;
import com.icecream.order.service.PointInoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/17 0017
 */
@RestController
@RequestMapping("order/pointInout")
public class PointInoutController {

    @Autowired
    private PointInoutService pointInoutService;


    @RequestMapping("getList/{count}/{lastTime}/{sort}")
    public ResultVO getPointListAndSort(@PathVariable("count")Integer count,
                                        @PathVariable("lastTime")Integer lastTime,
                                        @PathVariable("sort")Integer sort,
                                        @Param("specialTokenId") String specialTokenId){
        return pointInoutService.getPointListAndSort(count,lastTime,sort,Integer.parseInt(specialTokenId));
    }
}
