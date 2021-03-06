package com.icecream.common.util.exception;

import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.BindingException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Mr_h
 * @version 1.0
 * description: 全局异常处理
 * create by Mr_h on 2018/6/26 0026
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 未知的异常处理
     *
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public ResultVO doException(Exception e) {
        e.printStackTrace();
        return ResultUtil.error(e.getLocalizedMessage(), ResultEnum.ERROR_UNKNOWN);

    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResultVO HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException){
        return ResultUtil.error("请检查请求方式是否正确",ResultEnum.UNSUPPORTED_MEDIA_TYPE);
    }


    /**
     * 校验的异常处理
     *
     * @param methodArgumentNotValidException
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultVO beanValidation(MethodArgumentNotValidException methodArgumentNotValidException) throws Exception {
        return ResultUtil.error("非法的参数"
                + methodArgumentNotValidException
                .getBindingResult().getFieldError()
                .getDefaultMessage(), ResultEnum.PARAMS_ERROR);

    }



    /**
     * 校验的异常处理
     *
     * @param constraintViolationException spring方法级别参数校验返回对象
     * @return
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResultVO ConstraintViolationException(ConstraintViolationException constraintViolationException) throws Exception {
        Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
        return ResultUtil.error(constraintViolations.iterator().next().getMessage(), ResultEnum.PARAMS_ERROR);
    }


    /**
     * Servlet底层自带的校验
     *
     * @param missingServletRequestParameterException 丢失servlet请求参数异常
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResultVO beanValidation(MissingServletRequestParameterException missingServletRequestParameterException) throws Exception {
        return ResultUtil.error("非法的参数"
                        + missingServletRequestParameterException.getParameterName()
                , ResultEnum.PARAMS_ERROR);

    }


}
