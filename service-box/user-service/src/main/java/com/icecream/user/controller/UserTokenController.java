package com.icecream.user.controller;

import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/17 0017
 */
@RestController
@RequestMapping("token")
public class UserTokenController {

   @Autowired
   private UserTokenService userTokenService;

    @RequestMapping("star")
    public ResultVO checkStar(String token){
      return userTokenService.checkStar(token);
    }
    @RequestMapping("consumer")
    public ResultVO checkConsumer(String token){
        return userTokenService.checkConsumer(token);
    }
}
