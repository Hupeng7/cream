package com.icecream.user.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.model.ChargeParamContainer;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.user.utils.factory.ChargeFactory;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;


/**
 * @author Mr_h
 * @version 1.0
 * description: 权限注解
 * create by Mr_h on 2018/7/23 0023
 */
@Aspect
@Order(2)
@Component
@Slf4j
@SuppressWarnings("all")
public class PayAspect {


    @Pointcut("@annotation(com.icecream.user.aspect.annotation.Pay)")
    public void fishingNet() {
    }


    @Around("fishingNet()")
    public Object setTargetHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        ServletInputStream in = request.getInputStream();
        String body = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
        JSONObject jsonObject = JSON.parseObject(body);
        ChargeParamContainer chargeParamContainer =new ChargeParamContainer();
        chargeParamContainer.setType(jsonObject.getInteger("type"));
        chargeParamContainer.setPrice(jsonObject.getBigDecimal("price"));
        chargeParamContainer.setUid(request.getParameter("specialTokenId"));
        chargeParamContainer = ChargeFactory.getServiceHandler(chargeParamContainer);
        Object[] obj=new Object[1];
        obj[0]=chargeParamContainer;
        try {
             return proceedingJoinPoint.proceed(obj);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return ResultUtil.error(null,ResultEnum.PARAMS_ERROR);
    }
}
