package com.icecream.user.controller.token;

import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.user.service.token.UserTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mr_h
 * @version 2.0
 * description: token相关接口
 * create by Mr_h on 2018/7/17 0017
 */
@Api(description = "令牌")
@RestController
@RequestMapping("user/token")
public class TokenController {

   @Autowired
   private UserTokenService userTokenService;

    /**
     * 版主端token验证
     * @param uid star开头的版主令牌
     * @return ResultVo<T></>
     */
    @GetMapping("star")
    @ApiOperation(value = "内部调用查询版主信息")
    public UserStar checkStarByMysql(@RequestParam("uid") Integer uid){
      return userTokenService.checkStar(uid);
    }

    /**
     * 粉丝端token验证
     * @param uid consumer开头的粉丝令牌
     * @return ResultVo
     */
    @GetMapping("consumer")
    @ApiOperation(value = "内部调用查询粉丝信息")
    public User checkConsumerByMysql(@RequestParam(value = "uid")Integer uid){
        return userTokenService.checkConsumer(uid);
    }

    @GetMapping("getToken/{uid}")
    @ApiOperation(value = "获取粉丝token")
    public String getToken(@PathVariable("uid") Integer uid){
       return userTokenService.getToken(uid);
    }

    @GetMapping("getStarToken/{uid}")
    @ApiOperation(value = "获取版主token")
    public String getStarToken(@PathVariable("uid") Integer uid){
        return userTokenService.getStarToken(uid);
    }
}
