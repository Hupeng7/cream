package com.icecream.user.controller;

import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("star")
    public ResultVO checkStar(@RequestParam("token") String token){
      return userTokenService.checkStar(token);
    }
    @GetMapping("consumer")
    public ResultVO checkConsumer(@RequestParam("token")String token){
        return userTokenService.checkConsumer(token);
    }
}
