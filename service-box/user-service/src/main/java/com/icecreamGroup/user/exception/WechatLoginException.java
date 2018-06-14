package com.icecreamGroup.user.exception;

import com.icecreamGroup.common.util.res.ResultEnum;
import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 自定义微信登陆异常类
 * create by Mr_h on 2018/6/14 0014
 */
@Data
public class WechatLoginException extends Exception{

    private Integer errorCode;

    public WechatLoginException(ResultEnum resultEnum){
     super(resultEnum.getMsg());
     this.errorCode=resultEnum.getCode();
    }
}
