package com.icecreamGroup.common.model;

import lombok.Data;

import javax.persistence.Table;

/**
 * 用户极光推送关联表
 */
@Data
@Table(name = "user_register")
public class UserRegister {
    private Integer id;

    private Integer uid;

    private Integer registerType;

    private String register;

    private Integer status;
}