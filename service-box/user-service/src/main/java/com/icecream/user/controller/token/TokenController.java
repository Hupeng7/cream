package com.icecream.user.controller.token;

import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.token.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author Mr_h
 * @version 2.0
 * description: token相关接口
 * create by Mr_h on 2018/7/17 0017
 */
@RestController
@RequestMapping("user/token")
public class TokenController {

   @Autowired
   private UserTokenService userTokenService;

    /**
     * 版主端token验证
     * @param token star开头的版主令牌
     * @return ResultVo<T></>
     */
    @GetMapping("star")
    public ResultVO checkStar(@RequestParam("token") String token){
      return userTokenService.checkStar(token);
    }

    /**
     * 粉丝端token验证
     * @param token consumer开头的粉丝令牌
     * @return ResultVo
     */
    @GetMapping("consumer")
    public ResultVO checkConsumer(@RequestParam(value = "token")String token){
        return userTokenService.checkConsumer(token);
    }

    @GetMapping("getToken/{uid}")
    public String getToken(@PathVariable("uid") Integer uid){
       return userTokenService.getToken(uid);
    }

    @GetMapping("getStarToken/{uid}")
    public String getStarToken(@PathVariable("uid") Integer uid){
        return userTokenService.getStarToken(uid);
    }
}
