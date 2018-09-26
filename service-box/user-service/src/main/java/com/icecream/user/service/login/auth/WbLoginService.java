package com.icecream.user.service.login.auth;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.model.*;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.redis.RedisHandler;
import com.icecream.user.service.UserService;
import com.icecream.user.service.login.AbstractLoginSupport;
import com.icecream.user.service.login.SuperLogin;
import com.icecream.user.service.register.UserRegisterService;
import com.icecream.user.utils.jwt.TokenBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static com.icecream.common.util.constant.SysConstants.USER_HASH_PREFIX;
import static com.icecream.user.constants.Constants.TYPE_AUTH_WB;

/**
 * @version 2.0
 */
@Service
@SuppressWarnings("all")
public class WbLoginService extends AbstractLoginSupport implements SuperLogin<WbLoginParams> {

    @Autowired
    private AppIdConfig appIdConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private TokenBuilder tokenBuilder;

    @Autowired
    private UserService userService;


    @Override
    public ResultVO login(WbLoginParams wbLoginParams) {
        String msg = vaild(wbLoginParams);
        if(StringUtils.isBlank(msg)) {
            UserAuth record = userRegisterService.isHaveBeenRegistered(wbLoginParams.getOpenId(), wbLoginParams.getType());
            if (null == record) {
                ThirdPartUserInfo thirdPartUserInfo = callRemoteInterFaceForWxLogin(wbLoginParams);
                User user = cover(thirdPartUserInfo, wbLoginParams);
                Integer isRegister = userRegisterService.registerForFans(user, wbLoginParams.getOpenId(), wbLoginParams.getType());
                if (isRegister > 0) {
                    RedisHandler.addMap(USER_HASH_PREFIX, user.getId().toString(), JSON.toJSONString(user));
                    LoginReturn loginReturn = buildLoginSuccessReturn(user);
                    return ResultUtil.success(loginReturn);
                } else {
                    return ResultUtil.error(null, ResultEnum.MYSQL_OPERATION_FAILED);
                }
            }
            User user = userService.getUserInfoByUid(record.getUid());
            RedisHandler.addMap(USER_HASH_PREFIX, user.getId().toString(), JSON.toJSONString(user));
            return ResultUtil.success(buildLoginSuccessReturn(user));
        }else {
            return ResultUtil.error(msg,ResultEnum.PARAMS_ERROR);
        }
    }

    //调用远程接口
    private ThirdPartUserInfo callRemoteInterFaceForWxLogin(WbLoginParams wbLoginParams) {
        ThirdPartUserInfo thirdPartUserInfo = new ThirdPartUserInfo();
        String url = appIdConfig.getWeiboOpenApiUrl() + "?access_token=" + wbLoginParams.getAccessToken() + "&uid=" + wbLoginParams.getOpenId();
        String str = restTemplate.getForObject(url, JSONObject.class, String.class).toString();
        Map map = JsonUtil.jsonToMap(str);
        thirdPartUserInfo.setName(map.get("name") != null ? map.get("name").toString() : "");
        thirdPartUserInfo.setUrl(map.get("profile_image_url") != null ? map.get("profile_image_url").toString() : "");
        return thirdPartUserInfo;
    }


    private User cover(ThirdPartUserInfo thirdPartUserInfo, WbLoginParams wbLoginParams) {
        User user = new User();
        user.setNickname(thirdPartUserInfo.getName());
        user.setAvatar(thirdPartUserInfo.getUrl());
        user.setPhonemodel(wbLoginParams.getPhoneModel());
        user.setRegister(wbLoginParams.getRegister());
        user.setRegisterType(wbLoginParams.getRegisterType());
        user.setOpenid(wbLoginParams.getOpenId());
        int time = (int) LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        user.setCtime(time);
        user.setLastlogintime(time);
        user.setMtime(0);
        return user;
    }

    private String vaild(WbLoginParams wbLoginParams){
        if(StringUtils.isBlank(wbLoginParams.getOpenId())) return "openId不能为空";
        if(StringUtils.isBlank(wbLoginParams.getAccessToken())) return "accessToken不能为空";
        if(wbLoginParams.getType()!=TYPE_AUTH_WB) return "登录类型不符合";
        if(StringUtils.isBlank(wbLoginParams.getPhoneType())) return "phoneType不能为空";
        if(StringUtils.isBlank(wbLoginParams.getRegister())) return "register不能为空";
        if(null==wbLoginParams.getRegisterType()) return "registerType不能为空";
        if(null==wbLoginParams.getPhoneModel()) return "phoneModel不能为空";
        return "";
    }
}
