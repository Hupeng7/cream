package com.icecream.user.utils.jwt;

import com.icecreamGroup.common.model.User;
import com.icecreamGroup.common.model.UserStar;
import com.icecreamGroup.common.util.jwt.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/21 0021
 */
@Component
public class TokenBuilder {


    @Autowired
    private JwtProperties jwtProperties;

    public  String createToken(Object o)throws NullPointerException{
        if(o instanceof UserStar){
            UserStar userStar = (UserStar)o;
            return JwtHelper.createJWTForStar(jwtProperties.getValidTime(),jwtProperties.getStarSecret(),userStar);
        }else if(o instanceof User){
            User user = (User)o;
            return JwtHelper.createJWT(jwtProperties.getValidTime(),jwtProperties.getCustomerSecret(),user);
        }else {
            return "";
        }
    }
}
