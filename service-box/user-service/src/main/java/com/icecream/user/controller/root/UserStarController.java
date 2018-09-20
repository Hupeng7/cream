package com.icecream.user.controller.root;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.model.model.PersonStatusInfo;
import com.icecream.common.model.model.SimpleLogin;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserStarMapper;
import com.icecream.user.redis.RedisHandler;
import com.icecream.user.service.UserService;
import com.icecream.user.service.UserStarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.icecream.common.util.constant.SysConstants.USER_HASH_PREFIX;
import static com.icecream.common.util.constant.SysConstants.USER_STAR_HASH_PREFIX;

/**
 * @author Mr_h
 * @version 2.0
 * description: 版主Controller
 * create by Mr_h on 2018/6/27 0027
 */
@Slf4j
@RestController
@Api(value = "版主接口",description = "版主操作",tags ="master-root-api")
@RequestMapping("Consumers/star")
public class UserStarController {

    @Autowired
    private UserStarService userStarService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserStarMapper userStarMapper;


    /*@RequestMapping("loading")
    public void loadingUserCache() {
        List<UserStar> userStars = userStarMapper.selectAll();
        userStars.forEach(star -> {
            RedisHandler.addMap(USER_STAR_HASH_PREFIX, star.getId().toString(), JSON.toJSONString(star));
        });
    }*/

    /**
     * 版主获取粉丝的信息
     *
     * @param uid 粉丝id
     * @return userInfo
     */
    @GetMapping("{consumerId}")
    @ApiOperation(value = "【需要版主token】版主获取粉丝的信息")
    public ResultVO starGetUserInfoById(@PathVariable("consumerId") Integer uid) {
        return userService.get(uid);
    }


    /**
     * 获取版主存在redis中的信息
     *
     * @param specialTokenId 获取token的封装对象
     * @return userStarInfo
     */
    @GetMapping("consumerInfo")
    @ApiOperation(value = "【需要版主token】获取版主存在redis中的信息")
    public ResultVO starGetUserInfoByRedis(@NotBlank @Param("specialTokenId") String specialTokenId) {
        return userStarService.getUserStarInfo(specialTokenId);
    }


    /**
     * 版主登陆
     *
     * @param simpleLogin {@link SimpleLogin}
     * @return LoginReturn
     */
    @PostMapping("login")
    @ApiOperation(value = "版主登陆")
    public ResultVO login(@Validated @RequestBody SimpleLogin simpleLogin) {
        return userStarService.login(simpleLogin);
    }

    /**
     * 添加版主用户
     *
     * @param userStar {@link UserStar}
     * @return ResultVo<T></>
     */
    @PostMapping("addStar")
    @ApiOperation(value = "【需要版主token】添加版主")
    public ResultVO add(@Validated @RequestBody UserStar userStar) {
        return userStarService.add(userStar);
    }

    /**
     * 版主拉黑或者禁言粉丝
     *
     * @param personStatusInfo {@link PersonStatusInfo}
     * @return count(Integer)是否修改成功
     */
    @PostMapping("changeFanStatus")
    @ApiOperation(value = "【需要版主token】版主拉黑或者禁言粉丝")
    public ResultVO changeFansStatus(@Validated @RequestBody PersonStatusInfo personStatusInfo) {
        return userService.changeUserStatus(personStatusInfo);
    }


    /**
     * 超级管理员 拉黑或者禁言版主
     *
     * @param personStatusInfo
     * @return
     */
    @PostMapping("updateStarInfo")
    @ApiOperation(value = "【需要版主token】超级管理员 拉黑或者禁言版主")
    public ResultVO updateStarStatus(@Validated @RequestBody PersonStatusInfo personStatusInfo) {
        return userService.changeUserStatus(personStatusInfo);
    }
}
