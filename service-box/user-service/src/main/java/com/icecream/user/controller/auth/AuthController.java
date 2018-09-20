package com.icecream.user.controller.auth;


import com.icecream.common.model.model.BindingModel;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.binding.UserAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户登录方式相关接口
 * @author mr_h
 * @version 2.0
 */
@Api(description = "登录方式接口")
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
    @ApiOperation(value = "【需要粉丝端token】绑定第三方平台")
    @PostMapping(value = "auth")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "specialTokenId", value = "用户id", required = true, dataType = "String")
    })
    public ResultVO binding(@Validated @RequestBody BindingModel bindingModel,
                            @RequestParam("specialTokenId") String specialTokenId) {
        return userAuthService.binding(bindingModel, specialTokenId);
    }

    /**
     * 解绑第三方平台
     * @return ResultVO<T></>
     */
    @ApiOperation(value = "【需要粉丝端token】解绑第三方平台")
    @DeleteMapping("{type}/auth")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "解绑的登录类型", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "specialTokenId", value = "用户id", required = true, dataType = "Integer")
    })
    public ResultVO unbinding(@PathVariable("type") Integer type,
                              @RequestParam("specialTokenId") String specialTokenId) {
        return userAuthService.unbinding(type, specialTokenId);
    }

    /**
     * 获取所有的登陆方式
     *
     * @param
     * @return
     */
    @ApiOperation(value = "【需要粉丝端token】获取用户所有的登陆方式")
    @GetMapping("auths")
    public ResultVO getAllAuths(@Param("specialTokenId") String specialTokenId) {
        return userAuthService.getAllAuths(specialTokenId);
    }
}
