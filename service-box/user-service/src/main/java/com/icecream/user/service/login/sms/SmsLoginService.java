package com.icecream.user.service.login.sms;

import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.model.LoginReturn;
import com.icecream.common.model.model.SmsLoginOrRegisterParams;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserAuthMapper;
import com.icecream.user.redis.RedisHandler;
import com.icecream.user.service.code.CodeHandler;
import com.icecream.user.service.code.chuanglan.ChuanglanSender;
import com.icecream.user.service.login.AbstractLoginSupport;
import com.icecream.user.service.register.UserRegisterService;
import com.icecream.user.service.UserService;
import com.icecream.user.service.login.SuperLogin;
import com.icecream.user.utils.jwt.TokenBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


/**
 * @version 2.0
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class SmsLoginService extends AbstractLoginSupport implements SuperLogin<SmsLoginOrRegisterParams> {

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChuanglanSender chuanglanSender;

    @Autowired
    private RedisHandler redisHandler;

    @Autowired
    private CodeHandler codeHandler;


    /**
     * 手机验证码登录
     * 1 有密码为手机号+密码登录
     * 2 无密码为手机号+验证码快速登录
     * @param smsLoginOrRegisterParams 手机登录相关参数
     * @return
     */
    @Override
    public ResultVO login(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        String key = smsLoginOrRegisterParams.getItucode() + smsLoginOrRegisterParams.getPhone();
        String value = redisHandler.get(key) == null ? "-1" : redisHandler.get(key).toString();
        Boolean mirror = codeHandler.check(key, Integer.parseInt(value));
        //验证码不正确
        if (!mirror) {
            return ResultUtil.error(null, ResultEnum.CODE_AUTHENTICATION_FAILED);
        }
        UserAuth record = userRegisterService.isHaveBeenRegistered(key, smsLoginOrRegisterParams.getType());
        //授权库存在登进记录
        if (record != null) {
            User user = userService.getUserInfoByUid(record.getUid());
            return ResultUtil.success(buildLoginSuccessReturn(user));
        }
        //验证码正确且授权库没有记录，第一次注册
        LoginReturn loginReturn = userRegisterService.toRegister(smsLoginOrRegisterParams);
        return ResultUtil.success(loginReturn);
    }


}
