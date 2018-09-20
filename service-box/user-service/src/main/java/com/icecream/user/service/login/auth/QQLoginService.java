package com.icecream.user.service.login.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.model.LoginReturn;
import com.icecream.common.model.model.QQLoginParams;
import com.icecream.common.model.model.ThirdPartUserInfo;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.redis.RedisHandler;
import com.icecream.user.service.login.AbstractLoginSupport;
import com.icecream.user.service.register.UserRegisterService;
import com.icecream.user.service.UserService;
import com.icecream.user.service.login.SuperLogin;
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
public class QQLoginService extends AbstractLoginSupport implements SuperLogin<QQLoginParams> {


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
    public ResultVO login(QQLoginParams qqLoginParams) {
        UserAuth record = userRegisterService.isHaveBeenRegistered(qqLoginParams.getOpenId(),
                qqLoginParams.getType());
        if (null == record) {
            ThirdPartUserInfo thirdPartUserInfo = callRemoteInterFaceForQQLogin(qqLoginParams);
            User user = cover(thirdPartUserInfo, qqLoginParams);
            Integer isRegiser = userRegisterService.registerForFans(user, qqLoginParams.getOpenId(), qqLoginParams.getType());
            if(isRegiser>0) {
                LoginReturn loginReturn = buildLoginSuccessReturn(user);
                RedisHandler.addMap(USER_HASH_PREFIX, user.getId().toString(),JSON.toJSONString(user));
                return ResultUtil.success(loginReturn);
            }else{
                return ResultUtil.error(null,ResultEnum.DATA_ERROR);
            }
        }
        User user = userService.getUserInfoByUid(record.getUid());
        RedisHandler.addMap(USER_HASH_PREFIX, user.getId().toString(),JSON.toJSONString(user));
        return ResultUtil.success(buildLoginSuccessReturn(user));
    }


    private ThirdPartUserInfo callRemoteInterFaceForQQLogin(QQLoginParams qqLoginParams) {
        ThirdPartUserInfo thirdPartUserInfo = new ThirdPartUserInfo();
        //根据token&&openid获取用户信息。
        String qqOpenApiUrl2 = appIdConfig.getQqOpenApiUrl2();
        String qqAppId = appIdConfig.getQQappId();
        //根据token获取openid的url
        String getInfoUrl = qqOpenApiUrl2 + "?access_token=" + qqLoginParams.getAccessToken()
                + "&oauth_consumer_key=" + qqAppId + "&openid=" + qqLoginParams.getOpenId();
        /*String getInfoUrl = String.format("%s?access_token=%s&oauth_consumer_key=%s&openid=%s",
                qqOpenApiUrl2, qqLoginParams.getAccessToken(), qqAppId, qqLoginParams.getOpenId());*/

        String str = restTemplate.getForObject(getInfoUrl, JSONObject.class, String.class).toString();
        Map map = JsonUtil.jsonToMap(str);
        thirdPartUserInfo.setName(map.get("nickname") != null ? map.get("nickname").toString() : "");
        thirdPartUserInfo.setUrl(map.get("figureurl_qq_1") != null ? map.get("figureurl_qq_1").toString() : "");
        return thirdPartUserInfo;
    }
    private User cover(ThirdPartUserInfo thirdPartUserInfo, QQLoginParams qqLoginParams) {
        User user = new User();
        user.setNickname(thirdPartUserInfo.getName());
        user.setAvatar(thirdPartUserInfo.getUrl());
        user.setPhonemodel(qqLoginParams.getPhoneModel());
        user.setRegister(qqLoginParams.getRegister());
        user.setRegisterType(qqLoginParams.getRegisterType());
        user.setOpenid(qqLoginParams.getOpenId());
        int time = (int) LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        user.setCtime(time);
        user.setLastlogintime(time);
        user.setMtime(0);
        return user;
    }
}
