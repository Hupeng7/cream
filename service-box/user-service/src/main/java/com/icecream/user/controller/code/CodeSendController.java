package com.icecream.user.controller.code;

import com.icecream.common.model.model.SendCode;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.code.chuanglan.ChuanglanSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 2.0
 */
@RestController
@RequestMapping("user")
@SuppressWarnings("all")
public class CodeSendController {


    @Autowired
    private ChuanglanSender chuanglanSender;
    /**
     * 发送验证码
     * @return {@link ResultVO }
     */
    @PostMapping(value = "sendauthcode")
    public ResultVO<String> sendCode(@Validated @RequestBody SendCode sendCode) {
        return ResultUtil.success(chuanglanSender.send(sendCode.getItucode(), sendCode.getPhone()));
    }
}
