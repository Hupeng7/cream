package com.icecreamGroup.common.model;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 第三方登陆处理完成返回的实体类
 * create by Mr_h on 2018/6/14 0014
 */
@Data
public class LoginReturn {

    private String token;

    private User user;
}