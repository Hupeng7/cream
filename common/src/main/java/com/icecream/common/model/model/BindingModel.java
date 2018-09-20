package com.icecream.common.model.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr_h
 * @version 2.0
 * description: 绑定第三方平台实体类
 * create by Mr_h on 2018/6/22 0022
 */
@Data
@ApiModel(value="绑定对象",description="绑定时json串转化的实体类")
public class BindingModel {

    @ApiModelProperty(name = "identityType",value = "登录方式类型",required = true)
    private Integer identityType;

    //QQ
    @ApiModelProperty(name = "openId",value = "qq登录的openId")
    private String openId;

    //微信
    @ApiModelProperty(name = "code",value = "微信登录的openId")
    private String code;

    //微博
    @ApiModelProperty(name = "uid",value = "微博登录的openId")
    private String uid;

    @ApiModelProperty(name = "accessToken",value = "微信登录第一步获取的token")
    private String accessToken;
}
