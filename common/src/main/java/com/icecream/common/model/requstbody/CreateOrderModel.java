package com.icecream.common.model.requstbody;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @author Mr_h
 * @version 1.0
 * description: 创建订单时的参数模型
 * create by Mr_h on 2018/8/15 0015
 */
@Data
public class CreateOrderModel {

    @NotBlank(message = "商品编号不能为空")
    private String goodsSn;

    @NotNull
    private Integer sid;

    private String specId;

    @Range(min = 0,message = "商品数量不能小于0")
    @NotNull(message = "商品数量不能为空")
    private Integer goodsCount;

    @NotNull(message = "商品种类不能为空")
    private Integer isDigital;

    @NotBlank(message = "订单收货地址不能为空")
    private String address;
}
