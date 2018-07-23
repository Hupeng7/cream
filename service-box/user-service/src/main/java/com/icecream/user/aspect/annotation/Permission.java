package com.icecream.user.aspect.annotation;

/**
 * @author Mr_h
 * @version 1.0
 * description: 权限注解
 * create by Mr_h on 2018/7/23 0023
 */
public @interface Permission {

    String method() default "";
}
