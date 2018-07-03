package com.icecream.user.controller;

import com.icecream.user.service.UserService;
import com.icecream.user.service.UserStarService;
import com.icecream.common.model.requstbody.SimpleLogin;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.model.requstbody.PersonStatusInfo;
import com.icecream.common.util.req.RequestHandler;
import com.icecream.common.util.res.ResultVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Mr_h
 * @version 1.0
 * description: 版主Controller
 * create by Mr_h on 2018/6/27 0027
 */
@RestController
@RequestMapping("Consumers/star")
public class UserStarController {

    @Autowired
    private UserStarService userStarService;

    @Autowired
    private UserService userService;


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
     * @param request 获取token的封装对象
     * @return userStarInfo
     */
    @GetMapping("consumerInfo")
    public ResultVO starGetUserInfoByRedis(HttpServletRequest request) {
        return userStarService.getUserStarInfo(RequestHandler.paramHandlerForStar(request));
    }


    /**
     * 获取全部用户的总数
     *
     * @return count (Integer)
     */
    @GetMapping("monitor/all")
    public ResultVO getTheTotalNumberOfUsers() {
        return userService.GetTheTotalNumberOfUsers();
    }


    /**
     * 获取某年某周用户增长的数量
     * @param year x年
     * @param week 第x周
     * @return count (Integer)
     */
    @GetMapping("monitor/week")
    public ResultVO getTheNumberOfUsersInWeek(@Param("year") Integer year, @Param("weekOfYear") Integer week) {
        return userService.getTheNumberOfUsersInWeek(year, week);
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
     * @param personStatusInfo
     * @return
     */
    @PostMapping("updateStarInfo")
    public ResultVO updateStarStatus(@Validated @RequestBody PersonStatusInfo personStatusInfo){
        return userService.changeUserStatus(personStatusInfo);
    }

}
