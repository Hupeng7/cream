package com.icecream.user.exception.exceptionHandler;

import com.icecreamGroup.common.util.res.ResultEnum;
import com.icecreamGroup.common.util.res.ResultUtil;
import com.icecreamGroup.common.util.res.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.BindingException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.validation.ConstraintViolationException;

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
     * 所有异常报错
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value=Exception.class)
    public ResultVO allExceptionHandler(Exception exception) {
        exception.printStackTrace();
        if(exception instanceof ConstraintViolationException|exception instanceof BindException){
            exception.printStackTrace();
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }else {
            exception.printStackTrace();
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }
}
