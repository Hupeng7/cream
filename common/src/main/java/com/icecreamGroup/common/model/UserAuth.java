package com.icecreamGroup.common.model;

import lombok.Data;

import javax.persistence.Table;

/**
 * 用户登陆记录表
 */
@Data
@Table(name = "user_auth")
public class UserAuth {

    private Integer id;

    private Integer uid;

    private Integer identityType;

    private String identifier;

    private String credential;

    private Boolean isSync;


}