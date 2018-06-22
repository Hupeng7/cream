package com.icecreamGroup.common.model;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 绑定第三方平台实体类
 * create by Mr_h on 2018/6/22 0022
 */
@Data
public class BindingModel {

    private Integer identityType;

    private String openId;

    private String code;

    private String uid;

    private String accessToken;
}
