package com.icecream.user.service.register;

import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.model.*;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserMapper;
import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.redis.RedisHandler;
import com.icecream.user.service.binding.UserAuthService;
import com.icecream.user.service.code.CodeHandler;
import com.icecream.user.service.login.AbstractLoginSupport;
import com.icecream.user.service.login.sms.SmsLoginService;
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
public class UserRegisterService extends AbstractLoginSupport {

    @Autowired
    private RedisHandler redisHandler;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SmsLoginService smsLoginService;

    @Autowired
    private UserStarMapper userStarMapper;

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
    public Integer registerForFans(User user, String identifier, Integer type) {
        int userCount = userMapper.insertSelective(user);
        User userInfo = userMapper.getCache(user.getId());
        int userAuthCount = userAuthService.insertUserAuthByType(user, identifier, type);
        int userRegisterCount = userPushService.insertUserPushByUserId(user);
        if (userAuthCount > 0 && userRegisterCount > 0)
            return userAuthCount;
        return -1;
    }

    public Integer registerForStar(UserStar userStar) {
        return userStarMapper.insertSelective(userStar);
    }

    /**
     * 此方法是因为要封装resultVO类而冗余的方法
     *
     * @param smsLoginOrRegisterParams
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO register(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        String key = smsLoginOrRegisterParams.getItucode() + smsLoginOrRegisterParams.getPhone();
        Boolean right = codeHandler.check(key, smsLoginOrRegisterParams.getCode());
        String vaild = vaild(smsLoginOrRegisterParams);
        if (!right) {
            return ResultUtil.error("验证码失效或者不正确", ResultEnum.PARAMS_ERROR);
        }
        if (StringUtils.isNotBlank(vaild(smsLoginOrRegisterParams))) {
            return ResultUtil.error(vaild, ResultEnum.PARAMS_ERROR);
        }
        User user = cover(smsLoginOrRegisterParams);
        Integer isRegister = registerForFans(user, smsLoginOrRegisterParams.getItucode() + smsLoginOrRegisterParams.getPhone(),
                smsLoginOrRegisterParams.getType());
        if (isRegister > 0) {
            LoginReturn loginReturn = buildLoginSuccessReturn(user);
            return ResultUtil.success(loginReturn);
        } else {
            return ResultUtil.error(null, ResultEnum.DATA_ERROR);
        }
    }


    private String vaild(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        if (smsLoginOrRegisterParams.getType() == null) {
            return "identity_type类型不能为空";
        }
        if (StringUtils.isBlank(smsLoginOrRegisterParams.getPassword())) {
            return "注册用户时password不能为空";
        }
        return "";
    }

    @Transactional(rollbackFor = Exception.class)
    public LoginReturn toRegister(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        User user = cover(smsLoginOrRegisterParams);
        Integer isRegister = registerForFans(user, smsLoginOrRegisterParams.getItucode() + smsLoginOrRegisterParams.getPhone(),
                smsLoginOrRegisterParams.getType());
        if (isRegister > 0) {
            return buildLoginSuccessReturn(user);
        }
        return null;
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
        user.setRegisterType(smsLoginOrRegisterParams.getRegisterType());
        user.setCtime(time);
        user.setLastlogintime(time);
        user.setMtime(0);
        return user;
    }
}
