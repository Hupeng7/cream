package com.icecream.user.service.login;

import com.icecream.common.model.model.LoginReturn;
import com.icecream.user.utils.jwt.TokenBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/9/17 0017
 */
@Slf4j
@SuppressWarnings("all")
public abstract class AbstractLoginSupport {

    @Autowired
    private TokenBuilder tokenBuilder;


    protected LoginReturn buildLoginSuccessReturn(Object result){
      return new LoginReturn<Object>(result,tokenBuilder.createToken(result));
    }
}
