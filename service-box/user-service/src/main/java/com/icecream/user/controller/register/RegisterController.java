package com.icecream.user.controller.register;

import com.icecream.common.model.model.*;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.register.UserRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户注册相关接口
 *
 * @author mr_h
 * @version 2.0
 */
@Api(description = "注册")
@RestController
@RequestMapping("user")
@SuppressWarnings("all")
public class RegisterController {


    @Autowired
    private UserRegisterService userRegisterService;

    /**
     * 注册用户(手机验证码+密码注册)
     * @param SmsRegisterParams 手机注册实体类
     * @return {@link ResultVO}
     */
    @PostMapping("register")
    @ApiOperation(value = "【需要粉丝token】注册用户")
    public ResultVO<LoginReturn> register(@Validated @RequestBody SmsLoginOrRegisterParams smsRegisterParams) {
        return userRegisterService.register(smsRegisterParams);
    }

    /**
     * 查询用户是否被注册过(第三方登录)
     *
     * @param openId
     * @param type
     * @return
     */
    @GetMapping("isHaveBeenRegistered")
    @ApiOperation(value = "【需要粉丝token】查询用户是否被注册过(第三方登录)")
    public ResultVO isHaveBeenRegistered(String openId, Integer type) {
        return ResultUtil.success(userRegisterService.isHaveBeenRegistered(openId, type));
    }


}
