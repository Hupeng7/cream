package com.icecream.common.model.model;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 发送封装类
 * create by Mr_h on 2018/6/19 0019
 */
@Data
public class SmsSendEntity {

     private String account;

     private String password;

     private String msg;

     private String phone;

}
