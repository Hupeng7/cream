package com.icecream.common.model.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Mr_h
 * @version 1.0
 * description: 发送验证码实体类
 * create by Mr_h on 2018/6/21 0021
 */
@Data
public class SendCode {


    private String itucode;

    @NotBlank(message = "手机号不能为空")
    private String phone;

}
