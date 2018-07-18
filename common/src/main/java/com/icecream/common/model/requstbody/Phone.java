package com.icecream.common.model.requstbody;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author Mr_h
 * @version 1.0
 * description: 手机号实体类
 * create by Mr_h on 2018/6/27 0027
 */
@Data
public class Phone {

    @NotNull
    private Integer id;

    @NotBlank
    private String itucode;

    @NotBlank
    private String phone;

}
