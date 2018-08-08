package com.icecream.order.controller;

import com.icecream.common.util.res.ResultVO;
import com.icecream.order.service.CheckInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr_h
 * @version 1.0
 * description: 此业务应该在用户库，暂时存放
 * create by Mr_h on 2018/8/6 0006
 */

@RestController
@RequestMapping("check")
public class CheckInfoController {

    @Autowired
    private CheckInfoService checkInfoService;

    @PostMapping("do")
        public ResultVO signIn(@Param("specialTokenId")String specialTokenId) {
        return checkInfoService.signIn(Integer.parseInt(specialTokenId));
    }
}
