package com.icecream.zuul.feign;

import com.icecream.common.util.res.ResultVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/17 0017
 */
@Component
@FeignClient(name ="user-service")
public interface UserTokenFeignClient {

    @RequestMapping(value = "token/star",method = RequestMethod.GET)
    ResultVO checkStar(@RequestParam("token") String token);

    @RequestMapping(value = "token/consumer",method = RequestMethod.GET)
    ResultVO checkConsumer(@RequestParam("token")String token);
}
