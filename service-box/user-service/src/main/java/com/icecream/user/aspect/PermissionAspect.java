package com.icecream.user.aspect;

import com.icecream.common.model.pojo.*;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.user.aspect.annotation.Permission;
import com.icecream.user.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


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
public class PermissionAspect {


    @Pointcut("@annotation(com.icecream.user.aspect.annotation.Permission)")
    public void fishingNet() {
    }

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private UserPermissionMapper userPermissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Around("fishingNet()&&@annotation(permissionAnnotation)")
    public Object checkPermission(ProceedingJoinPoint target,Permission permissionAnnotation) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String url = request.getRequestURL().toString();
        String uid = request.getParameter("specialTokenId");
        MethodName method = permissionAnnotation.method();
        String name = method.name();
        if (uid != null) {
            UserRole userRoleArg = new UserRole();
            userRoleArg.setUserId(Integer.parseInt(uid));
            UserRole userRole = userRoleMapper.selectOne(userRoleArg);
            Role roleArg = new Role();
            roleArg.setId(userRole.getRoleId());
            Role role = roleMapper.selectOne(roleArg);
            log.info("当前操作用户id是{}，操作等级为{}", userRole.getId(), role.getRoleName());
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(role.getId());
            RolePermission permission = rolePermissionMapper.selectOne(rolePermission);
            String permissionId = permission.getPermissionId();
            String[] pid = permissionId.split(",");
            List<String> strings = Arrays.asList(pid);
            UserPermission userPermission = new UserPermission();
            for (String p : strings) {
                userPermission.setId(Integer.parseInt(p));
                userPermission.setPermissionName(name);
                UserPermission result = userPermissionMapper.selectOne(userPermission);
                if (result != null) {
                    if (url.contains(result.getPermissionUrl())) {
                        try {
                            return target.proceed();
                        } catch (Throwable throwable) {
                            log.error("执行方法内部出错");
                            throwable.printStackTrace();
                            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
                        }
                    }
                }
            }
        }
        return ResultUtil.error(null, ResultEnum.NOT_AUTH);
    }
}
