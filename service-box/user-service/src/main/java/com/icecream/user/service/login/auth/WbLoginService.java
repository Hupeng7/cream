package com.icecream.user.service.login.auth;


import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.requstbody.*;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.service.UserService;
import com.icecream.user.service.login.SuperLogin;
import com.icecream.user.service.register.UserRegisterService;
import com.icecream.user.utils.jwt.TokenBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @version 2.0
 */
@Service
@SuppressWarnings("all")
public class WbLoginService implements SuperLogin<WbLoginParams> {

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
        UserAuth record = userRegisterService.isHaveBeenRegistered(wbLoginParams.getOpenId(),
                wbLoginParams.getType());
        if (null == record) {
            ThirdPartUserInfo thirdPartUserInfo = callRemoteInterFaceForWxLogin(wbLoginParams);
            LoginReturn loginReturn = userRegisterService.toRegister(thirdPartUserInfo, wbLoginParams);
            return ResultUtil.success(loginReturn);
        }
        User user = userService.getUserInfoByUid(record.getUid());
        String token = tokenBuilder.createToken(user);
        return ResultUtil.success(new LoginReturn<>(user, token));
    }


    private ThirdPartUserInfo callRemoteInterFaceForWxLogin(WbLoginParams wbLoginParams) {
        ThirdPartUserInfo thirdPartUserInfo = new ThirdPartUserInfo();
        String url = appIdConfig.getWeiboOpenApiUrl() + "?access_token=" + wbLoginParams.getAccessToken() + "&uid=" + wbLoginParams.getOpenId();
        String str = restTemplate.getForObject(url, JSONObject.class, String.class).toString();
        Map map = JsonUtil.jsonToMap(str);
        thirdPartUserInfo.setName(map.get("name") != null ? map.get("name").toString() : "");
        thirdPartUserInfo.setUrl(map.get("profile_image_url") != null ? map.get("profile_image_url").toString() : "");
        return thirdPartUserInfo;
    }
}
