package com.icecream.user.aspect.annotation;


import com.icecream.common.model.pojo.MethodName;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Mr_h
 * @version 1.0
 * description: 权限注解
 * create by Mr_h on 2018/7/23 0023
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Permission {

    MethodName method();

}
