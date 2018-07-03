package com.icecream.common.model.requstbody;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 调用第三方接口筛选返回数据之后的封装类
 * create by Mr_h on 2018/6/15 0015
 */
@Data
public class ThirdPartUserInfo {
    private String name;

    private String url;
}
