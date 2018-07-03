package com.icecream.common.model.requstbody;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 短信发送返回信息实体类
 * create by Mr_h on 2018/6/20 0020
 */
@Data
public class SmsSendResult {

    private Boolean existPhone;

    private Boolean existPwd;

}
