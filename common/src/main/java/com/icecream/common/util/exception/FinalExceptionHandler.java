package com.icecream.common.util.exception;

import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/12 0012
 */
@Slf4j
@RestController
public class FinalExceptionHandler implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = "/error")
    public ResultVO error(HttpServletResponse resp, HttpServletRequest req) {
        // 错误处理逻辑
        log.error("错误的请求路径--->{},请求方式--->{},请求参数---->{}"
                ,req.getRequestURL(),req.getMethod(),req.getQueryString());
        int code = resp.getStatus();
        log.error("http状态码--->{}",code);
        switch (code){
            case HttpServletResponse.SC_METHOD_NOT_ALLOWED:
                return ResultUtil.error("请检查请求类型是否正确",ResultEnum.REQUEST_TYPE_TO_METHOD_NOT_ALLOW);
            case HttpServletResponse.SC_NOT_FOUND:
                return ResultUtil.error("请检查url是否输入正确",ResultEnum.NOT_FOUND);
            case HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE:
                return ResultUtil.error("请检查",ResultEnum.UNSUPPORTED_MEDIA_TYPE);
            default:
                return ResultUtil.error(resp.getStatus(),"请求失败，请检查请求路径或请求参数");
        }
    }
}
