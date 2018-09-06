package com.icecream.common.model.model;

import lombok.Data;

/**
 * @author Mr_h
 * @version 2.0
 * description: 绑定第三方平台实体类
 * create by Mr_h on 2018/6/22 0022
 */
@Data
public class BindingModel {

    private Integer identityType;

    //QQ
    private String openId;

    //微信
    private String code;

    //微博
    private String uid;

    private String accessToken;
}
