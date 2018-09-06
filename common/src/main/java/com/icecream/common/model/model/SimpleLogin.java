package com.icecream.common.model.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Mr_h
 * @version 1.0
 * description: 版主端用户名密码登陆
 * create by Mr_h on 2018/6/28 0028
 */
@Data
public class SimpleLogin {

    @NotBlank
    private String account;

    @NotBlank
    private String password;
}
