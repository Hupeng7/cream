package com.icecream.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.tx.annotation.TxTransaction;
import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.feignclients.OrderFeignClient;
import com.icecream.user.mapper.UserMapper;
import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.utils.UserBuilder;
import com.icecream.user.utils.jwt.JwtBuilder;
import com.icecreamGroup.common.model.*;
import com.icecreamGroup.common.util.json.JsonUtil;
import com.icecream.user.mapper.UserAuthMapper;
import com.icecream.user.mapper.UserRegisterMapper;
import com.icecream.user.sms.SmsSender;
import com.icecreamGroup.common.util.redis.FastJson2JsonRedisSerializer;
import com.icecreamGroup.common.util.redis.RedisHandler;
import com.icecreamGroup.common.util.res.ResultEnum;
import com.icecreamGroup.common.util.res.ResultUtil;
import com.icecreamGroup.common.util.res.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings("all")
public class UserService {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserRegisterService userRegisterService;

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
    private RedisHandler redisHandler;

    @Autowired
    private FastJson2JsonRedisSerializer fastJson2JsonRedisSerializer;

    @Autowired
    private SmsSender smsSender;

    @Autowired
    private JwtBuilder jwtBuilder;

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
    public LoginReturn login(PasswordLogin passwordLogin) {
        UserAuth userAuth = new UserAuth();
        userAuth.setIdentityType(2);
        userAuth.setIdentifier(passwordLogin.getItucode() + passwordLogin.getPhone());
        UserAuth resualt = userAuthMapper.selectOne(userAuth);
        if (resualt != null) {
            User user = new User();
            user.setId(userAuth.getUid());
            User record = userMapper.selectOne(user);
            if (record != null) {
                LoginReturn loginReturn = new LoginReturn();
                loginReturn.setToken(jwtBuilder.createToken(user));
                loginReturn.setUser(user);
                return loginReturn;
            }
        }
        return null;
    }


    public LoginReturn oauthLoginAndRegister(ThirdPartyLoginParam thirdPartyLoginParam) throws Exception {
        //数据校验与转换 每种登陆方式传递参数不一致 需要转换成请求第三方用户接口的最终形态
        ThirdPartyDataTransform thirdPartyDataTransform = checkAndTransformByType(thirdPartyLoginParam);
        if (thirdPartyDataTransform == null) return null;
        //先去user_auth表中查询，如果存在直接返回
        UserAuth userAuth = getUserAuth(thirdPartyDataTransform.getOpenid(), thirdPartyDataTransform.getType());
        if (userAuth == null) {
            //获取第三方用户数据
            ThirdPartUserInfo thirdPartUserInfo = getUserInfoByThirdPartyAPi(thirdPartyDataTransform);
            //注册用户并返回token
            return registerUser(builderThirdPartyUser(thirdPartyLoginParam, thirdPartUserInfo, thirdPartyDataTransform),
                    thirdPartyLoginParam.getIdentityType());
        } else {
            //直接从记录(数据库)中返回数据
            return getUserInfoByRecord(userAuth);
        }
    }

    public LoginReturn oauthLogin(ThirdPartyLoginParam thirdPartyLoginParam) throws Exception {
        //数据校验与转换 每种登陆方式传递参数不一致 需要转换成请求第三方用户接口的最终形态
        ThirdPartyDataTransform thirdPartyDataTransform = checkAndTransformByType(thirdPartyLoginParam);
        if (thirdPartyDataTransform == null) return null;
        //先去user_auth表中查询，如果存在直接返回
        UserAuth userAuth = getUserAuth(thirdPartyDataTransform.getOpenid(), thirdPartyDataTransform.getType());
        if (userAuth != null) {
            User user = new User();
            user.setId(userAuth.getUid());
            User result = userMapper.selectOne(user);
            if (result != null) {
                LoginReturn loginReturn = new LoginReturn();
                loginReturn.setToken(jwtBuilder.createToken(user));
                loginReturn.setUser(user);
                return loginReturn;
            }
        }
        return null;
    }


    private LoginReturn getUserInfoByRecord(UserAuth userAuth) {
        LoginReturn loginReturn = new LoginReturn();
        User userSelect = new User();
        userSelect.setId(userAuth.getUid());
        User user = userMapper.selectOne(userSelect);
        loginReturn.setToken(jwtBuilder.createToken(user));
        loginReturn.setUser(user);
        return loginReturn;
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
        if (regiterType != null) {
            thirdPartyDataTransform.setRegisterType(regiterType);
        }
        return null;
    }

    //微博&&QQ
    private ThirdPartyDataTransform checkAndTransForm(String id, String token, Integer type, Integer registerType) {
        ThirdPartyDataTransform thirdPartyDataTransform = new ThirdPartyDataTransform();
        thirdPartyDataTransform.setOpenid(id);
        thirdPartyDataTransform.setToken(token);
        thirdPartyDataTransform.setType(type);
        if (registerType != null) {
            thirdPartyDataTransform.setRegisterType(registerType);
        }
        return null;
    }

    private User builderThirdPartyUser(ThirdPartyLoginParam thirdPartyLoginParam,
                                       ThirdPartUserInfo thirdPartUserInfo,
                                       ThirdPartyDataTransform thirdPartyDataTransform) {
        User user = new User();
        user.setNickname(thirdPartUserInfo.getName());
        user.setAvatar(thirdPartUserInfo.getUrl());
        user.setPhonemodel(thirdPartyLoginParam.getPhoneModel());
        user.setRegister(thirdPartyLoginParam.getRegister());
        user.setRegisterType(thirdPartyLoginParam.getRegisterType());
        user.setOpenid(thirdPartyDataTransform.getOpenid());
        User result = userMapper.selectOne(user);
        return result == null ? user : null;
    }

    //插入user、user_auth、user_register表数据
    public LoginReturn registerUser(User user, Integer type) throws RuntimeException {
        if (user != null) {
            LoginReturn loginReturn = new LoginReturn();
            Long time = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
            user.setCtime(time.intValue());
            user.setMtime(time.intValue());
            user.setLastlogintime(time.intValue());
            int userCount = userMapper.insertSelective(user);
            User userInfo = userMapper.getCache(user.getId());
            //存入数据到redis，key为uid
            setUserInfoToRedis(userInfo);
            int userAuthCount = userAuthService.insertUserAuthByType(user, type);
            int userRegisterCount = userRegisterService.insertRegisterByUserId(user);
            if (userAuthCount > 0 && userRegisterCount > 0) {
                String token = jwtBuilder.createToken(user);
                loginReturn.setUser(user);
                loginReturn.setToken(token);
                return loginReturn;
            }
        }
        throw new RuntimeException("插入失败");
    }

    private void setUserInfoToRedis(User user) {
        try {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(user);
            redisHandler.set(user.getId(), jsonObject);
        } catch (Exception e) {
            log.error("用户信息存入redis时失败");
            e.printStackTrace();
        }
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

    public Boolean sendCode(String itucode, String phone) {
        String s = smsSender.smsSend(itucode, phone);
        if (StringUtils.isNotBlank(s)) {
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public LoginReturn fastLogin(SmsLoginParams smsLoginParams) throws Exception {
        int mirror = Integer.parseInt(redisHandler.get(smsLoginParams.getItuCode() + smsLoginParams.getPhone()).toString());
        if (smsLoginParams.getCode().intValue() == mirror) {
            UserAuth userAuth = new UserAuth();
            userAuth.setIdentifier(smsLoginParams.getItuCode() + smsLoginParams.getPhone());
            userAuth.setIdentityType(1);
            UserAuth result = userAuthMapper.selectOne(userAuth);
            if (result == null) {
                return registerUser(builderPhoneUser(smsLoginParams), 1);
            } else {
                LoginReturn loginReturn = new LoginReturn();
                User user = new User();
                user.setId(result.getUid());
                User record = userMapper.selectOne(user);
                loginReturn.setUser(record);
                loginReturn.setToken(jwtBuilder.createToken(user));
                return loginReturn;
            }
        }
        return null;
    }

    public LoginReturn toRigster(SmsLoginParams smsLoginParams) throws Exception {
        int mirror = Integer.parseInt(redisHandler.get(smsLoginParams.getItuCode() + smsLoginParams.getPhone()).toString());
        if (smsLoginParams.getCode().intValue() == mirror) {
            UserAuth userAuth = new UserAuth();
            userAuth.setIdentityType(1);
            if (userAuth.getCredential() != null) {
                userAuth.setCredential(smsLoginParams.getPassword());
            }
            userAuth.setIdentifier(smsLoginParams.getItuCode() + smsLoginParams.getPhone());
            UserAuth resualt = userAuthMapper.selectOne(userAuth);
            if (resualt == null) {
                return registerUser(builderPhoneUser(smsLoginParams), 1);
            } else {
                return null;
            }
        }
        return null;
    }

    private User builderPhoneUser(SmsLoginParams smsLoginParams) {
        User user = new User();
        user.setRegister(smsLoginParams.getRegister());
        user.setPhone(smsLoginParams.getPhone());
        user.setItucode(smsLoginParams.getItuCode());
        user.setRegisterType(smsLoginParams.getRegisterType());
        user.setPhonetype(smsLoginParams.getPhoneType());
        user.setPhonemodel(smsLoginParams.getPhoneModel());
        user.setPassword(smsLoginParams.getPassword());
        return user;
    }
    //------------------------------登陆业务结束--------------------------------->

    public ResultVO get(Integer uid) {
        if (uid == null) return ResultUtil.error(null, ResultEnum.TOKEN_INFO_ERROR);
        User result = getUserInfoByUid(uid);
        if (result != null) {
            return ResultUtil.success(result);
        } else {
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }
    }

    public ResultVO update(User user, Integer uid) {
        if (uid == null) return ResultUtil.error(null, ResultEnum.TOKEN_INFO_ERROR);
        User result = getUserInfoByUid(uid);
        if (result != null) {
            if (user.getNickname() != null) {
                if (user.getNickname() == result.getNickname() || user.getNickname().equals(result.getNickname())) {
                    return ResultUtil.error(null, ResultEnum.NAME_REPETITION);
                }
            }
            user.setId(uid);
            int count = userMapper.updateByPrimaryKeySelective(user);
            if (count > 0) {
                return ResultUtil.success(user);
            }
        }
        return null;
    }

    public User getUserInfoByUid(Integer uid) {
        User user = new User();
        user.setId(uid);
        User result = userMapper.selectOne(user);
        return result;
    }

    public ResultVO isSetPassword(String itucode, String phone, Integer uid) {
        if (uid == null) return ResultUtil.error(null, ResultEnum.TOKEN_INFO_ERROR);
        User user = getUserInfoByUid(uid);
        if (user != null) {
            UserAuth userAuth = new UserAuth();
            userAuth.setUid(user.getId());
            List<UserAuth> select = userAuthMapper.select(userAuth);
            List<UserAuth> result = select.stream()
                    .filter(l -> l.getCredential() != null)
                    .collect(Collectors.toList());
            if (result.isEmpty())
                return ResultUtil.success(false);
            return ResultUtil.success(true);

        } else {
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }
    }

    public ResultVO binding(BindingModel bindingModel, Integer uid) {
        if (uid == null) return ResultUtil.error(null, ResultEnum.TOKEN_INFO_ERROR);
        ThirdPartyLoginParam thirdPartyLoginParam = new ThirdPartyLoginParam();
        if (bindingModel.getCode() != null) thirdPartyLoginParam.setCode(bindingModel.getCode());
        if (bindingModel.getOpenId() != null) thirdPartyLoginParam.setOpenId(bindingModel.getOpenId());
        if (bindingModel.getUid() != null) thirdPartyLoginParam.setUid(bindingModel.getUid());
        thirdPartyLoginParam.setAccessToken(bindingModel.getAccessToken());
        thirdPartyLoginParam.setIdentityType(bindingModel.getIdentityType());
        ThirdPartyDataTransform thirdPartyDataTransform = checkAndTransformByType(thirdPartyLoginParam);
        if (thirdPartyDataTransform == null) return null;
        User user = getUserInfoByUid(uid);
        if (user != null) {
            UserAuth userAuth = new UserAuth();
            userAuth.setUid(user.getId());
            userAuth.setIdentifier(thirdPartyDataTransform.getOpenid());
            userAuth.setIdentityType(thirdPartyDataTransform.getType());
            int count = userAuthMapper.insertSelective(userAuth);
            if (count > 0) {
                return ResultUtil.success();
            }
        } else {
            return ResultUtil.error(null, ResultEnum.TOKEN_INFO_ERROR);
        }
        return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
    }

    public ResultVO unbinding(Integer type, Integer uid) {
        if (uid == null) return ResultUtil.error(null, ResultEnum.TOKEN_INFO_ERROR);
        User user = getUserInfoByUid(uid);
        if (user != null) {
            UserAuth userAuth = new UserAuth();
            userAuth.setId(user.getId());
            userAuth.setIdentityType(type);
            int count = userAuthMapper.delete(userAuth);
            if (count > 0) {
                return ResultUtil.success();
            } else {
                return ResultUtil.error("null", ResultEnum.DATA_ERROR);
            }
        } else {
            return ResultUtil.error(null, ResultEnum.DATA_ERROR);
        }
    }

    public ResultVO getUserInfo(Integer uid) {
        if (uid == null) return ResultUtil.error(null, ResultEnum.TOKEN_INFO_ERROR);
        try {
            User user = (User) redisHandler.get(uid.toString());
            if (user != null)
                return ResultUtil.success(user);
            throw new RuntimeException("redis中数据为空");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从redis中取数据出错,开始从mysql中取得数据,错误{}", e.getStackTrace());
            try {
                User cache = userMapper.getCache(uid);
                if (cache != null)
                    return ResultUtil.success(cache);
                return ResultUtil.error(null, ResultEnum.DATA_ERROR);
            } catch (Exception e1) {
                log.error("从mysql中取数据出错,错误{}", e.getStackTrace());
                e1.printStackTrace();
            }
        }
        return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);

    }


}
