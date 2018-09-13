package com.icecream.zuul.feign;

import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.util.res.ResultVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Mr_h
 * @version 2.0
 * description:
 * create by Mr_h on 2018/7/17 0017
 */
@Component
@FeignClient(name ="user-service")
public interface UserTokenFeignClient {

    @RequestMapping(value = "user/token/star",method = RequestMethod.GET)
    UserStar checkStarByMysql(@RequestParam("uid") Integer uid);

    @RequestMapping(value = "user/token/consumer",method = RequestMethod.GET)
    User checkConsumerByMysql(@RequestParam("uid") Integer uid);
}
