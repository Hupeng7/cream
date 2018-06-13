package com.icecreamGroup.common.model;

import lombok.Data;

import javax.persistence.Table;

/**
 * @author Mr_h
 * @version 1.0
 * description: 版主用户测试类
 * create by Mr_h on 2018/6/13 0013
 */
@Data
@Table(name = "user_star1")
public class UserStar {

    private Integer id;
    private String name;
    private String password;
    private Integer uid;
    private Integer type;
}
