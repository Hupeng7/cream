package com.icecreamGroup.user.controller;

import com.icecreamGroup.common.model.Order;
import com.icecreamGroup.common.model.ThirdPartyLoginParam;
import com.icecreamGroup.common.model.ThirdPartyLoginReturn;
import com.icecreamGroup.common.model.UserNameAndPasswordLogin;
import com.icecreamGroup.common.util.res.ResultEnum;
import com.icecreamGroup.common.util.res.ResultUtil;
import com.icecreamGroup.common.util.res.ResultVO;
import com.icecreamGroup.user.config.login.AppIdConfig;
import com.icecreamGroup.user.service.UserService;
import com.icecreamGroup.user.feignClients.CommentsClient;
import com.icecreamGroup.user.feignClients.OrderFeignClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @Autowired
    private AppIdConfig appIdConfig;

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


    //登陆相关start------------------------------------------------------------------------------>
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
            log.error("查询用户/创建token出错，原因：{}",e.getCause());
            return ResultUtil.error(null,ResultEnum.ERROR_UNKNOWN);
        }
    }

    /**
     * @param thirdPartyLoginParam
     * {@link ThirdPartyLoginParam}
     * @return ResultVO
     * 第三方登陆  QQ/微信/微博
     */
    @RequestMapping("thirdPartyLogin")
    public ResultVO<ThirdPartyLoginReturn> thirdPartyLogin(@RequestBody @Valid ThirdPartyLoginParam thirdPartyLoginParam,
                                                           BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.info("基础参数不合法:"+bindingResult.getFieldError()  .getDefaultMessage());
            return ResultUtil.error(11011,"非法的参数："+bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            ThirdPartyLoginReturn thirdPartyLoginReturn = userService.oauthLogin(thirdPartyLoginParam);
            if(thirdPartyLoginReturn!=null){
                return ResultUtil.success(thirdPartyLoginReturn);
            }else {
                return ResultUtil.error(null,ResultEnum.ERROR_UNKNOWN);
            }
        } catch (Exception e) {
            log.info("第三方登陆出错,错误原因{}",e.getStackTrace());
            return ResultUtil.error(null,ResultEnum.ERROR_UNKNOWN);
        }
    }


    /**
     * 发送验证码
     * @param phone 手机号码
     * @return ResultVo
     */
    @RequestMapping(value = "getCode",method = RequestMethod.GET)
    public ResultVO<String> getCode(String phone){
        try {
            Boolean flag = userService.sendCode(phone);
            if(flag){
                return ResultUtil.success(flag);
            }else
                return ResultUtil.error(null,ResultEnum.SMS_CODE_SEND_FAILED);
        } catch (Exception e) {
            log.info("发送验证码出错，错误是{}",e.getStackTrace());
            e.printStackTrace();
            return ResultUtil.error(null,ResultEnum.ERROR_UNKNOWN);
        }
    }
    //登陆相关end------------------------------------------------------------------------------>


}
