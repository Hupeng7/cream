package com.icecream.order.utils.math;

import com.icecream.common.util.idbuilder.staticfactroy.SnowflakeGlobalIdFactory;

import java.time.LocalDate;

/**
 * @author Mr_h
 * @version 1.0
 * description: 订单号生成器
 * create by Mr_h on 2018/8/10 0010
 */
public class TradesNoCreater {

    public static String create(){
        String header = LocalDate.now().toString().replace("-", "");
        Long snowNumber = new SnowflakeGlobalIdFactory().create().nextId();
        String body = snowNumber.toString();
        String less = String.valueOf((int)((Math.random()*9+1)*100000));
        return header+body+less;
    }

}
