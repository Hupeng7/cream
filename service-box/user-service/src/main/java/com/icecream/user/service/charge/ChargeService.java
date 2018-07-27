package com.icecream.user.service.charge;

import com.icecream.common.util.res.ResultVO;

import java.math.BigDecimal;

/**
 * @author Mr_h
 * @version 1.0
 * description: 支付接口(支付宝，微信)
 * create by Mr_h on 2018/7/26 0026
 */
public interface ChargeService {

    ResultVO charge(BigDecimal price);
}
