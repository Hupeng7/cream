package com.icecream.common.model.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Mr_h
 * @version 1.0
 * description: 手机号实体类
 * create by Mr_h on 2018/6/27 0027
 */
@Data
public class Phone {
    @NotBlank
    private String itucode;

    @NotBlank
    private String phone;

}
