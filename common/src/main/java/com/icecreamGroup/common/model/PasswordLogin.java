package com.icecreamGroup.common.model;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 使用用户名和密码登陆的实体类
 * create by Mr_h on 2018/6/12 0012
 */

@Data
public class PasswordLogin extends LoginBaseParams{

    private String itucode;

    private String phone;

    private String password;

}
