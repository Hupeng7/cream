package com.icecream.user.controller.charge;

import com.icecream.user.service.charge.AliPayNotifyService;
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
@Slf4j
@RestController
@RequestMapping("notify")
public class ChargeNotifyController {


    @Autowired
    private AliPayNotifyService aliPayNotifyService;


    @PostMapping("ali")
    public String handlerAliNotifyPayMessage(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        return aliPayNotifyService.aliNotify(request);
    }

    @RequestMapping("wx")
    public String handlerWxNotifyPayMessage() {
        return "";
    }

}
