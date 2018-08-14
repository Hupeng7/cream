package com.icecream.user.service.charge;

import com.icecream.common.model.pojo.Order;
import com.icecream.common.util.res.ResultVO;

import java.math.BigDecimal;

/**
 * @author Mr_h
 * @version 1.0
 * description: 支付接口(支付宝，微信)
 * create by Mr_h on 2018/7/26 0026
 */
public interface ChargeService {

    /**
     * 充值
     * @param uid 用户id
     * @param price 充值金额
     * @return ResultVO
     */
    ResultVO charge(String uid,BigDecimal price);

    /**
     * 构建预下单的对象
     * @param uid  用户id
     * @param price 充值金额
     * @param orderNo 生成的订单号
     * @return Order
     */
    Order buildPreOrder(String uid,String orderNo,BigDecimal price);


}
