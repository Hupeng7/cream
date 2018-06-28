package com.icecream.user.service;

import com.icecream.user.mapper.UserAuthMapper;
import com.icecreamGroup.common.model.User;
import com.icecreamGroup.common.model.UserAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr_h
 * @version 1.0
 * description: 用户登陆方式
 * create by Mr_h on 2018/6/25 0025
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class UserAuthService {

    @Autowired
    private UserAuthMapper userAuthMapper;

    public Integer insertUserAuthByType(User user, Integer type) {
        UserAuth userAuth = new UserAuth();
        userAuth.setUid(user.getId());
        userAuth.setIdentityType(type);
        userAuth.setUid(user.getId());
        if (type == 1) {
            userAuth.setIdentifier(user.getItucode() + user.getPhone());

            if (user.getPassword() != null) {
                userAuth.setCredential(user.getPassword());
            }
        } else {
            userAuth.setIdentifier(user.getOpenid());
        }
        return userAuthMapper.insertSelective(userAuth);
    }

    public UserAuth get(Integer uid,String password){
        UserAuth arg = new UserAuth();
        arg.setUid(uid);
        arg.setCredential(password);
        UserAuth result = userAuthMapper.selectOne(arg);
        return result;
    }

    public UserAuth getByIdentifer(Integer uid,String identifer){
        UserAuth arg = new UserAuth();
        arg.setUid(uid);
        arg.setIdentifier(identifer);
        UserAuth result = userAuthMapper.selectOne(arg);
        return result;
    }

    public UserAuth getByType(Integer uid,Integer type){
        UserAuth arg = new UserAuth();
        arg.setUid(uid);
        arg.setIdentityType(type);
        UserAuth result = userAuthMapper.selectOne(arg);
        return result;
    }

}
