package com.icecream.common.model.requstbody;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Mr_h
 * @version 1.0
 * description: 订单相关的用户信息初始化
 * create by Mr_h on 2018/8/29 0029
 */
@Data
public class UserInfoInitModel {

    private Integer hasBeenBought;

    private BigDecimal balance;

    private BigDecimal exp;

    private MitGoodsRedis mitGoodsRedis;
}
