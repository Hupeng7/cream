package com.icecreamGroup.user.utils.token;

import com.icecreamGroup.common.model.TokenEntity;
import org.springframework.stereotype.Component;

/**@version 1.0
 * @author Mr_h
 * {@link}
 * 描述:token调度类
 * create by 2018/6/8 0008
 */

@Component
public class TokenHandler implements Token{

    @Override
    public String createToken() {
        return null;
    }

    @Override
    public Boolean checkToken(String token) {
        return null;
    }

    @Override
    public Boolean refreshToken() {
        return null;
    }
    public TokenEntity getTokenInfo() {
        return null;
    }
}
