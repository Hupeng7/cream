package com.icecream.user.controller.charge;

import com.icecream.user.service.charge.AliPayNotifyService;
import com.icecream.user.service.charge.WxPayNotifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mr_h
 * @version 1.0
 * description:充值之后回调
 * create by Mr_h on 2018/8/1 0001
 */
@Api(description = "回调(内部调用)")
@Slf4j
@RestController
@RequestMapping("notify")
public class ChargeNotifyController {


    @Autowired
    private AliPayNotifyService aliPayNotifyService;

    @Autowired
    private WxPayNotifyService wxPayNotifyService;


    @PostMapping("ali")
    @ApiOperation(value = "支付宝回调")
    public String handlerAliNotifyPayMessage(HttpServletRequest request) {
        return aliPayNotifyService.aliNotify(request);
    }

    @PostMapping("wx")
    @ApiOperation(value = "微信回调")
    public String handlerWxNotifyPayMessage(HttpServletRequest request, HttpServletResponse response) {
        return wxPayNotifyService.wxNotify(request,response);
    }

}
