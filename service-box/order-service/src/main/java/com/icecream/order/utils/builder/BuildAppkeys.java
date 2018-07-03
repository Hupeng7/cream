package com.icecream.order.utils.builder;

import com.icecream.common.model.pojo.Appkeys;

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
