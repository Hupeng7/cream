package com.icecream.user.service.login.sms;

import com.icecream.common.model.model.WbLoginParams;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.icecream.user.constants.Constants.TYPE_AUTH_WB;
import static com.icecream.user.constants.Constants.TYPE_SMS;


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
     *
     * @param smsLoginOrRegisterParams 手机登录相关参数
     * @return
     */
    @Override
    public ResultVO login(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        String msg = vaild(smsLoginOrRegisterParams);
        if (StringUtils.isBlank(msg)) {
            String key = smsLoginOrRegisterParams.getItucode() + smsLoginOrRegisterParams.getPhone();
            if (smsLoginOrRegisterParams.getPassword() == null) {
                String value = redisHandler.get(key) == null ? "-1" : redisHandler.get(key).toString();
                Boolean mirror = codeHandler.check(key, Integer.parseInt(value));
                //验证码不正确
                if (!mirror) {
                    return ResultUtil.error(null, ResultEnum.CODE_AUTHENTICATION_FAILED);
                }
            }
            UserAuth record = userRegisterService.isHaveBeenRegistered(key, smsLoginOrRegisterParams.getType());
            //授权库存在登进记录
            if (record != null) {
                if (smsLoginOrRegisterParams.getPassword() != null) {
                    if (record.getCredential() != smsLoginOrRegisterParams.getPassword()) {
                        return ResultUtil.error("密码不正确", ResultEnum.ERROR_PASSWORD);
                    }
                }
                User user = userService.getUserInfoByUid(record.getUid());
                return ResultUtil.success(buildLoginSuccessReturn(user));
            }
            //验证码正确且授权库没有记录，第一次注册
            if (smsLoginOrRegisterParams.getPassword() == null) {
                LoginReturn loginReturn = userRegisterService.toRegister(smsLoginOrRegisterParams);
                return ResultUtil.success(loginReturn);
            } else {
                return ResultUtil.error("用户不存在", ResultEnum.PARAMS_ERROR);
            }
        } else {
            return ResultUtil.error(msg, ResultEnum.PARAMS_ERROR);
        }
    }

    private String vaild(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        if (StringUtils.isBlank(smsLoginOrRegisterParams.getItucode())) return "itucode不能为空";
        if (StringUtils.isBlank(smsLoginOrRegisterParams.getPhone())) return "phone不能为空";
        if (smsLoginOrRegisterParams.getType() != TYPE_SMS) return "登录类型不符合";
        if (StringUtils.isBlank(smsLoginOrRegisterParams.getPhoneType())) return "phoneType不能为空";
        if (StringUtils.isBlank(smsLoginOrRegisterParams.getRegister())) return "register不能为空";
        if (null == smsLoginOrRegisterParams.getRegisterType()) return "registerType不能为空";
        if (null == smsLoginOrRegisterParams.getPhoneModel()) return "phoneModel不能为空";
        return "";
    }

}
