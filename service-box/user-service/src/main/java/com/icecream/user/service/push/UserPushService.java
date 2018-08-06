package com.icecream.user.service.push;

import com.icecream.user.mapper.UserPushMapper;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr_h
 * @version 2.0
 * description: 用户手机推送
 * create by Mr_h on 2018/6/25 0025
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class UserPushService {

    @Autowired
    private UserPushMapper userPushMapper;


    public int insertUserPushByUserId(User user) {
        UserPush userPush = new UserPush();
        userPush.setUid(user.getId());
        userPush.setRegister(user.getRegister());
        userPush.setRegisterType(user.getRegisterType());
        return userPushMapper.insertSelective(userPush);
    }

    public UserPush get(Integer uid, Integer type){
        UserPush userPush = new UserPush();
        userPush.setUid(uid);
        userPush.setRegisterType(type);
        UserPush result = userPushMapper.selectOne(userPush);
        return result;
    }

}
