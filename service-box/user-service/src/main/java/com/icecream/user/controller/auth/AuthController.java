package com.icecream.user.controller.auth;


import com.icecream.common.model.model.BindingModel;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.binding.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户登录方式相关接口
 * @author mr_h
 * @version 2.0
 */
@RestController
@RequestMapping("user")
public class AuthController {

    @Autowired
    private UserAuthService userAuthService;


    /**
     * 绑定第三方平台
     *
     * @param bindingModel 绑定实体类
     * @return ResultVO
     */
    @PostMapping(value = "auth")
    public ResultVO binding(@Validated @RequestBody BindingModel bindingModel,
                            @Param("specialTokenId") String specialTokenId) {
        return userAuthService.binding(bindingModel, specialTokenId);
    }

    /**
     * 解绑第三方平台
     * @return ResultVO<T></>
     */
    @DeleteMapping("{type}/auth")
    public ResultVO unbinding(@PathVariable("type") Integer type,
                              @Param("specialTokenId") String specialTokenId) {
        return userAuthService.unbinding(type, specialTokenId);
    }

    /**
     * 获取所有的登陆方式
     *
     * @param
     * @return
     */
    @GetMapping("auths")
    public ResultVO getAllAuths(@Param("specialTokenId") String specialTokenId) {
        return userAuthService.getAllAuths(specialTokenId);
    }
}
