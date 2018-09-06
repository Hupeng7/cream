package com.icecream.common.util.aspect;

import com.icecream.common.model.eunm.MethodName;
import com.icecream.common.model.eunm.OperatorRole;
import com.icecream.common.util.aspect.annotation.Operator;
import com.icecream.common.util.check.PermissionChecker;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.icecream.common.util.constant.SysConstants.FANS;
import static com.icecream.common.util.constant.SysConstants.STAR;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/9/6 0006
 */
@Aspect
@Order(2)
@Component
@Slf4j
public class OperatorAspect {

    @Pointcut("@annotation(com.icecream.common.util.aspect.annotation.Operator)")
    public void pointCut() {
    }

    @Around("pointCut()&&@annotation(operator)")
    public Object checkRole(ProceedingJoinPoint target, Operator operator) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String specialTokenId = request.getParameter("specialTokenId");
        OperatorRole role = operator.role();
        String name = role.name();
        if (name.equals("CONSUMER")) {
            if (!PermissionChecker.belongToConsumer(Integer.parseInt(specialTokenId))) {
                return ResultUtil.error(null, ResultEnum.NOT_AUTH);
            } else {
                try {
                    return target.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    return ResultUtil.error(null, ResultEnum.NOT_AUTH);
                }
            }
        } else if (name.equals("STAR")) {
            if (!PermissionChecker.belongToStar(Integer.parseInt(specialTokenId))) {
                return ResultUtil.error(null, ResultEnum.NOT_AUTH);
            } else {
                try {
                    return target.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    return ResultUtil.error(null, ResultEnum.NOT_AUTH);
                }
            }
        } else {
            throw new RuntimeException("未知异常");
        }
    }
}
