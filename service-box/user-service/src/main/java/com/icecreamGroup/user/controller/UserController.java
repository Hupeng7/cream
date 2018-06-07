package com.icecreamGroup.user.controller;

import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.common.util.exception.RemoteCallException;
import com.icecreamGroup.common.util.res.ResultUtil;
import com.icecreamGroup.common.util.res.ResultVO;
import com.icecreamGroup.user.service.UserService;
import com.icecreamGroup.user.feignClients.CommentsClient;
import com.icecreamGroup.user.feignClients.OrderFeignClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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

    @RequestMapping("user-order/{orderNo}")
    public ResultVO<Order> selectOrderByUserId(@NonNull @PathVariable("orderNo") String orderNo) {
        try {
            Order order = orderFeignClient.getOrderByOrderNo(orderNo);
            if (order != null) return ResultUtil.success(order);
        } catch (RemoteCallException e) {
            log.error("调用远程服务出现异常且没有进入断路由,错误原因是{}", e.getMessage());
        }
        return ResultUtil.error(QUERY_NO_RESULT_CODE,QUERY_NO_RESULT);
    }

    @RequestMapping("tx")
    public ResultVO<Integer> insert() {
        try {
            Integer count = userService.insert();
            if (count > 0)
                ResultUtil.success(count);
        } catch (Exception e) {
            log.error("调用远程服务insert()出现异常,错误原因是{}", e.getMessage());
        }
        return ResultUtil.error(INSERT_FAILED_CODE, INSERT_FAILED);
    }

    @RequestMapping("user-comment")
    public String selectCommentList() {
        return commentsClient.backComments();
    }

}
