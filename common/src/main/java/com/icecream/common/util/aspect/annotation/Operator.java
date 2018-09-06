package com.icecream.common.util.aspect.annotation;

import com.icecream.common.model.eunm.OperatorRole;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/9/6 0006
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Operator {

    OperatorRole role();
}
