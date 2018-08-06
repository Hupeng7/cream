package com.icecream.user.service.login.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.model.requstbody.AccountLoginParams;
import com.icecream.common.model.requstbody.LoginReturn;
import com.icecream.common.redis.RedisHandler;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.service.login.SuperLogin;
import com.icecream.user.utils.jwt.TokenBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


/**
 * @version 2.0
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class AccountLoginService implements SuperLogin<AccountLoginParams> {

    @Autowired
    private UserStarMapper userStarMapper;

    @Autowired
    private TokenBuilder tokenBuilder;

    @Autowired
    private RedisHandler redisHandler;

    @Override
    public ResultVO login(@Validated AccountLoginParams accountLoginParams) {
        log.info("account---登陆。。。");
        UserStar userStar = new UserStar();
        userStar.setUsername(accountLoginParams.getAccount());
        userStar.setPassword(accountLoginParams.getPassword());
        UserStar result = userStarMapper.selectOne(userStar);
        if(result.getPassword().equals(accountLoginParams.getPassword())){
            UserStar cache = userStarMapper.getCache(result.getId());
            RedisHandler.set(cache.getId(),cache);
            LoginReturn loginReturn = new LoginReturn();
            loginReturn.setAdmin(result);
            loginReturn.setToken(tokenBuilder.createToken(result));
            return ResultUtil.success(loginReturn);
        }
        return null;
    }


    private void setUserStarInfoToRedis(UserStar userStar) {
        try {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(userStar);
            redisHandler.set(userStar.getId()*(-1), jsonObject);
        } catch (Exception e) {
            log.error("用户信息存入redis时失败");
            e.printStackTrace();
        }
    }
}
