package com.icecream.user.aspect.annotation;

import java.lang.annotation.*;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/27 0027
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pay {

    String value() default "";
}
