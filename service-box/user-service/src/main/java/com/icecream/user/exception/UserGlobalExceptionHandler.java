package com.icecream.user.exception;

import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Mr_h
 * @version 1.0
 * description: 用户服务全局异常处理
 * create by Mr_h on 2018/6/26 0026
 */
@Slf4j
@RestControllerAdvice
public class UserGlobalExceptionHandler {

    @ExceptionHandler(value = UnsupportedPaymentTypeException.class)
    public ResultVO ChargeNonsupportPay(UnsupportedPaymentTypeException unsupportedPaymentTypeException) {
        return ResultUtil.error(unsupportedPaymentTypeException.getMessage(),ResultEnum.PARAMS_ERROR);
    }
}
