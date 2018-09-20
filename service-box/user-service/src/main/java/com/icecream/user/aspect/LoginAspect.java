package com.icecream.user.aspect;

import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.model.LoginParamContainer;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.user.utils.factory.LoginFactory;
import com.icecream.user.utils.factory.builder.FactoryBuilder;
import com.icecream.user.utils.paramutils.ParamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
import java.util.Optional;

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
        try {
            //准备工作
            LoginParamContainer loginParamContainer = new LoginParamContainer();
            Object[] args = proceedingJoinPoint.getArgs();

            //从请求中获取参数信息并验证
            JSONObject jsonObject = ParamUtils.transitionParamsToJSONObject();
            Optional.ofNullable(jsonObject).orElseThrow(Throwable::new);
            String type = jsonObject.getString("type");
            String body = jsonObject.getString("body");
            Optional.ofNullable(jsonObject.getString("type")).orElseThrow(Throwable::new);
            Optional.ofNullable(jsonObject.getString("body")).orElseThrow(Throwable::new);

            //设置容器属性
            loginParamContainer.setBody(jsonObject.getString("body")!=null?jsonObject.getString("body"):"");
            loginParamContainer.setType(Integer.parseInt(jsonObject.getString("type")!=null?jsonObject.getString("type"):""));
            LoginParamContainer result = ((LoginFactory) FactoryBuilder.build(LOGIN))
                                                                       .getObject(loginParamContainer);
            //将原来的参数结构替换掉
            args[0] = result;

            log.info("超级登录已部署");
            //执行登录函数
            return proceedingJoinPoint.proceed(args);
        } catch (Throwable throwable) {
            log.error("登录失败,请检查参数");
            throwable.printStackTrace();
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }
    }
}
