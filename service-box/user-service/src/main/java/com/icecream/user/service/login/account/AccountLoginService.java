package com.icecream.user.service.login.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.model.model.AccountLoginParams;
import com.icecream.common.model.model.LoginReturn;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.redis.RedisHandler;
import com.icecream.user.service.login.AbstractLoginSupport;
import com.icecream.user.service.login.SuperLogin;
import com.icecream.user.utils.charge.StringUtil;
import com.icecream.user.utils.jwt.TokenBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

import static com.icecream.user.constants.Constants.TYPE_ACCOUNT;


/**
 * @version 2.0
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class AccountLoginService extends AbstractLoginSupport implements SuperLogin<AccountLoginParams> {

    @Autowired
    private UserStarMapper userStarMapper;

    @Autowired
    private TokenBuilder tokenBuilder;

    @Autowired
    private RedisHandler redisHandler;

    @Override
    public ResultVO login(AccountLoginParams accountLoginParams) {
        log.info("账号密码登录准备就绪...");
        String msg = vaild(accountLoginParams);
        if (StringUtils.isBlank(msg)) {
            UserStar result = userStarMapper.selectOne(buildUserStar(accountLoginParams));
            if (result != null) {
                if (result.getPassword() != null) {
                    if (result.getPassword().equals(accountLoginParams.getPassword())) {
                        UserStar userStarBriefInfoInfo = userStarMapper.getUserStarBriefInfo(result.getId());
                        redisHandler.set(userStarBriefInfoInfo.getId() * (-1), userStarBriefInfoInfo);
                        return ResultUtil.success(buildLoginSuccessReturn(userStarBriefInfoInfo));
                    }
                }
            }
            return ResultUtil.error("账号密码不正确", ResultEnum.ERROR_ACCOUNT_OR_PASSWORD);
        }
        return ResultUtil.error(msg, ResultEnum.PARAMS_ERROR);
    }

    private UserStar buildUserStar(AccountLoginParams accountLoginParams) {
        UserStar userStar = new UserStar();
        userStar.setUsername(accountLoginParams.getAccount());
        userStar.setPassword(accountLoginParams.getPassword());
        return userStar;
    }

    private String vaild(AccountLoginParams accountLoginParams) {
        if (StringUtils.isBlank(accountLoginParams.getAccount())) return "登录账号不能为空";
        if (StringUtils.isBlank(accountLoginParams.getPassword())) return "登录密码不能为空";
        if (accountLoginParams.getType() != TYPE_ACCOUNT) return "登录类型不符合";
        return "";
    }
}
