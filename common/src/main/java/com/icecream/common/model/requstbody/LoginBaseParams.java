package com.icecream.common.model.requstbody;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author Mr_h
 * @version 1.0
 * description: 登陆参数基础类
 * create by Mr_h on 2018/6/21 0021
 */
@Data
public class LoginBaseParams {

    @NotBlank(message = "手机型号不能为空")
    private String phoneType;
    @NotNull(message = "手机系统类型不能为空")
    private Integer phoneModel;
    @NotNull(message = "推送注册标识不能为空")
    @NotBlank(message = "推送注册标识不能为null")
    private String register;
    @NotNull(message = "推送注册类型不能为空")
    private Integer registerType;
}
