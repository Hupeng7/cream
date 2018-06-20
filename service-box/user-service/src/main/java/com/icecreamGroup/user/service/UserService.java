package com.icecreamGroup.user.service;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.tx.annotation.TxTransaction;
import com.icecreamGroup.common.model.*;
import com.icecreamGroup.common.util.json.JsonUtil;
import com.icecreamGroup.common.util.jwt.JwtHelper;
import com.icecreamGroup.user.config.login.AppIdConfig;
import com.icecreamGroup.user.mapper.UserAuthMapper;
import com.icecreamGroup.user.mapper.UserMapper;
import com.icecreamGroup.user.feignClients.OrderFeignClient;
import com.icecreamGroup.user.mapper.UserRegisterMapper;
import com.icecreamGroup.user.mapper.UserStarMapper;
import com.icecreamGroup.user.sms.SmsSender;
import com.icecreamGroup.user.utils.UserBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Slf4j
@Service
@SuppressWarnings("all")
public class UserService {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserStarMapper userStarMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private UserRegisterMapper userRegisterMapper;

    @Autowired
    private AppIdConfig appIdConfig;

    @Autowired
    private SmsSender smsSender;

    @TxTransaction(isStart = true)
    @Transactional
    public Integer insert() {
        int count1 = userMapper.insert(UserBuilder.buildUser());
        int count2 = orderFeignClient.insert();
        if (count1 > 0 && count2 > 0) {
            log.info("插入成功");
            return 1;
        } else {
            log.error("插入失败");
            return 0;
        }
    }


    //--------------------------登陆业务开始------------------------------------->
    public String login(PasswordLogin loginArgs) {
        if (loginArgs.getType() == 1) {
            UserStar args = new UserStar();
            args.setName(loginArgs.getUserName());
            args.setPassword(loginArgs.getPassword());
            UserStar star = userStarMapper.selectOne(args);
            if (star != null) {
                //查出user,生成token
                return JwtHelper.createJWTForStar(3600000000L, "star", star);
            } else {
                //查不出user,拒绝登陆
                return "";
            }
        } else {
            User args = new User();
            args.setNickname(loginArgs.getUserName());
            //args.setPassword(loginArgs.getPassword());
            User user = userMapper.selectOne(args);
            if (user != null) {
                //查出user,生成token
                return JwtHelper.createJWT(3600000000L, "customer", user);
            } else {
                //查不出user,拒绝登陆
                return "";
            }
        }
    }

    public ThirdPartyLoginReturn oauthLogin(ThirdPartyLoginParam thirdPartyLoginParam) {
        //数据校验与转换 每种登陆方式传递参数不一致 需要转换成请求第三方用户接口的最终形态
        ThirdPartyDataTransform thirdPartyDataTransform = checkAndTransformByType(thirdPartyLoginParam);
        if (thirdPartyDataTransform == null) return null;
        //先去user_auth表中查询，如果存在直接返回
        UserAuth userAuth = getUserAuth(thirdPartyDataTransform.getOpenid(), thirdPartyDataTransform.getType());
        if (userAuth == null) {
            //获取第三方用户数据
            ThirdPartUserInfo thirdPartUserInfo = getUserInfoByThirdPartyAPi(thirdPartyDataTransform);
            //注册用户并返回token
            String token = registerUser(thirdPartUserInfo, thirdPartyDataTransform);
            return new ThirdPartyLoginReturn(thirdPartUserInfo.getName(), thirdPartUserInfo.getUrl(),
                    token);
        } else {
            //直接从记录(数据库)中返回数据
            return getUserInfoByRecord(userAuth);
        }
    }

    private ThirdPartyLoginReturn getUserInfoByRecord(UserAuth userAuth) {
        ThirdPartyLoginReturn thirdPartyLoginReturn = new ThirdPartyLoginReturn();
        User userSelect = new User();
        userSelect.setId(userAuth.getUid());
        User user = userMapper.selectOne(userSelect);
        thirdPartyLoginReturn.setToken(JwtHelper.createJWT(3600000000L, "customer", user));
        thirdPartyLoginReturn.setName(user.getNickname());
        thirdPartyLoginReturn.setUrl(user.getAvatar());
        return thirdPartyLoginReturn;
    }

    //获取授权表
    private UserAuth getUserAuth(String openid, Integer type) {
        UserAuth select = new UserAuth();
        select.setIdentifier(openid);
        select.setIdentityType(type);
        return userAuthMapper.selectOne(select);
    }

    private ThirdPartyDataTransform checkAndTransformByType(ThirdPartyLoginParam thirdPartyLoginParam) {
        switch (thirdPartyLoginParam.getIdentityType()) {
            //微信
            case 3:
                return checkAndTransForm(thirdPartyLoginParam.getCode(),
                        thirdPartyLoginParam.getIdentityType(),
                        thirdPartyLoginParam.getRegisterType());
            //微博
            case 4:
                return checkAndTransForm(thirdPartyLoginParam.getUid(),
                        thirdPartyLoginParam.getAccessToken(),
                        thirdPartyLoginParam.getIdentityType(),
                        thirdPartyLoginParam.getRegisterType());
            //QQ
            case 5:
                return checkAndTransForm(thirdPartyLoginParam.getOpenId(),
                        thirdPartyLoginParam.getAccessToken(),
                        thirdPartyLoginParam.getIdentityType(),
                        thirdPartyLoginParam.getRegisterType());
            default:
                return null;
        }
    }

    //微信
    private ThirdPartyDataTransform checkAndTransForm(String code, Integer type, Integer regiterType) {
        String wechatAppId = appIdConfig.getWechatAppId();
        String wechatSecret = appIdConfig.getWechatSecret();
        String wechatGrantType = appIdConfig.getWechatGrantType();
        //微信用code获取access_token的url
        String wxOpenApiUrl = appIdConfig.getWxOpenApiUrl();
        //微信用access_token和openid获取用户信息的url
        String wxOpenApiUrl2 = appIdConfig.getWxOpenApiUrl2();
        String getAccessTokenUrl = wxOpenApiUrl + "?appid=" + wechatAppId + "&secret=" + wechatSecret
                + "&code=" + code + "&grant_type" + wechatGrantType;
        String str = restTemplate.getForObject(getAccessTokenUrl, JSONObject.class, String.class).toString();
        Map map = JsonUtil.jsonToMap(str);
        String openId = map.get("openid") != null ? map.get("openid").toString() : "";
        String accessToken = map.get("access_token") != null ? map.get("").toString() : "";
        ThirdPartyDataTransform thirdPartyDataTransform = new ThirdPartyDataTransform();
        thirdPartyDataTransform.setOpenid(openId);
        thirdPartyDataTransform.setToken(accessToken);
        thirdPartyDataTransform.setType(type);
        thirdPartyDataTransform.setRegisterType(regiterType);
        return null;
    }

    //微博&&QQ
    private ThirdPartyDataTransform checkAndTransForm(String id, String token, Integer type, Integer registerType) {
        ThirdPartyDataTransform thirdPartyDataTransform = new ThirdPartyDataTransform();
        thirdPartyDataTransform.setOpenid(id);
        thirdPartyDataTransform.setToken(token);
        thirdPartyDataTransform.setType(type);
        thirdPartyDataTransform.setRegisterType(registerType);
        return null;
    }

    //插入user、user_auth、user_register表数据
    @Transactional
    public String registerUser(ThirdPartUserInfo thirdPartUserInfo, ThirdPartyDataTransform thirdPartyDataTransform) {
        User user = new User();
        user.setNickname(thirdPartUserInfo.getName());
        user.setAvatar(thirdPartUserInfo.getUrl());
        Long time = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        user.setCtime(time.intValue());
        user.setMtime(time.intValue());
        user.setLastlogintime(time.intValue());
        int userCount = userMapper.insertSelective(user);
        if (userCount <= 0) return null;
        UserAuth userAuth = new UserAuth();
        userAuth.setIdentityType(thirdPartyDataTransform.getType());
        userAuth.setUid(user.getId());
        userAuth.setIdentifier(thirdPartyDataTransform.getOpenid());
        int userAuthCount = userAuthMapper.insertSelective(userAuth);
        UserRegister userRegister = new UserRegister();
        userRegister.setUid(user.getId());
        userRegister.setRegister(thirdPartyDataTransform.getOpenid());
        userRegister.setRegisterType(thirdPartyDataTransform.getRegisterType());
        int userRegisterCount = userRegisterMapper.insertSelective(userRegister);
        if (userAuthCount > 0 && userRegisterCount > 0) {
            return JwtHelper.createJWT(3600000000L, "customer", user);
        }
        return null;
    }

    private ThirdPartUserInfo getUserInfoByThirdPartyAPi(ThirdPartyDataTransform thirdPartyDataTransform) {
        Integer type = thirdPartyDataTransform.getType();
        switch (type) {
            //请求微信接口
            case 3:
                return getUserInfoByWechat(thirdPartyDataTransform.getOpenid(), thirdPartyDataTransform.getToken(), type);
            //请求微博接口
            case 4:
                return getUserInfoByWeibo(thirdPartyDataTransform.getOpenid(), thirdPartyDataTransform.getToken(), type);
            //请求qq接口
            case 5:
                return getUserInfoByQQ(thirdPartyDataTransform.getOpenid(), thirdPartyDataTransform.getOpenid(), type);
            default:
                log.error("未知type");
                return null;
        }
    }

    private ThirdPartUserInfo getUserInfoByWechat(String accessToken, String openId, Integer type) {
        ThirdPartUserInfo thirdPartUserInfo = new ThirdPartUserInfo();
        String wxOpenApiUrl = appIdConfig.getWxOpenApiUrl2();
        String getInfoUrl = wxOpenApiUrl + "?access_token=" + accessToken + "&openid=" + openId;
        String str = restTemplate.getForObject(getInfoUrl, JSONObject.class, String.class).toString();
        Map map = JsonUtil.jsonToMap(str);
        thirdPartUserInfo.setName(map.get("nickname") != null ? map.get("nickname").toString() : "");
        thirdPartUserInfo.setUrl(map.get("headimgurl") != null ? map.get("headimgurl").toString() : "");
        return thirdPartUserInfo;
    }

    private ThirdPartUserInfo getUserInfoByWeibo(String token, String uid, Integer type) {
        ThirdPartUserInfo thirdPartUserInfo = new ThirdPartUserInfo();
        String url = appIdConfig.getWeiboOpenApiUrl() + "?access_token=" + token + "&uid=" + uid;
        String str = restTemplate.getForObject(url, JSONObject.class, String.class).toString();
        Map map = JsonUtil.jsonToMap(str);
        thirdPartUserInfo.setName(map.get("name") != null ? map.get("name").toString() : "");
        thirdPartUserInfo.setUrl(map.get("profile_image_url") != null ? map.get("profile_image_url").toString() : "");
        return thirdPartUserInfo;
    }

    private ThirdPartUserInfo getUserInfoByQQ(String token, String openid, Integer type) {
        ThirdPartUserInfo thirdPartUserInfo = new ThirdPartUserInfo();
        //根据token&&openid获取用户信息
        String qqOpenApiUrl2 = appIdConfig.getQqOpenApiUrl2();
        String qQappId = appIdConfig.getQQappId();
        //根据token获取openid的url
        String getInfourl = qqOpenApiUrl2 + "access_token=" + token + "&oauth_consumer_key=" + qQappId + "&openid=" + openid;
        String str = restTemplate.getForObject(getInfourl, JSONObject.class, String.class).toString();
        Map map = JsonUtil.jsonToMap(str);
        thirdPartUserInfo.setName(map.get("nickname") != null ? map.get("nickname").toString() : "");
        thirdPartUserInfo.setUrl(map.get("figureurl_qq_1") != null ? map.get("figureurl_qq_1").toString() : "");
        return thirdPartUserInfo;
    }


    public Boolean sendCode(String phone) {
        String s = smsSender.SmsSend(phone);
        if (StringUtils.isNotBlank(s)) {
            return true;
        }
        return false;
    }
    //------------------------------登陆业务结束--------------------------------->

}
