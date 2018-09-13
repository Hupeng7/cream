package com.icecream.comment.feign;

import com.icecream.common.model.model.SendCode;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/9/13 0013
 */
@FeignClient(name = "superman",url = "http://ytts4h.natappfree.cc")
public interface CallRemoteUrl {

    @RequestMapping(value = "/user-api/user/star/monitor/all",method = RequestMethod.GET)
    String call(@RequestParam("access_token")String accessToken);

    @RequestMapping(value = "/user-api/user/sendauthcode",method = RequestMethod.POST)
    String sendPhoneCode(@RequestParam("access_token")String accessToken,
                         @RequestBody SendCode sendCode);
}
