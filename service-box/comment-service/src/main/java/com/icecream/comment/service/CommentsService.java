package com.icecream.comment.service;

import com.icecream.comment.feign.CallRemoteUrl;
import com.icecream.common.model.model.SendCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/9/13 0013
 */
@Slf4j
@Service
public class CommentsService {

    @Autowired
    private CallRemoteUrl callRemoteUrl;

    public String getCallData(String token){
        String json = callRemoteUrl.call(token);
        return json;
    }

    public String getCallData2(String token,SendCode sendCode){
        String json = callRemoteUrl.sendPhoneCode(token,sendCode);
        return json;
    }
}
