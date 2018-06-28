package com.icecream.user.controller;

import com.icecream.user.service.UserService;
import com.icecream.user.service.UserStarService;
import com.icecreamGroup.common.model.SimpleLogin;
import com.icecreamGroup.common.model.UserStar;
import com.icecreamGroup.common.util.req.RequestHandler;
import com.icecreamGroup.common.util.res.ResultVO;
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
     * @return
     */
    @GetMapping("{consumerId}")
    public ResultVO starGetUserInfoById(@PathVariable("consumerId") Integer uid) {
        return userService.get(uid);
    }

    @GetMapping("consumerInfo")
    public ResultVO starGetUserInfoByRedis(HttpServletRequest request) {
        return userStarService.getUserStarInfo(RequestHandler.paramHandler(request));
    }

    @GetMapping("monitor/all")
    public ResultVO getTheTotalNumberOfUsers(){
      return userService.GetTheTotalNumberOfUsers();
    }

    @GetMapping("monitor/week")
    public ResultVO getTheNumberOfUsersInWeek(@Param("year")Integer year,@Param("weekOfYear")Integer week){
        return userService.getTheNumberOfUsersInWeek(year,week);
    }

    @PostMapping("login")
    public ResultVO login(@Validated @RequestBody SimpleLogin simpleLogin){
        return userStarService.login(simpleLogin);
    }

    @PostMapping("addStar")
    public ResultVO add(@Validated @RequestBody UserStar userStar){
        return userStarService.add(userStar);
    }

}
