package com.icecream.user.controller.login;


import com.icecream.common.model.requstbody.LoginParamContainer;
import com.icecream.common.model.requstbody.LoginReturn;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.aspect.annotation.LoginHandler;
import com.icecream.user.service.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录相关接口
 * @author mr_h
 * @version 2.0
 */
@RestController
@RequestMapping("user")
@SuppressWarnings("all")
public class LoginController {

    @Autowired
    private LoginService loginService;


    @LoginHandler
    @PostMapping("superLogin")
    public ResultVO<LoginReturn> superLogin(LoginParamContainer loginParamContainer){
        return loginService.superLogin(loginParamContainer);
    }
}

