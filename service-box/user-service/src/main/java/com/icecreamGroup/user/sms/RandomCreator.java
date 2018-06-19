package com.icecreamGroup.user.sms;

/**
 * @author Mr_h
 * @version 1.0
 * description: 随机数生成
 * create by Mr_h on 2018/6/19 0019
 */
public class RandomCreator {

    //随机生成4位随机数
    public static Integer createAuthCode(){
        return (int)(Math.random()*9000)+1000;
    }
}
