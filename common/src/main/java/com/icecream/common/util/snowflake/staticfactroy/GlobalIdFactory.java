package com.icecream.common.util.snowflake.staticfactroy;

/**
 * @author Mr_h
 * @version 1.0
 * description: id算法工厂
 * create by Mr_h on 2018/7/4 0004
 */
public interface GlobalIdFactory<T> {

    T create();
}
