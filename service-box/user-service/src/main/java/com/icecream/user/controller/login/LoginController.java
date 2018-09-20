package com.icecream.user.controller.login;


import com.icecream.common.model.model.LoginParamContainer;
import com.icecream.common.model.model.LoginReturn;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.aspect.annotation.LoginHandler;
import com.icecream.user.service.login.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录相关接口
 * @author mr_h
 * @version 2.0
 */
@Api(description = "登录")
@RestController
@RequestMapping("user")
@SuppressWarnings("all")
public class LoginController {

    @Autowired
    private LoginService loginService;


    /**
     * 用户登录支持
     * 粉丝端登录方式：
     * 1.手机验证码快速登录
     * 2.手机密码登录
     * 3.第三方登录(qq,微信,微博)
     * 版主端登录方式：账号密码登录
     * @param loginParamContainer 前端传递的参数容器
     * @return ResultVo<LoginReturn>
     */
    @LoginHandler
    @PostMapping("superLogin")
    @ApiOperation(value = "用户登录")
    public ResultVO<LoginReturn> superLogin(LoginParamContainer loginParamContainer){
        return loginService.superLogin(loginParamContainer);
    }
}

