package com.icecream.common.model.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mr_h
 * @version 2.0
 * description: 短信验证码登陆实体类
 * create by Mr_h on 2018/6/20 0020
 */
@Data
public class SmsLoginOrRegisterParams extends LoginBaseParams {

    @NotBlank(message = "区号不能为空")
    private String itucode;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private Integer code;

    private String password;

    private Integer type;

}
