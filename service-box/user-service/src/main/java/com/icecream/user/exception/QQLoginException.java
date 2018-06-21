package com.icecream.user.exception;

import com.icecreamGroup.common.util.res.ResultEnum;
import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 自定义QQ登陆异常类
 * create by Mr_h on 2018/6/14 0014
 */
@Data
public class QQLoginException extends Exception{

    private Integer errorCode;

    public QQLoginException(ResultEnum resultEnum){
     super(resultEnum.getMsg());
     this.errorCode=resultEnum.getCode();
    }
}
