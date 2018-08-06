package com.icecream.user.aspect;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icecream.common.model.requstbody.LoginParamContainer;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.user.utils.factory.LoginFactory;
import com.icecream.user.utils.factory.builder.FactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.icecream.user.constants.Constants.LOGIN;


/**
 * @version 2.0
 */
@Slf4j
@Aspect
@Component
@SuppressWarnings("all")
public class LoginAspect {

    @Pointcut("@annotation(com.icecream.user.aspect.annotation.LoginHandler)")
    public void loginParamsAop() {
    }

    @Around("loginParamsAop()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws IOException {
        Object[] args = proceedingJoinPoint.getArgs();
        LoginParamContainer loginParamContainer = new LoginParamContainer();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        streamReader.close();
        try {
            JSONObject jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
            String type = jsonObject.getString("type");
            String body = jsonObject.getString("body");
            loginParamContainer.setBody(body);
            loginParamContainer.setType(Integer.parseInt(type));
            LoginParamContainer result = ((LoginFactory)FactoryBuilder.build(LOGIN)).getObject(loginParamContainer);
            args[0] = result;
            log.info("超级登录已部署");
            return proceedingJoinPoint.proceed(args);
        } catch (Throwable throwable) {
            return ResultUtil.error(null,ResultEnum.PARAMS_ERROR);
        }
    }
}
