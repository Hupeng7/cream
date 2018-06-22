package com.icecreamGroup.common.model;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 解析token后返回的实体类
 * create by Mr_h on 2018/6/22 0022
 */
@Data
public class TokenInfo {

    private Integer uid;

    private Integer isToken; //1 合法token 2非法token 3过期token
}
