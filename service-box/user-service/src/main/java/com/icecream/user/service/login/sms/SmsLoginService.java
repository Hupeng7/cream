package com.icecream.user.service.login.sms;

import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.model.LoginReturn;
import com.icecream.common.model.model.SmsLoginOrRegisterParams;
import com.icecream.common.redis.RedisHandler;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserAuthMapper;
import com.icecream.user.service.code.chuanglan.ChuanglanSender;
import com.icecream.user.service.register.UserRegisterService;
import com.icecream.user.service.UserService;
import com.icecream.user.service.login.SuperLogin;
import com.icecream.user.utils.jwt.TokenBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @version 2.0
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class SmsLoginService implements SuperLogin<SmsLoginOrRegisterParams> {

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenBuilder tokenBuilder;

    @Autowired
    private ChuanglanSender chuanglanSender;


    /**
     * 手机验证码登录（有密码或者无密码）
     * @param smsLoginOrRegisterParams 手机登录相关参数
     * @return
     */
    @Override
    public ResultVO login(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        LoginReturn loginReturn = new LoginReturn();
        String key = smsLoginOrRegisterParams.getItucode() + smsLoginOrRegisterParams.getPhone();
        int mirror = Integer.parseInt(RedisHandler.get(key).toString());
        UserAuth record = userRegisterService.isHaveBeenRegistered(key, smsLoginOrRegisterParams.getType());
        //验证码不正确
        if (mirror != smsLoginOrRegisterParams.getCode()) {
            return ResultUtil.error(null, ResultEnum.CODE_AUTHENTICATION_FAILED);
        }
        //授权库存在登进记录
        if (record != null) {
            User user = userService.getUserInfoByUid(record.getUid());
            return ResultUtil.success(new LoginReturn<>(user, tokenBuilder.createToken(user)));
        }
        //验证码正确且授权库没有记录，第一次注册
        loginReturn = userRegisterService.toRegister(smsLoginOrRegisterParams);
        return ResultUtil.success(loginReturn);
    }

}
