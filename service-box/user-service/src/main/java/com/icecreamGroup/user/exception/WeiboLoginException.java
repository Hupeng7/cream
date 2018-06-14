package com.icecreamGroup.user.exception;

import com.icecreamGroup.common.util.res.ResultEnum;
import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 自定义微博登陆异常
 * create by Mr_h on 2018/6/14 0014
 */
@Data
public class WeiboLoginException extends Exception{


    private Integer errorCode;

    public WeiboLoginException(ResultEnum resultEnum){
        super(resultEnum.getMsg());
        this.errorCode =resultEnum.getCode();
    }
}
