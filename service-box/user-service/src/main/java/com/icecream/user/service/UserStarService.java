package com.icecream.user.service;

import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.utils.jwt.TokenBuilder;
import com.icecream.user.utils.time.DateUtil;
import com.icecreamGroup.common.model.LoginReturn;
import com.icecreamGroup.common.model.SimpleLogin;
import com.icecreamGroup.common.model.UserStar;
import com.icecreamGroup.common.util.redis.RedisHandler;
import com.icecreamGroup.common.util.res.ResultEnum;
import com.icecreamGroup.common.util.res.ResultUtil;
import com.icecreamGroup.common.util.res.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/28 0028
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class UserStarService {

    @Autowired
    private UserStarMapper userStarMapper;

    @Autowired
    private RedisHandler redisHandler;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private TokenBuilder tokenBuilder;

    public ResultVO<Object> getUserStarInfo(Integer uid) {
        try {
            Object o = redisHandler.get(uid);
            if (o != null)
                return ResultUtil.success(o);
            throw new RuntimeException("redis中数据为空");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从redis中取数据出错,开始从mysql中取得数据,错误{}", e.getStackTrace());
            try {
                UserStar cache = userStarMapper.getCache(uid);
                if (cache != null)
                    return ResultUtil.success(cache);
                return ResultUtil.error(null, ResultEnum.QUERY_RESULT_IS_NULL);
            } catch (Exception e1) {
                log.error("从mysql中取数据出错,错误{}", e.getStackTrace());
                e1.printStackTrace();
                return ResultUtil.error(null, ResultEnum.MYSQL_OPERATION_FAILED);
            }
        }
    }


    public ResultVO login(SimpleLogin simpleLogin) {
        UserStar userStar = new UserStar();
        userStar.setUsername(simpleLogin.getAccount());
        UserStar result = userStarMapper.selectOne(userStar);
        if(result.getPassword()==simpleLogin.getPassword()){
            UserStar cache = userStarMapper.getCache(result.getId());
            redisHandler.set(result.getId(),cache);
            return ResultUtil.success();
        }
        return null;
    }

    public ResultVO add(UserStar userStar) {
        UserStar result = userStarMapper.selectOne(userStar);
        if (result == null) {
            Integer now = DateUtil.getNowTimeBySecond();
            userStar.setCtime(now);userStar.setMtime(now);userStar.setLastlogintime(now);
            int insertStar = userStarMapper.insertSelective(userStar);
            if (insertStar > 0 ) {
                LoginReturn loginReturn = new LoginReturn();
                loginReturn.setUser(userStar);
                loginReturn.setToken(tokenBuilder.createToken(userStar));
                return ResultUtil.success(loginReturn);
            }
        } else {
            return ResultUtil.error(null, ResultEnum.NAME_REPETITION);
        }
        return ResultUtil.error(null,ResultEnum.MYSQL_OPERATION_FAILED);
    }

}
