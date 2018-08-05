package com.icecream.common.model.requstbody;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Mr_h
 * @version 2.0
 * description:
 * create by Mr_h on 2018/7/18 0018
 */
@Data
public class SmsSupplementOrRegister extends SmsLoginOrRegisterParams {

    @NotNull
    private Integer id;
}
