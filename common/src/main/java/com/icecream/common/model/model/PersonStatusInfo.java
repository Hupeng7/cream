package com.icecream.common.model.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/29 0029
 */
@Data
public class PersonStatusInfo {

    @NotNull
    private Integer uid;

    @NotNull
    private Integer status;
}
