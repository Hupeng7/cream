package com.icecream.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.tx.annotation.TxTransaction;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.pojo.UserRegister;
import com.icecream.common.model.requstbody.*;
import com.icecream.common.redis.RedisHandler;
import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.feignclients.OrderFeignClient;
import com.icecream.user.mapper.UserMapper;
import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.utils.UserBuilder;
import com.icecream.user.utils.jwt.TokenBuilder;
import com.icecream.user.utils.time.DateUtil;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.user.mapper.UserAuthMapper;
import com.icecream.user.mapper.UserRegisterMapper;
import com.icecream.user.sms.AuthCodeHandler;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.utils.vaild.Checker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.icecream.user.constants.Constans.TYPE_SMS;

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
    private AuthCodeHandler authCodeHandler;

    @Autowired
    private TokenBuilder tokenBuilder;

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
    public ResultVO login(PasswordLogin passwordLogin) {
        UserAuth userAuth = new UserAuth();
        userAuth.setIdentityType(TYPE_SMS);
        userAuth.setIdentifier(passwordLogin.getItucode() + passwordLogin.getPhone());
        UserAuth resualt = userAuthMapper.selectOne(userAuth);
        if (resualt != null) {
            User user = new User();
            user.setId(resualt.getUid());
            User record = userMapper.selectOne(user);
            if (record != null) {
                LoginReturn loginReturn = new LoginReturn();
                loginReturn.setToken(tokenBuilder.createToken(user));
                loginReturn.setUser(user);
                return ResultUtil.success(loginReturn);
            }
        }
        return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
    }


    public ResultVO oauthLoginAndRegister(ThirdPartyLoginParam thirdPartyLoginParam){
        //数据校验与转换 每种登陆方式传递参数不一致 需要转换成请求第三方用户接口的最终形态
        ThirdPartyDataTransform thirdPartyDataTransform = checkAndTransformByType(thirdPartyLoginParam);
        if (thirdPartyDataTransform == null) return null;
        //先去user_auth表中查询，如果存在直接返回
        UserAuth userAuth = getUserAuth(thirdPartyDataTransform.getOpenid(), thirdPartyDataTransform.getType());
        if (userAuth == null) {
            //获取第三方用户数据
            ThirdPartUserInfo thirdPartUserInfo = getUserInfoByThirdPartyAPi(thirdPartyDataTransform);
            //注册用户并返回token
            return ResultUtil.success(registerUser(builderThirdPartyUser(thirdPartyLoginParam, thirdPartUserInfo, thirdPartyDataTransform),
                    thirdPartyLoginParam.getIdentityType()));
        } else {
            //直接从记录(数据库)中返回数据
            return ResultUtil.success(getUserInfoByRecord(userAuth));
        }
    }

    public ResultVO oauthLogin(ThirdPartyLoginParam thirdPartyLoginParam){
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
                loginReturn.setToken(tokenBuilder.createToken(user));
                loginReturn.setUser(user);
                return ResultUtil.success(loginReturn);
            }
        }
        return ResultUtil.error(null,ResultEnum.PARAMS_ERROR);
    }


    private LoginReturn getUserInfoByRecord(UserAuth userAuth) {
        LoginReturn loginReturn = new LoginReturn();
        User userSelect = new User();
        userSelect.setId(userAuth.getUid());
        User user = userMapper.selectOne(userSelect);
        loginReturn.setToken(tokenBuilder.createToken(user));
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
                String token = tokenBuilder.createToken(user);
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
            RedisHandler.set(user.getId(), jsonObject);
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
        //根据token&&openid获取用户信息。
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

    public ResultVO sendCode(String itucode, String phone) {
        String s = authCodeHandler.smsSend(itucode, phone);
        if (StringUtils.isNotBlank(s)) {
            return ResultUtil.success(true);
        }
        return ResultUtil.error(false,ResultEnum.SMS_CODE_SEND_FAILED);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO fastLogin(SmsLoginParams smsLoginParams) {
        int mirror = Integer.parseInt(RedisHandler.get(smsLoginParams.getItuCode() + smsLoginParams.getPhone()).toString());
        if (smsLoginParams.getCode().intValue() == mirror) {
            UserAuth userAuth = new UserAuth();
            userAuth.setIdentifier(smsLoginParams.getItuCode() + smsLoginParams.getPhone());
            userAuth.setIdentityType(TYPE_SMS);
            UserAuth result = userAuthMapper.selectOne(userAuth);
            if (result == null) {
                return ResultUtil.success(registerUser(builderPhoneUser(smsLoginParams), TYPE_SMS));
            } else {
                LoginReturn loginReturn = new LoginReturn();
                User user = new User();
                user.setId(result.getUid());
                User record = userMapper.selectOne(user);
                loginReturn.setUser(record);
                loginReturn.setToken(tokenBuilder.createToken(user));
                return ResultUtil.success(loginReturn);
            }
        }
        return ResultUtil.error(null,ResultEnum.PARAMS_ERROR);
    }

    public ResultVO toRigster(SmsLoginParams smsLoginParams) {
        int mirror = Integer.parseInt(RedisHandler.get(smsLoginParams.getItuCode() + smsLoginParams.getPhone()).toString());
        if (smsLoginParams.getCode().intValue() == mirror) {
            UserAuth userAuth = new UserAuth();
            userAuth.setIdentityType(TYPE_SMS);
            if (userAuth.getCredential() != null) {
                userAuth.setCredential(smsLoginParams.getPassword());
            }
            userAuth.setIdentifier(smsLoginParams.getItuCode() + smsLoginParams.getPhone());
            UserAuth resualt = userAuthMapper.selectOne(userAuth);
            if (resualt == null) {
                return ResultUtil.success(registerUser(builderPhoneUser(smsLoginParams), TYPE_SMS));
            } else {
                return ResultUtil.error(null,ResultEnum.EXIST_ACCOUNT);
            }
        }
        return ResultUtil.error(null,ResultEnum.WRONG_CODE);
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
        User result = getUserInfoByUid(uid);
        if (result != null) {
            return ResultUtil.success(result);
        } else {
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }
    }

    public ResultVO update(User user) {
        Boolean vaild = Checker.checkUpateUser(user);
        if(!vaild) return ResultUtil.success();
        //此步操作是为了判断user中是否只有id有值，如果是这种情况，不允许更新操作
        User result = getUserInfoByUid(user.getId());
        if (result != null) {
            if (user.getNickname() != null) {
                if (user.getNickname() == result.getNickname() || user.getNickname().equals(result.getNickname())) {
                    return ResultUtil.error(null, ResultEnum.NAME_REPETITION);
                }
            }
            user.setId(result.getId());
            int count = userMapper.updateByPrimaryKeySelective(user);
            if (count > 0) {
                User u = getUserInfoByUid(user.getId());
                setUserInfoToRedis(u);
                return ResultUtil.success(u);
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
        if(uid==null) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
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

    public ResultVO binding(BindingModel bindingModel) {
        ThirdPartyLoginParam thirdPartyLoginParam = new ThirdPartyLoginParam();
        if (bindingModel.getCode() != null) thirdPartyLoginParam.setCode(bindingModel.getCode());
        if (bindingModel.getOpenId() != null) thirdPartyLoginParam.setOpenId(bindingModel.getOpenId());
        if (bindingModel.getUid() != null) thirdPartyLoginParam.setUid(bindingModel.getUid());
        thirdPartyLoginParam.setAccessToken(bindingModel.getAccessToken());
        thirdPartyLoginParam.setIdentityType(bindingModel.getIdentityType());
        ThirdPartyDataTransform thirdPartyDataTransform = checkAndTransformByType(thirdPartyLoginParam);
        if (thirdPartyDataTransform == null) return null;
        User user = getUserInfoByUid(bindingModel.getId());
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
        if(uid==null) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
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

    public ResultVO getAllAuths(Integer uid) {
        if(uid==null) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
        UserAuth userAuth = new UserAuth();
        userAuth.setUid(uid);
        List<UserAuth> select = userAuthMapper.select(userAuth);
        if (!select.isEmpty()) {
            return ResultUtil.success(select);
        } else {
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }
    }

    public ResultVO<Object> getUserInfo(Integer uid) {
        try {
            if(uid==null) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
            Object o = RedisHandler.get(uid);
            if (o != null)
                return ResultUtil.success(o);
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
                return ResultUtil.error(null, ResultEnum.DATA_ERROR);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO updatePhone(Integer uid, String itucode, String phone) {
        if(uid==null) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
        User user = new User();
        user.setId(uid);
        user.setItucode(itucode);
        user.setPhone(phone);
        int count1 = userMapper.updateByPrimaryKeySelective(user);
        UserAuth userAuth = new UserAuth();
        userAuth.setIdentifier(itucode + phone);
        userAuth.setUid(uid);
        Example example = new Example(UserAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uid", uid);
        int count2 = userAuthMapper.updateByExampleSelective(userAuth, example);
        if (count1 > 0 & count2 > 0) {
            User args = new User();
            args.setId(uid);
            User result = userMapper.selectOne(args);
            return ResultUtil.success(result);
        } else {
            return ResultUtil.error(null, ResultEnum.MYSQL_OPERATION_FAILED);
        }
    }

    public ResultVO updatePassword(Password password) {
        UserAuth result = userAuthService.get(password.getId(), password.getOldPassword());
        if (result != null) {
            result.setCredential(password.getNewPassword());
            int count = userAuthMapper.updateByPrimaryKey(result);
            if (count > 0) {
                return ResultUtil.success(result);
            }
        } else {
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }
        return ResultUtil.error(null, ResultEnum.MYSQL_OPERATION_FAILED);
    }

    public ResultVO updateByCodeAndPasswrod(SmsSupplement smsLoginParams) {
        String key = smsLoginParams.getItuCode() + smsLoginParams.getPhone().toString();
        Boolean result = authCodeHandler.checkCode(key, smsLoginParams.getCode());
        if (result) {
            User arg = new User();
            arg.setId(smsLoginParams.getId());
            User user = userMapper.selectOne(arg);
            if (user.getPhone().equals(smsLoginParams.getPhone())) {
                UserAuth userAuth = userAuthService.getByIdentifer(smsLoginParams.getId(), key);
                if (userAuth != null) {
                    UserAuth authArgs = new UserAuth();
                    authArgs.setId(userAuth.getId());
                    authArgs.setCredential(smsLoginParams.getPassword());
                    int count = userAuthMapper.updateByPrimaryKeySelective(authArgs);
                    if (count > 0) {
                        return ResultUtil.success(count);
                    }
                }
            } else {
                return ResultUtil.error(null, ResultEnum.ERROR_PHONE);
            }
        } else {
            return ResultUtil.error(null, ResultEnum.DATA_ERROR);
        }
        return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
    }

    public ResultVO ifExistPhone(String phone, Integer uid) {
        if(uid==null) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
        UserAuth result = userAuthService.getByIdentifer(uid, phone);
        if (result != null) {
            return ResultUtil.success(true);
        } else {
            return ResultUtil.success(false);
        }
    }

    public ResultVO checkPhoneInfo(Phone phone) {
        UserAuth userAuth = userAuthService.getByIdentifer(phone.getId(), phone.getItucode() + phone.getPhone());
        if (userAuth != null) {
            User user = new User();
            user.setId(phone.getId());
            User result = userMapper.selectOne(user);
            return ResultUtil.error(result, ResultEnum.EXIST_BINDING);
        } else {
            authCodeHandler.smsSend(phone.getItucode(), phone.getPhone());
            return ResultUtil.success();
        }
    }

    public ResultVO checkPassword(Phone phone) {
        UserAuth userAuth = userAuthService.getByIdentifer(phone.getId(), phone.getItucode() + phone.getPhone());
        SmsSendResult smsSendResult = new SmsSendResult();
        if (userAuth != null) {
            smsSendResult.setExistPhone(true);
            if (Integer.parseInt(userAuth.getCredential()) != -1) {
                smsSendResult.setExistPwd(true);
            }
        } else {
            smsSendResult.setExistPwd(false);
            authCodeHandler.smsSend(phone.getItucode(), phone.getPhone());
            smsSendResult.setExistPhone(false);
        }
        if (smsSendResult.getExistPhone()) {
            return ResultUtil.error(smsSendResult, ResultEnum.EXIST_ACCOUNT);
        } else {
            return ResultUtil.success(smsSendResult);
        }
    }


    //换新手机时调用的接口
    @Transactional(rollbackFor = Exception.class)
    public ResultVO changePhones(SmsSupplement smsLoginParams) {
        String phone = smsLoginParams.getItuCode() + smsLoginParams.getPhone();
            User user = getUserInfoByUid(smsLoginParams.getId());
            if (user != null) {
                User args = new User();
                args.setItucode(smsLoginParams.getItuCode());
                args.setPhone(smsLoginParams.getPhone());
                args.setId(user.getId());
                int userUpdate = userMapper.updateByPrimaryKeySelective(args);
                UserAuth record = userAuthService.getByType(smsLoginParams.getId(), TYPE_SMS);
                record.setIdentifier(phone);
                int userAuthUpdate = userAuthMapper.updateByPrimaryKeySelective(record);
                UserRegister userRegister = userRegisterService.get(smsLoginParams.getId(), TYPE_SMS);
                userRegister.setRegister(smsLoginParams.getRegister());
                int registerUpdate = userRegisterMapper.updateByPrimaryKeySelective(userRegister);
                if (userUpdate > 0 & userAuthUpdate > 0 & registerUpdate > 0) {
                    LoginReturn loginReturn = new LoginReturn();
                    loginReturn.setToken(tokenBuilder.createToken(user));
                    loginReturn.setUser(getUserInfoByUid(smsLoginParams.getId()));
                    User userInfo = userMapper.getCache(user.getId());
                    RedisHandler.set(smsLoginParams.getId(), userInfo);
                    return ResultUtil.success(loginReturn);
                }
            }
        return null;
    }

    public ResultVO checkCode(Integer uid, Integer code) {
        if(uid==null) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
        User user = getUserInfoByUid(uid);
        if (user != null) {
            Boolean mirror = authCodeHandler.checkCode(user.getItucode() + user.getPhone(), code);
            if (mirror) {
                return ResultUtil.success(true);
            } else {
                return ResultUtil.error(null, ResultEnum.CODE_AUTHENTICATION_FAILED);
            }
        }
        return ResultUtil.error(null, ResultEnum.QUERY_RESULT_IS_NULL);
    }

    public ResultVO GetTheTotalNumberOfUsers() {
        int count = userMapper.selectCount(null);
        return ResultUtil.success(count);
    }

    public ResultVO getTheNumberOfUsersInWeek(Integer year, Integer week) {
        Date startTime = DateUtil.getFirstDayOfWeek(year, week);
        Date endTime = DateUtil.getLastDayOfWeek(year, week);
        BaseTimeSection timeSection = DateUtil.getTimeSection(startTime.getTime(), endTime.getTime());
        Integer count = userMapper.getUserCountInWeek(timeSection.getStartTime(), timeSection.getEndTime());
        return ResultUtil.success(count);
    }


    public ResultVO changeUserStatus(PersonStatusInfo personStatusInfo) {
        User user = new User();
        user.setId(personStatusInfo.getUid());
        user.setStatus(personStatusInfo.getStatus());
        int userUpdate = userMapper.updateByPrimaryKeySelective(user);
        if (userUpdate > 0) {
            return ResultUtil.success(userUpdate);
        } else {
            return ResultUtil.error(null, ResultEnum.MYSQL_OPERATION_FAILED);
        }
    }


}


