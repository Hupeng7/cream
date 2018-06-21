package com.icecreamGroup.common.model;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 从第三方登陆参数中提取需要的数据的实体类
 * create by Mr_h on 2018/6/20 0020
 */
@Data
public class ThirdPartyDataTransform {

    private String openid;

    private String token;

    private Integer type;

    private String phone; //冗余字段 验证码登陆

    private Integer registerType; //推送方式 1极光 2百度 3网易云 4蝴蝶云
}
