package com.icecream.zuul.jwt;

import com.icecream.common.model.model.TokenInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/17 0017
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class TokenParser {


    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private TokenBuilder tokenBuilder;


    public TokenInfo parseToken(String token){
        TokenInfo tokenInfo = null;
        if(token.startsWith("star")) {
             tokenInfo = JwtHelper.parseJWT(token.replace("star", ""), jwtProperties.getStarSecret());
        }else if(token.startsWith("consumer")){
             tokenInfo = JwtHelper.parseJWT(token.replace("consumer", ""), jwtProperties.getCustomerSecret());
        }
        return tokenInfo;

    }

}
