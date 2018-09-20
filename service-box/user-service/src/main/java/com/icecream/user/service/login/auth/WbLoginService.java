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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static com.icecream.common.util.constant.SysConstants.USER_HASH_PREFIX;

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
        UserAuth record = userRegisterService.isHaveBeenRegistered(wbLoginParams.getOpenId(), wbLoginParams.getType());
        if (null == record) {
            ThirdPartUserInfo thirdPartUserInfo = callRemoteInterFaceForWxLogin(wbLoginParams);
            User user = cover(thirdPartUserInfo, wbLoginParams);
            Integer isRegister = userRegisterService.registerForFans(user, wbLoginParams.getOpenId(), wbLoginParams.getType());
            if (isRegister > 0) {
                RedisHandler.addMap(USER_HASH_PREFIX, user.getId().toString(),JSON.toJSONString(user));
                LoginReturn loginReturn = buildLoginSuccessReturn(user);
                return ResultUtil.success(loginReturn);
            }else {
                return ResultUtil.error(null,ResultEnum.DATA_ERROR);
            }
        }
        User user = userService.getUserInfoByUid(record.getUid());
        RedisHandler.addMap(USER_HASH_PREFIX, user.getId().toString(),JSON.toJSONString(user));
        return ResultUtil.success(buildLoginSuccessReturn(user));
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
}
