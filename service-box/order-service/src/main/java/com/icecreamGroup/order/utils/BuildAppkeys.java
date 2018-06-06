package com.icecreamGroup.order.utils;

import com.icecreamGroup.common.model.Appkeys;
import com.icecreamGroup.common.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class BuildAppkeys {


    public static Appkeys buildAppkeys(){
        Appkeys appkeys = new Appkeys();
        appkeys.setId(1);
        appkeys.setAppid("123");
        appkeys.setAppsecret("我");
        appkeys.setRemark("hah");
        return appkeys;
    }
}
