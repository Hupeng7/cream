package com.icecream.user.service.token;

import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.model.model.TokenInfo;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserMapper;
import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.utils.jwt.JwtHelper;
import com.icecream.user.utils.jwt.JwtProperties;
import com.icecream.user.utils.jwt.TokenBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/17 0017
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class UserTokenService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserStarMapper userStarMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private TokenBuilder tokenBuilder;


    public UserStar checkStar(Integer uid) {
        UserStar userStar = new UserStar();
        userStar.setId(uid);
        return userStarMapper.selectOne(userStar);
    }

    public User checkConsumer(Integer uid) {
        User user = new User();
        user.setId(uid);
        return userMapper.selectOne(user);
    }

    public String getToken(Integer uid) {
        User user = new User();
        user.setId(uid);
        User result = userMapper.selectOne(user);
        String token = tokenBuilder.createToken(result);
        return token;
    }

    public String getStarToken(Integer uid) {
        UserStar userStar = new UserStar();
        userStar.setId(uid);
        UserStar result = userStarMapper.selectOne(userStar);
        String token = tokenBuilder.createToken(result);
        return token;
    }
}
