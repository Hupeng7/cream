package com.icecream.user.service;

import com.icecream.user.mapper.UserRegisterMapper;
import com.icecreamGroup.common.model.User;
import com.icecreamGroup.common.model.UserRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr_h
 * @version 1.0
 * description: 用户手机推送
 * create by Mr_h on 2018/6/25 0025
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class UserRegisterService {

    @Autowired
    private UserRegisterMapper userRegisterMapper;


    public int insertRegisterByUserId(User user) {
        UserRegister userRegister = new UserRegister();
        userRegister.setUid(user.getId());
        userRegister.setRegister(user.getRegister());
        userRegister.setRegisterType(user.getRegisterType());
        return userRegisterMapper.insertSelective(userRegister);
    }

    public UserRegister get(Integer uid,Integer type){
        UserRegister userRegister = new UserRegister();
        userRegister.setUid(uid);
        userRegister.setRegisterType(type);
        UserRegister result = userRegisterMapper.selectOne(userRegister);
        return result;
    }
}
