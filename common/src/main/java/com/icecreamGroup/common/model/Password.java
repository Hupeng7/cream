package com.icecreamGroup.common.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Mr_h
 * @version 1.0
 * description: 修改密码的实体类
 * create by Mr_h on 2018/6/26 0026
 */

@Data
public class Password {

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
