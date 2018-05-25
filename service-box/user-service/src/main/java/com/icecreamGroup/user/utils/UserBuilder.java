package com.icecreamGroup.user.utils;

import com.icecreamGroup.common.model.User;

public class UserBuilder {

    public static User buildUser(){
        User user = new User();
        user.setId(2);
        user.setName("四六");
        user.setPassword("123");
        return user;
    }
}
