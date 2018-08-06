package com.icecream.user.service.token;

import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.model.requstbody.TokenInfo;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserMapper;
import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.utils.jwt.JwtHelper;
import com.icecream.user.utils.jwt.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/17 0017
 */
@Service
@SuppressWarnings("all")
public class UserTokenService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserStarMapper userStarMapper;

    @Autowired
    private JwtProperties jwtProperties;

    public ResultVO checkStar(String token){
        if(token==null){return ResultUtil.error(null,ResultEnum.PARAMS_ERROR);}
        TokenInfo tokenInfo = JwtHelper.parseJWT(token, jwtProperties.getStarSecret());
        if(tokenInfo!=null){
            UserStar userStar = new UserStar();
            userStar.setId(tokenInfo.getUid());
            UserStar result = userStarMapper.selectOne(userStar);
            if(result!=null){
                return ResultUtil.success(tokenInfo);
            }
        }
        return ResultUtil.error(null,ResultEnum.PARAMS_ERROR);
    }

    public ResultVO checkConsumer(String token){
        if(token==null){return ResultUtil.error(null,ResultEnum.PARAMS_ERROR);}
        TokenInfo tokenInfo = JwtHelper.parseJWT(token, jwtProperties.getCustomerSecret());
        if(tokenInfo!=null){
            User user = new User();
            user.setId(tokenInfo.getUid());
            User result = userMapper.selectOne(user);
            if(result!=null){
                return ResultUtil.success(tokenInfo);
            }
        }
        return ResultUtil.error(null,ResultEnum.PARAMS_ERROR);
    }
}
