package com.icecream.user.exception;

import com.icecreamGroup.common.util.res.ResultEnum;
import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/22 0022
 */
@Data
public class DoubleNicknameException extends RuntimeException{


    public DoubleNicknameException(ResultEnum resultEnum){
        super(resultEnum.getMsg());

    }
}
