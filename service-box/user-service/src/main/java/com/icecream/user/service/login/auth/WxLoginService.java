package com.icecream.user.service.login.auth;


import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.model.LoginReturn;
import com.icecream.common.model.model.ThirdPartUserInfo;
import com.icecream.common.model.model.WxLoginParams;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.service.UserService;
import com.icecream.user.service.binding.UserAuthService;
import com.icecream.user.service.login.AbstractLoginSupport;
import com.icecream.user.service.login.SuperLogin;
import com.icecream.user.service.register.UserRegisterService;
import com.icecream.user.utils.jwt.TokenBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.icecream.user.constants.Constants.TYPE_AUTH_WX;

/**
 * @version 2.0
 */
@Service
@SuppressWarnings("all")
public class WxLoginService extends AbstractLoginSupport implements SuperLogin<WxLoginParams> {

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

    @Autowired
    private UserAuthService userAuthService;

    @Override
    public ResultVO login(WxLoginParams wxLoginParams) {
        String msg = vaild(wxLoginParams);
        if (StringUtils.isBlank(msg)) {
            List<String> strings = callRemoteInterFaceForWxLoginStepOne(wxLoginParams);
            if (strings == null) return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
            UserAuth record = userRegisterService.isHaveBeenRegistered(strings.get(0), wxLoginParams.getType());
            if (null == record) {
                ThirdPartUserInfo thirdPartUserInfo = callRemoteInterFaceForWxLoginStepTwo(strings, wxLoginParams);
                User user = cover(thirdPartUserInfo, wxLoginParams, strings.get(0));
                Integer integer = userRegisterService.registerForFans(user, user.getOpenid(), wxLoginParams.getType());
                if (integer > 0) {
                    LoginReturn loginReturn = buildLoginSuccessReturn(user);
                    return ResultUtil.success(loginReturn);
                } else {
                    return ResultUtil.error(null, ResultEnum.MYSQL_OPERATION_FAILED);
                }
            } else {
                User user = userService.getUserInfoByUid(record.getUid());
                return ResultUtil.success(buildLoginSuccessReturn(user));
            }
        } else {
            return ResultUtil.error(msg, ResultEnum.PARAMS_ERROR);
        }
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
                + "&code=" + wxLoginParams.getCode() + "&grant_type=" + wechatGrantType;
        String str = restTemplate.getForObject(getAccessTokenUrl, String.class, String.class).toString();
        Map map = JsonUtil.jsonToMap(str);
        String openId = map.get("openid") != null ? map.get("openid").toString() : "";
        String accessToken = map.get("access_token") != null ? map.get("access_token").toString() : "";
        if (StringUtils.isNotBlank(openId) & StringUtils.isNotBlank(accessToken)) {
            resultList.add(openId);
            resultList.add(accessToken);
        }
        return resultList;
    }

    private ThirdPartUserInfo callRemoteInterFaceForWxLoginStepTwo(List<String> list, WxLoginParams wxLoginParams) {
        ThirdPartUserInfo thirdPartUserInfo = new ThirdPartUserInfo();
        String wxOpenApiUrl2 = appIdConfig.getWxOpenApiUrl2();
        String getInfoUrl = wxOpenApiUrl2 + "?access_token=" + list.get(1) + "&openid=" + list.get(0);
        String result = restTemplate.getForObject(getInfoUrl, String.class, String.class).toString();
        Map resultMap = JsonUtil.jsonToMap(result);
        thirdPartUserInfo.setName(resultMap.get("nickname") != null ? resultMap.get("nickname").toString() : "");
        thirdPartUserInfo.setUrl(resultMap.get("headimgurl") != null ? resultMap.get("headimgurl").toString() : "");
        return thirdPartUserInfo;
    }

    private User cover(ThirdPartUserInfo thirdPartUserInfo, WxLoginParams wxLoginParams, String openId) {
        User user = new User();
        user.setNickname(thirdPartUserInfo.getName());
        user.setAvatar(thirdPartUserInfo.getUrl());
        user.setPhonemodel(wxLoginParams.getPhoneModel());
        user.setRegister(wxLoginParams.getRegister());
        user.setRegisterType(wxLoginParams.getRegisterType());
        user.setOpenid(openId);
        int time = (int) LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        user.setCtime(time);
        user.setLastlogintime(time);
        user.setMtime(0);
        return user;
    }

    private String vaild(WxLoginParams wxLoginParams) {
        if (StringUtils.isBlank(wxLoginParams.getCode())) return "code不能为空";
        if (wxLoginParams.getType() != TYPE_AUTH_WX) return "登录类型不符合";
        if (StringUtils.isBlank(wxLoginParams.getPhoneType())) return "phoneType不能为空";
        if (StringUtils.isBlank(wxLoginParams.getRegister())) return "register不能为空";
        if (null == wxLoginParams.getRegisterType()) return "registerType不能为空";
        if (null == wxLoginParams.getPhoneModel()) return "phoneModel不能为空";
        return "";
    }


}
