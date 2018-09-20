package com.icecream.user.controller.charge;

import com.icecream.common.model.model.ChargeParamContainer;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.aspect.annotation.Pay;
import com.icecream.user.service.charge.ChargeMealService;
import com.icecream.user.service.charge.ChargeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr_h
 * @version 1.0
 * description: 用户充值
 * create by Mr_h on 2018/7/26 0026
 */
@Api(description = "用户充值")
@RestController
@RequestMapping("charge")
public class ChargeController {



    @Autowired
    private ChargeMealService chargeMealService;

    /**
     * 支付宝/微信充值
     * @param chargeParamContainer 前端传递参数的容器
     * @return resultVo
     */
    @Pay
    @PostMapping("do")
    @ApiOperation(value = "【需要粉丝端token】用户充值")
    public ResultVO toCharge(ChargeParamContainer chargeParamContainer) {
        return ((ChargeService) chargeParamContainer
                                .getService())
                                .charge(chargeParamContainer.getUid(),chargeParamContainer.getPrice());
    }


    @GetMapping("meal")
    @ApiOperation(value = "【需要粉丝端token】获取充值套餐列表")
    public ResultVO getMeal(){
        return chargeMealService.get();
    }

}
