package com.icecream.common.model.requstbody;


import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @version 2.0
 * @param <K>
 * @param <V>
 */
@Component
@Data
public class LoginParamContainer<K,V> {

    private K body;

    private V service;

    private Integer type;


    public LoginParamContainer(){
    }

    public LoginParamContainer(K body,V service){
        this.body =body;
        this.service=service;
    }

}
