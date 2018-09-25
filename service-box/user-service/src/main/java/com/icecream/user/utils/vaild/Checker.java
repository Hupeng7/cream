package com.icecream.user.utils.vaild;

import com.icecream.common.model.pojo.User;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/18 0018
 */
public class Checker {

    public static Boolean checkUpdateUser(User user){
        if(user.getNickname()==null&user.getAvatar()==null&
           user.getSmallavatar()==null&user.getSex()==null&
           user.getBirthday()==null&user.getProfessional()==null&
           user.getAddress()==null){
            return false;
        }
        return true;
    }
}
