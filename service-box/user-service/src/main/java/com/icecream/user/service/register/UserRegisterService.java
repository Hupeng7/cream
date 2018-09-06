package com.icecream.user.service.register;

import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.model.*;
import com.icecream.common.redis.RedisHandler;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserMapper;
import com.icecream.user.service.binding.UserAuthService;
import com.icecream.user.service.code.CodeHandler;
import com.icecream.user.service.push.UserPushService;
import com.icecream.user.utils.jwt.TokenBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * @version 2.0
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class UserRegisterService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserPushService userPushService;

    @Autowired
    private TokenBuilder tokenBuilder;

    @Autowired
    private CodeHandler codeHandler;

    public UserAuth isHaveBeenRegistered(String key, Integer type) {
        UserAuth haveBeenRegistered = userAuthService.getByType(type, key);
        return Optional.ofNullable(haveBeenRegistered).orElse(null);
    }


    @Transactional(rollbackFor = Exception.class)
    public LoginReturn toRegister(ThirdPartUserInfo thirdPartUserInfo, QQLoginParams qqLoginParams) {
        User user = cover(thirdPartUserInfo, qqLoginParams);
        User register = register(qqLoginParams.getType(), user);
        if (register!=null) {
            return new LoginReturn(register, tokenBuilder.createToken(user));
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public LoginReturn toRegister(ThirdPartUserInfo thirdPartUserInfo, WbLoginParams wbLoginParams) {
        User user = cover(thirdPartUserInfo, wbLoginParams);
        User register = register(wbLoginParams.getType(), user);
        if (register!=null) {
            return new LoginReturn(register, tokenBuilder.createToken(user));
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public LoginReturn toRegister(ThirdPartUserInfo thirdPartUserInfo, WxLoginParams wxLoginParams) {
        User user = cover(thirdPartUserInfo, wxLoginParams);
        User register = register(wxLoginParams.getType(), user);
        if (register!=null) {
            return new LoginReturn(register, tokenBuilder.createToken(user));
        }
        return null;
    }


    @Transactional(rollbackFor = Exception.class)
    public LoginReturn toRegister(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        User user = cover(smsLoginOrRegisterParams);
        User register = register(smsLoginOrRegisterParams.getType(), user);
        if (register!=null) {
            return new LoginReturn(register, tokenBuilder.createToken(user));
        }
        return null;
    }

    /**
     * 此方法是因为要封装resultVO类而冗余的方法
     * @param smsLoginOrRegisterParams
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO register(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        String key = smsLoginOrRegisterParams.getItucode() + smsLoginOrRegisterParams.getPhone();
        Boolean right= codeHandler.check(key, smsLoginOrRegisterParams.getCode());
        String vaild = vaild(smsLoginOrRegisterParams);
        if(!right){return ResultUtil.error("验证码失效或者不正确",ResultEnum.PARAMS_ERROR);}
        if(StringUtils.isNotBlank(vaild)){return ResultUtil.error(vaild,ResultEnum.PARAMS_ERROR);}
        LoginReturn loginReturn = toRegister(smsLoginOrRegisterParams);
        if(null==loginReturn){
            return ResultUtil.error(null,ResultEnum.PARAMS_ERROR);
        }
        return ResultUtil.success(loginReturn);
    }


    private User cover(ThirdPartUserInfo thirdPartUserInfo, QQLoginParams qqLoginParams) {
        User user = new User();
        user.setNickname(thirdPartUserInfo.getName());
        user.setAvatar(thirdPartUserInfo.getUrl());
        user.setPhonemodel(qqLoginParams.getPhoneModel());
        user.setRegister(qqLoginParams.getRegister());
        user.setRegisterType(qqLoginParams.getRegisterType());
        user.setOpenid(qqLoginParams.getOpenId());
        return user;
    }


    private User cover(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        User user = new User();
        if (smsLoginOrRegisterParams.getPassword() != null) {
            user.setPassword(smsLoginOrRegisterParams.getPassword());
        }
        user.setItucode(smsLoginOrRegisterParams.getItucode());
        user.setPhone(smsLoginOrRegisterParams.getPhone());
        user.setPhonemodel(smsLoginOrRegisterParams.getPhoneModel());
        user.setRegister(smsLoginOrRegisterParams.getRegister());
        int time = (int) LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        user.setCtime(time);
        user.setLastlogintime(time);
        user.setMtime(0);
        return user;
    }

    private User cover(ThirdPartUserInfo thirdPartUserInfo, WxLoginParams wxLoginParams) {
        User user = new User();
        user.setNickname(thirdPartUserInfo.getName());
        user.setAvatar(thirdPartUserInfo.getUrl());
        user.setPhonemodel(wxLoginParams.getPhoneModel());
        user.setRegister(wxLoginParams.getRegister());
        user.setRegisterType(wxLoginParams.getRegisterType());
        user.setOpenid(wxLoginParams.getCode());
        return user;
    }


    private User cover(ThirdPartUserInfo thirdPartUserInfo, WbLoginParams wbLoginParams) {
        User user = new User();
        user.setNickname(thirdPartUserInfo.getName());
        user.setAvatar(thirdPartUserInfo.getUrl());
        user.setPhonemodel(wbLoginParams.getPhoneModel());
        user.setRegister(wbLoginParams.getRegister());
        user.setRegisterType(wbLoginParams.getRegisterType());
        user.setOpenid(wbLoginParams.getOpenId());
        return user;
    }

    private User register(Integer type, User args) {
        int userCount = userMapper.insertSelective(args);
        User userInfo = userMapper.getCache(args.getId());
        RedisHandler.set(userInfo.getId(), userInfo);
        int userAuthCount = userAuthService.insertUserAuthByType(args, type);
        int userRegisterCount = userPushService.insertUserPushByUserId(args);
        if (userAuthCount > 0 && userRegisterCount > 0)
            return userInfo;
        return null;
    }


    private String vaild(SmsLoginOrRegisterParams smsLoginOrRegisterParams){
        if(smsLoginOrRegisterParams.getType()==null){
            return "identity_type类型不能为空";
        }
        if(StringUtils.isBlank(smsLoginOrRegisterParams.getPassword())){
            return "注册用户时password不能为空";
        }
        return "";
    }
}
