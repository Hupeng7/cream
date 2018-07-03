package com.icecream.common.model.requstbody;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/19 0019
 */
@Data
public class SmsOpenApiResponse {

    private String code;

    private String errorMsg;

    private String msgId;

    private String time;
}
