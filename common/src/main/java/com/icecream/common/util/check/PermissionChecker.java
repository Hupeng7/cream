package com.icecream.common.util.check;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/9/6 0006
 */
public class PermissionChecker {

    public static Boolean belongToStar(Integer uid) {
        if (uid < 0) {
            return true;
        }
        return false;
    }

    public static Boolean belongToConsumer(Integer uid) {
        if (uid > 0) {
            return true;
        }
        return false;
    }
}
