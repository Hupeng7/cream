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
    public ResultVO updateStarStatus(@Validated @RequestBody PersonStatusInfo personStatusInfo) {
        return userService.changeUserStatus(personStatusInfo);
    }
}
