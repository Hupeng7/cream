package com.icecreamGroup.common.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/14 0014
 */
@Data
public class ThirdPartyLoginParam {
    @NotBlank(message = "手机型号不能为空")
    private String phoneType; //手机型号
    @NotNull(message = "手机系统类型不能为空")
    private Integer phoneModel; //1 安卓 2 ios
    @NotBlank(message = "推送唯一标识不能为空")
    private String register; //推送唯一标识
    @NotNull(message = "推送方式不能为空")
    private Integer registerType; //推送方式 1极光 2百度 3网易云 4蝴蝶云
    @NotNull(message = "登陆方式不能为空")
    private Integer identityType; //登陆方式 3微信 4微博 5qq


    private String accessToken;//第三方登陆token
    private String openId; //第三方用户ID(可获取第三方用户数据)
    private String uid; //微博的openId;
    private String code;//微信的获取openId中间数据
}
