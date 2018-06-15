package com.icecreamGroup.common.model;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 第三方登陆返回给前端的实体类
 * create by Mr_h on 2018/6/14 0014
 */
@Data
public class ThirdPartyLoginReturn{

    private String name;
    private String url;
    private String token;

    public ThirdPartyLoginReturn(String name,String url,String token){
        this.name = name;
        this.url=url;
        this.token =token;
    }
    public ThirdPartyLoginReturn(){};
}
