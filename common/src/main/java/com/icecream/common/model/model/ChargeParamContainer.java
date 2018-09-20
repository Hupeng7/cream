package com.icecream.common.model.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Mr_h
 * @version 1.0
 * description: 前台的数据模型和临时数据存储容器
 * create by Mr_h on 2018/7/27 0027
 */
@Data
@ApiModel(value = "充值对象",description = "充值的数据模型")
public class ChargeParamContainer<S> {

    @ApiModelProperty(name = "type",value = "充值方式",required = true)
    private Integer type;

    @ApiModelProperty(name = "price",value = "充值金额",required = true)
    private BigDecimal price;

    @ApiModelProperty(name = "uid",value = "用户id",required = true)
    private String uid;

    private S service;

}
