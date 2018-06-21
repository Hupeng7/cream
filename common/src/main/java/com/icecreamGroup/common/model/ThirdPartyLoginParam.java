package com.icecreamGroup.common.model;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * @author Mr_h
 * @version 1.0
 * description: 第三方登陆实体类
 * create by Mr_h on 2018/6/14 0014
 */
@Data
public class ThirdPartyLoginParam extends LoginBaseParams{

    @NotNull(message = "登陆方式不能为空")
    private Integer identityType; //登陆方式 3微信 4微博 5qq

    private String accessToken;//第三方登陆token
    private String openId; //第三方用户ID(可获取第三方用户数据)
    private String uid; //微博的openId;
    private String code;//微信的获取openId中间数据
}
