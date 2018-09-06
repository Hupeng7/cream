package com.icecream.user.service.login;

import com.icecream.common.model.model.LoginParamContainer;
import com.icecream.common.util.res.ResultVO;
import org.springframework.stereotype.Service;


/**
 * @version 2.0
 */
@Service
@SuppressWarnings("all")
public class LoginService {

    public ResultVO superLogin(LoginParamContainer loginParamContainer){
       return ((SuperLogin)loginParamContainer.getService())
                                              .login(loginParamContainer.getBody());
    }
}
