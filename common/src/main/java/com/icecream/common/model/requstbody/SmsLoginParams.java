package com.icecream.common.model.requstbody;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mr_h
 * @version 1.0
 * description: 短信验证码登陆实体类
 * create by Mr_h on 2018/6/20 0020
 */
@Data
public class SmsLoginParams extends LoginBaseParams {

    @NotBlank(message = "区号不能为空")
    private String ituCode;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotNull(message = "验证码不能为空")
    private Integer code;

    private String password;

}
