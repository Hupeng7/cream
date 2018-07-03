package com.icecream.common.model.requstbody;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 发送验证码实体类
 * create by Mr_h on 2018/6/21 0021
 */
@Data
public class SendCode {

    private String itucode;

    private String phone;

}
