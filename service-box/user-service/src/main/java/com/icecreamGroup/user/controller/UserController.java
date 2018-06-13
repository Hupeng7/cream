package com.icecreamGroup.user.controller;

import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.common.model.UserNameAndPasswordLogin;
import com.icecreamGroup.common.util.res.ResultEnum;
import com.icecreamGroup.common.util.res.ResultUtil;
import com.icecreamGroup.common.util.res.ResultVO;
import com.icecreamGroup.user.service.UserService;
import com.icecreamGroup.user.feignClients.CommentsClient;
import com.icecreamGroup.user.feignClients.OrderFeignClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.icecreamGroup.common.util.constant.ConstantVal.*;

@Slf4j
@RestController
@RequestMapping("user")
@SuppressWarnings("all")
public class UserController {

    @Autowired
    private CommentsClient commentsClient;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private UserService userService;

    @RequestMapping("user-comment")
    public String selectCommentList() {
        return commentsClient.backComments();
    }



    @RequestMapping("user-order/{orderNo}")
    public ResultVO<Order> selectOrderByUserId(@NonNull @PathVariable("orderNo") String orderNo) {
        try {
            Order order = orderFeignClient.getOrderByOrderNo(orderNo);
            if (order != null) return ResultUtil.success(order);
        } catch (Exception e) {
            log.error("查询失败,错误原因是{}", e.getMessage());
            return ResultUtil.error(US_QUERY_FAILED_CODE, US_QUERY_FAILED);
        }
        return ResultUtil.error(US_QUERY_NO_RESULT_CODE, US_QUERY_NO_RESULT);
    }

    @RequestMapping("tx")
    public ResultVO<Integer> insert() {

        try {
            Integer count = userService.insert();
            if (count > 0) return ResultUtil.success(count);
        } catch (Exception e) {
            log.error("插入失败，原因为{}", e.getMessage());
        }
        return ResultUtil.error(US_INSERT_FAILED_CODE, US_INSERT_FAILED);
    }

     /**
     * @param username 用户名
     * @param password 密码
     * @return ResultVO
     */
    @RequestMapping("login")
    public ResultVO<String> login(UserNameAndPasswordLogin logginAgrs) {
        log.info("用户{}，用户类型{}，正在登陆",logginAgrs.getUserName(),logginAgrs.getType());
        if(logginAgrs.getUserName()==null||logginAgrs.getPassword()==null||logginAgrs.getType()==null){
            return ResultUtil.error(null,ResultEnum.PARAMS_ERROR);
        }
        try {
            String token = userService.login(logginAgrs);
            if (!StringUtils.isBlank(token)) {
                return ResultUtil.success(token);
            }else {
                return ResultUtil.error(null,ResultEnum.USER_USERNAME_OR_PASSWORD_ERROR);
            }
        } catch (Exception e) {
            log.error("请检查用户名或密码");
            e.printStackTrace();
        }
        return null;
    }
}
