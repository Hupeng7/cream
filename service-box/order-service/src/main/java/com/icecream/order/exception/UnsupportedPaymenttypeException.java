package com.icecream.order.exception;

/**
 * @author Mr_h
 * @version 1.0
 * description:自定义的支付异常
 * create by Mr_h on 2018/7/27 0027
 */
public class UnsupportedPaymenttypeException extends Exception{

    public UnsupportedPaymenttypeException(String msg){
        super(msg);
    }
}
