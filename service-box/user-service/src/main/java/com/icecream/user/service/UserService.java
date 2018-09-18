package com.icecream.user.service;

import com.codingapi.tx.annotation.TxTransaction;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.pojo.UserPush;
import com.icecream.common.model.model.*;
import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.feignclients.OrderFeignClient;
import com.icecream.user.mapper.UserMapper;
import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.redis.RedisHandler;
import com.icecream.user.service.binding.UserAuthService;
import com.icecream.user.service.code.CodeHandler;
import com.icecream.user.service.push.UserPushService;
import com.icecream.user.utils.jwt.TokenBuilder;
import com.icecream.user.mapper.UserAuthMapper;
import com.icecream.user.mapper.UserPushMapper;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.utils.vaild.Checker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

import static com.icecream.user.constants.Constants.TYPE_SMS;

/**
 * @version 2.0
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class UserService {

    @Autowired
    private RedisHandler redisHandler;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserPushService userPushService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserStarMapper userStarMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private UserPushMapper userPushMapper;

    @Autowired
    private AppIdConfig appIdConfig;

    @Autowired
    private CodeHandler codeHandler;

    @Autowired
    private TokenBuilder tokenBuilder;

  /*  @TxTransaction(isStart = true)
    @Transactional
    public Integer insert() {
        int count1 = userMapper.insert(UserBuilder.buildUser());
        int count2 = 1;
        if (count1 > 0 && count2 > 0) {
            log.info("插入成功");
            return 1;
        } else {
            log.error("插入失败");
            return 0;
        }
    }*/

    public User getUserInfoByUid(Integer uid) {
        User user = new User();
        user.setId(uid);
        User result = userMapper.selectOne(user);
        return result;
    }

    public ResultVO get(Integer uid) {
        User result = getUserInfoByUid(uid);
        if (result != null) {
            return ResultUtil.success(result);
        } else {
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }
    }

    public List<User> getList(){
        return userMapper.selectAll();
    }

    public ResultVO update(User user,String uid) {
        Integer id = Integer.parseInt(uid);
        Boolean vaild = Checker.checkUpateUser(user);
        if(!vaild) return ResultUtil.success();
        //此步操作是为了判断user中是否只有id有值，如果是这种情况，不允许更新操作
        User result = getUserInfoByUid(id);
        if (result != null) {
            if (user.getNickname() != null) {
                if (user.getNickname() == result.getNickname() || user.getNickname().equals(result.getNickname())) {
                    return ResultUtil.error(null, ResultEnum.NAME_REPETITION);
                }
            }
            user.setId(result.getId());
            int count = userMapper.updateByPrimaryKeySelective(user);
            if (count > 0) {
                User u = getUserInfoByUid(id);
                redisHandler.set(u.getId(),u);
                return ResultUtil.success(u);
            }
        }
        return null;
    }

    public ResultVO isSetPassword(String itucode, String phone, String uid) {
        if(uid.equals("")) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
        User user = getUserInfoByUid(Integer.valueOf(uid));
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

    public ResultVO<Object> getUserInfo(String uid) {
        try {
            if(uid==null||uid.equals("")) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
            Object o = redisHandler.get(uid);
            if (o != null)
                return ResultUtil.success(o);
            throw new RuntimeException("redis中数据为空");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从redis中取数据出错,开始从mysql中取得数据,错误{}", e.getStackTrace());
            try {
                User cache = userMapper.getCache(Integer.parseInt(uid));
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
    public ResultVO updatePhone(String tid, String itucode, String phone) {
        if(tid==null||tid.equals("")) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
        Integer uid = Integer.parseInt(tid);
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

    public ResultVO updatePassword(Password password,String uid) {
        UserAuth result = userAuthService.get(Integer.parseInt(uid), password.getOldPassword());
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

    public ResultVO updateByCodeAndPasswrod(SmsLoginOrRegisterParams smsLoginOrRegisterParams, String tid) {
        Integer uid = Integer.parseInt(tid);
        String key = smsLoginOrRegisterParams.getItucode() + smsLoginOrRegisterParams.getPhone().toString();
        Boolean result = codeHandler.check(key, smsLoginOrRegisterParams.getCode());
        if (result) {
            User arg = new User();
            arg.setId(uid);
            User user = userMapper.selectOne(arg);
            if (user.getPhone().equals(smsLoginOrRegisterParams.getPhone())) {
                UserAuth userAuth = userAuthService.getByIdentifer(uid, key);
                if (userAuth != null) {
                    UserAuth authArgs = new UserAuth();
                    authArgs.setId(userAuth.getId());
                    authArgs.setCredential(smsLoginOrRegisterParams.getPassword());
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

    public ResultVO ifExistPhone(String phone, String uid) {
        UserAuth result = userAuthService.getByIdentifer(Integer.parseInt(uid), phone);
        if (result != null) {
            return ResultUtil.success(true);
        } else {
            return ResultUtil.success(false);
        }
    }

    public ResultVO checkPhoneInfo(Phone phone,String tid) {
        Integer uid = Integer.parseInt(tid);
        UserAuth userAuth = userAuthService.getByIdentifer(uid, phone.getItucode() + phone.getPhone());
        if (userAuth != null) {
            User user = new User();
            user.setId(uid);
            User result = userMapper.selectOne(user);
            return ResultUtil.error(result, ResultEnum.EXIST_BINDING);
        } else {
            codeHandler.send(phone.getItucode(), phone.getPhone());
            return ResultUtil.success();
        }
    }

    public ResultVO checkPassword(Phone phone,String uid) {
        UserAuth userAuth = userAuthService.getByIdentifer(Integer.parseInt(uid), phone.getItucode() + phone.getPhone());
        SmsSendResult smsSendResult = new SmsSendResult();
        if (userAuth != null) {
            smsSendResult.setExistPhone(true);
            if (Integer.parseInt(userAuth.getCredential()) != -1) {
                smsSendResult.setExistPwd(true);
            }
        } else {
            smsSendResult.setExistPwd(false);
            codeHandler.send(phone.getItucode(), phone.getPhone());
            smsSendResult.setExistPhone(false);
        }
        if (smsSendResult.getExistPhone()) {
            return ResultUtil.error(smsSendResult, ResultEnum.EXIST_ACCOUNT);
        } else {
            return ResultUtil.success(smsSendResult);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO changePhones(SmsLoginOrRegisterParams smsLoginOrRegisterParams, String tid) {
        Integer uid = Integer.parseInt(tid);
        String phone = smsLoginOrRegisterParams.getItucode() + smsLoginOrRegisterParams.getPhone();
            User user = getUserInfoByUid(uid);
            if (user != null) {
                User args = new User();
                args.setItucode(smsLoginOrRegisterParams.getItucode());
                args.setPhone(smsLoginOrRegisterParams.getPhone());
                args.setId(user.getId());
                int userUpdate = userMapper.updateByPrimaryKeySelective(args);
                UserAuth record = userAuthService.getByType(uid, TYPE_SMS);
                record.setIdentifier(phone);
                int userAuthUpdate = userAuthMapper.updateByPrimaryKeySelective(record);
                UserPush userPush = userPushService.get(uid, TYPE_SMS);
                userPush.setRegister(smsLoginOrRegisterParams.getRegister());
                int registerUpdate = userPushMapper.updateByPrimaryKeySelective(userPush);
                if (userUpdate > 0 & userAuthUpdate > 0 & registerUpdate > 0) {
                    LoginReturn loginReturn = new LoginReturn();
                    loginReturn.setToken(tokenBuilder.createToken(user));
                    loginReturn.setAdmin(getUserInfoByUid(uid));
                    User userInfo = userMapper.getCache(user.getId());
                    redisHandler.set(uid, userInfo);
                    return ResultUtil.success(loginReturn);
                }
            }
        return null;
    }

    public ResultVO checkCode(String uid, Integer code) {
        if(uid==null||uid.equals("")) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
        User user = getUserInfoByUid(Integer.parseInt(uid));
        if (user != null) {
            Boolean mirror = codeHandler.check(user.getItucode() + user.getPhone(), code);
            if (mirror) {
                return ResultUtil.success(true);
            } else {
                return ResultUtil.error(null, ResultEnum.CODE_AUTHENTICATION_FAILED);
            }
        }
        return ResultUtil.error(null, ResultEnum.QUERY_RESULT_IS_NULL);
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


