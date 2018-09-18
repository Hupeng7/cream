package com.icecream.user.service.login.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.model.model.AccountLoginParams;
import com.icecream.common.model.model.LoginReturn;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.redis.RedisHandler;
import com.icecream.user.service.login.AbstractLoginSupport;
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
public class AccountLoginService extends AbstractLoginSupport implements SuperLogin<AccountLoginParams>{

    @Autowired
    private UserStarMapper userStarMapper;

    @Autowired
    private TokenBuilder tokenBuilder;

    @Autowired
    private RedisHandler redisHandler;

    @Override
    public ResultVO login(@Validated AccountLoginParams accountLoginParams) {
        log.info("账号密码登录准备就绪...");
        UserStar result = userStarMapper.selectOne(buildUserStar(accountLoginParams));
        if(result!=null&result.getPassword()!=null&result.getPassword().equals(accountLoginParams.getPassword())){
            UserStar userStarBriefInfoInfo = userStarMapper.getUserStarBriefInfo(result.getId());
            redisHandler.set(userStarBriefInfoInfo.getId()*(-1),userStarBriefInfoInfo);
            return ResultUtil.success(buildLoginSuccessReturn(userStarBriefInfoInfo));
        }
        return null;
    }

    private UserStar buildUserStar(AccountLoginParams accountLoginParams){
        UserStar userStar = new UserStar();
        userStar.setUsername(accountLoginParams.getAccount());
        userStar.setPassword(accountLoginParams.getPassword());
        return userStar;
    }
}
