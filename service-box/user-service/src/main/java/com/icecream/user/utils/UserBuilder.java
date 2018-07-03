package com.icecream.user.utils;

import com.icecream.common.model.pojo.User;

public class UserBuilder {

    public static User buildUser(){
        User user = new User();
        user.setId(2);
        return user;
    }
}
