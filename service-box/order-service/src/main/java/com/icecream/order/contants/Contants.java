package com.icecream.order.contants;

/**
 * @author Mr_h
 * @version 1.0
 * description: 订单常量池
 * create by Mr_h on 2018/7/2 0002
 */
public interface Contants {

    Integer DEFAULT_PAGE_CURRENT = 1;

    Integer DEFAULT_PAGE_SIZE = 15;


    /**
     * 业务流水常量
     */
    Integer TYPE_CHARGE = 1; //充值

    Integer TYPE_ORDER = 2; //订单

    Integer TYPE_VALUE_ADDED_SERVICE =3;//增值业务
}
