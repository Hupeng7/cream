package com.icecream.common.model.requstbody;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 订单地址封装类
 * create by Mr_h on 2018/8/14 0014
 */
@Data
public class AddressInfo {

    //收件人名字
    private String addressSee;

    //手机
    private String phone;

    //城市
    private String city;

    //省
    private String province;

    //详细地址
    private String district;

}
