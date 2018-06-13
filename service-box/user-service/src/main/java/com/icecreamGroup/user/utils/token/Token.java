package com.icecreamGroup.user.utils.token;

import com.icecreamGroup.common.model.TokenEntity;

/**
 * @author Mr_h
 * @version 1.0
 * 描述: token业务相关接口
 * create by 2018/6/8 0008
 */
public interface Token {

     String createToken();

     Boolean checkToken(String token);

     Boolean refreshToken();

}
