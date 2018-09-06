package com.icecream.user.service.login.auth;

import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.model.LoginReturn;
import com.icecream.common.model.model.QQLoginParams;
import com.icecream.common.model.model.ThirdPartUserInfo;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.service.register.UserRegisterService;
import com.icecream.user.service.UserService;
import com.icecream.user.service.login.SuperLogin;
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
public class QQLoginService implements SuperLogin<QQLoginParams> {


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
            LoginReturn loginReturn = userRegisterService.toRegister(thirdPartUserInfo, qqLoginParams);
            return ResultUtil.success(loginReturn);
        }
        User user = userService.getUserInfoByUid(record.getUid());
        String token = tokenBuilder.createToken(user);
        return ResultUtil.success(new LoginReturn<>(user, token));
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
}
