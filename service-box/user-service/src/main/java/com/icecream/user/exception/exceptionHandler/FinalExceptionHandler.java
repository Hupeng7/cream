package com.icecream.user.exception.exceptionHandler;

import com.icecreamGroup.common.util.res.ResultEnum;
import com.icecreamGroup.common.util.res.ResultUtil;
import com.icecreamGroup.common.util.res.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mr_h
 * @version 1.0
 * description: 在进入Controller之前 出现访问异常(如404,405)
 * create by Mr_h on 2018/6/26 0026
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
        log.error("错误的请求路径--->{},请求方式--->{}",req.getRequestURL(),req.getMethod());
        int code = resp.getStatus();
        log.error("http状态码--->{}",code);
        switch (code){
            case HttpServletResponse.SC_METHOD_NOT_ALLOWED:
                return ResultUtil.error(null,ResultEnum.REQUEST_TYPE_TO_METHOD_NOT_ALLOW);
            case HttpServletResponse.SC_NOT_FOUND:
                return ResultUtil.error(null,ResultEnum.NOT_FOUND);
            default:
                return ResultUtil.error(resp.getStatus(),"请求失败，请检查请求路径或请求参数");
        }
    }
}
