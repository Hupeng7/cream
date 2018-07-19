package com.icecream.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.requstbody.LoginReturn;
import com.icecream.common.model.requstbody.PersonStatusInfo;
import com.icecream.common.model.requstbody.SimpleLogin;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.redis.RedisHandler;
import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.utils.jwt.TokenBuilder;
import com.icecream.user.utils.time.DateUtil;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
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
    private UserAuthService userAuthService;

    @Autowired
    private TokenBuilder tokenBuilder;

    public ResultVO<Object> getUserStarInfo(String tid) {
        Integer uid = Integer.parseInt(tid);
        try {
            Object o = RedisHandler.get(uid*(-1));
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
        if(result.getPassword().equals(simpleLogin.getPassword())){
            UserStar cache = userStarMapper.getCache(result.getId());
            setUserStarInfoToRedis(cache);
            LoginReturn loginReturn = new LoginReturn();
            loginReturn.setUser(result);
            loginReturn.setToken(tokenBuilder.createToken(result));
            return ResultUtil.success(loginReturn);
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
                return ResultUtil.success(null);
            }
        } else {
            return ResultUtil.error(null, ResultEnum.NAME_REPETITION);
        }
        return ResultUtil.error(null,ResultEnum.MYSQL_OPERATION_FAILED);
    }


    private void setUserStarInfoToRedis(UserStar userStar) {
        try {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(userStar);
            RedisHandler.set(userStar.getId()*(-1), jsonObject);
        } catch (Exception e) {
            log.error("用户信息存入redis时失败");
            e.printStackTrace();
        }
    }

    public ResultVO changeStarStatus(PersonStatusInfo personStatusInfo){
        UserStar star = new UserStar();
        star.setId(personStatusInfo.getUid());
        star.setStatus(personStatusInfo.getStatus());
        int statUpdate = userStarMapper.updateByPrimaryKeySelective(star);
        if(statUpdate>0){
            return ResultUtil.success(statUpdate);
        }else {
            return ResultUtil.error(null,ResultEnum.MYSQL_OPERATION_FAILED);
        }
    }
}
