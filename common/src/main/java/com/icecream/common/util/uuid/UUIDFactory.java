package com.icecream.common.util.uuid;

import java.util.UUID;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/9 0009
 */
public class UUIDFactory {

    public static String create(){

        return UUID.randomUUID().toString();
    }
}
