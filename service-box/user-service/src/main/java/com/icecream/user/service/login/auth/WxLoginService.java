package com.icecream.user.service.login.auth;


import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.requstbody.LoginReturn;
import com.icecream.common.model.requstbody.ThirdPartUserInfo;
import com.icecream.common.model.requstbody.WxLoginParams;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.common.util.res.ResultEnum;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @version 2.0
 */
@Service
@SuppressWarnings("all")
public class WxLoginService implements SuperLogin<WxLoginParams> {

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
    public ResultVO login(WxLoginParams wxLoginParams) {
        List<String> strings = callRemoteInterFaceForWxLoginStepOne(wxLoginParams);
        UserAuth record = userRegisterService.isHaveBeenRegistered(strings.get(0),
                wxLoginParams.getType());
        if (null == record) {
            ThirdPartUserInfo thirdPartUserInfo = callRemoteInterFaceForWxLoginStepTwo(strings, wxLoginParams);
            LoginReturn loginReturn = userRegisterService.toRegister(thirdPartUserInfo, wxLoginParams);
            if (loginReturn != null) {
                return ResultUtil.success(loginReturn);
            }
        }
        User user = userService.getUserInfoByUid(record.getUid());
        String token = tokenBuilder.createToken(user);
        if (user != null) {
            return ResultUtil.success(new LoginReturn<>(user, token));
        }
        return ResultUtil.error("登录失败", ResultEnum.PARAMS_ERROR);
    }


    public List<String> callRemoteInterFaceForWxLoginStepOne(WxLoginParams wxLoginParams) {
        List resultList = new ArrayList();
        String wechatAppId = appIdConfig.getWechatAppId();
        String wechatSecret = appIdConfig.getWechatSecret();
        String wechatGrantType = appIdConfig.getWechatGrantType();
        //微信用code获取access_token的url
        String wxOpenApiUrl = appIdConfig.getWxOpenApiUrl();
        //微信用access_token和openid获取用户信息的url
        String wxOpenApiUrl2 = appIdConfig.getWxOpenApiUrl2();
        String getAccessTokenUrl = wxOpenApiUrl + "?appid=" + wechatAppId + "&secret=" + wechatSecret
                + "&code=" + wxLoginParams.getCode() + "&grant_type" + wechatGrantType;
        String str = restTemplate.getForObject(getAccessTokenUrl, JSONObject.class, String.class).toString();
        Map map = JsonUtil.jsonToMap(str);
        String openId = map.get("openid") != null ? map.get("openid").toString() : "";
        String accessToken = map.get("access_token") != null ? map.get("").toString() : "";
        resultList.add(openId);
        resultList.add(accessToken);
        return resultList;
    }

    private ThirdPartUserInfo callRemoteInterFaceForWxLoginStepTwo(List<String> list, WxLoginParams wxLoginParams) {
        ThirdPartUserInfo thirdPartUserInfo = new ThirdPartUserInfo();
        String wxOpenApiUrl2 = appIdConfig.getWxOpenApiUrl2();
        String getInfoUrl = wxOpenApiUrl2 + "?access_token=" + list.get(1) + "&openid=" + list.get(0);
        String result = restTemplate.getForObject(getInfoUrl, JSONObject.class, String.class).toString();
        Map resultMap = JsonUtil.jsonToMap(result);
        thirdPartUserInfo.setName(resultMap.get("nickname") != null ? resultMap.get("nickname").toString() : "");
        thirdPartUserInfo.setUrl(resultMap.get("headimgurl") != null ? resultMap.get("headimgurl").toString() : "");
        return thirdPartUserInfo;
    }
}
