package com.icecream.common.model.requstbody;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Mr_h
 * @version 1.0
 * description: 前台的数据模型和临时数据存储容器
 * create by Mr_h on 2018/7/27 0027
 */
@Data
public class ChargeParamContainer<S> {

    private Integer type;

    private BigDecimal price;

    private String uid;

    private S service;

}
