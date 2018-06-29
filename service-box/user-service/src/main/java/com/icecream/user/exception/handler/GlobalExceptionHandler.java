package com.icecream.user.exception.handler;

import com.icecreamGroup.common.util.res.ResultEnum;
import com.icecreamGroup.common.util.res.ResultUtil;
import com.icecreamGroup.common.util.res.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Mr_h
 * @version 1.0
 * description: 全局异常处理
 * create by Mr_h on 2018/6/26 0026
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler{

    /**
     * 未知的异常处理
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value=Exception.class)
    public ResultVO doException(Exception e) throws Exception{
        return ResultUtil.error(e.getLocalizedMessage(),ResultEnum.ERROR_UNKNOWN);

    }

    /**
     * 校验的异常处理
     * @param methodArgumentNotValidException
     * @return
     */
    @ExceptionHandler(value=MethodArgumentNotValidException.class)
    public ResultVO beanValidation(MethodArgumentNotValidException methodArgumentNotValidException) throws Exception{
            return ResultUtil.error("非法的参数"
                    +methodArgumentNotValidException
                    .getBindingResult().getFieldError()
                    .getDefaultMessage(), ResultEnum.PARAMS_ERROR);

    }



}
