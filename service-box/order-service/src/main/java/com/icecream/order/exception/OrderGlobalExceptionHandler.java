package com.icecream.order.exception;

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
public class OrderGlobalExceptionHandler {

    @ExceptionHandler(value = UnsupportedPaymenttypeException.class)
    public ResultVO ChargeNonsupportPay(UnsupportedPaymenttypeException unsupportedPaymenttypeException) {
        return ResultUtil.error(unsupportedPaymenttypeException.getMessage(),ResultEnum.PARAMS_ERROR);
    }
}
