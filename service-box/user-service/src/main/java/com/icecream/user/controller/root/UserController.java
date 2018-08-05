package com.icecream.user.controller.root;

import com.icecream.common.model.pojo.User;
import com.icecream.common.model.requstbody.*;
import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.feignclients.CommentsClient;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.UserService;
import com.icecream.user.feignclients.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @version 2.0
 */
@Slf4j
@RestController
@RequestMapping("user")
@SuppressWarnings("all")
public class
UserController {

    @Autowired
    private CommentsClient commentsClient;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private UserService userService;

    @Autowired
    private AppIdConfig appIdConfig;

    @Autowired
    private RedisTemplate redisTemplate;

/*    @RequestMapping("uid")
    public String selectCommentList(@RequestBody Map<String,Object> body) {
        return commentsClient.backComments();
    }

    @RequestMapping("user-order/{orderNo}")
    public ResultVO<Order> selectOrderByUserId(@NonNull @PathVariable("orderNo") String orderNo) {
        try {
            Order order = orderFeignClient.getOrderByOrderNo(orderNo);
            if (order != null) return ResultUtil.success(order);
        } catch (Exception e) {
            log.error("查询失败,错误原因是{}", e.getMessage());
            return ResultUtil.error(null,ResultEnum.PARAMS_ERROR);
        }
        return ResultUtil.error(null,ResultEnum.QUERY_RESULT_IS_NULL);
    }

    @RequestMapping("tx")
    public ResultVO<Integer> insert() {

        try {
            Integer count = userService.insert();
            if (count > 0) return ResultUtil.success(count);
        } catch (Exception e) {
            log.error("插入失败，原因为{}", e.getMessage());
        }
        return ResultUtil.error(null,ResultEnum.MYSQL_OPERATION_FAILED);
    }*/

    /**
     * 修改个人信息
     *
     * @param user    需要修改的字段封装实体类
     * @param request 从该对象中解析token获取uid
     * @return ResultVO<T>
     */
    @PatchMapping(value = "update")
    public ResultVO updateUserInfo(@RequestBody User user, @Param("specialTokenId") String specialTokenId) {
        return userService.update(user, specialTokenId);
    }

    /**
     * 原接口获取user数据和获取指定user数据整合
     * 根据id查询用户
     *
     * @param uid 用户的id
     * @return ResultVo
     */
    @GetMapping(value = "{consumerId}")
    public ResultVO getUserInfo(@PathVariable("consumerId") Integer uid) {
        return userService.get(uid);
    }

    /**
     * 查询用户是否设置了密码
     *
     * @param itucode 手机区号
     * @param phone   手机号
     * @param request 获取token中的uid
     * @return ResultVo<T></>
     */
    @GetMapping("{itucode}/{phone}/existpwd")
    public ResultVO isSetPassword(@PathVariable("itucode") String itucode,
                                  @PathVariable("phone") String phone,
                                  @Param("specialTokenId") String specialTokenId) {
        return userService.isSetPassword(itucode, phone, specialTokenId);
    }





    @GetMapping("authCodes/{Code}/verify")
    public ResultVO checkCode(@Param("specialTokenId") String specialTokenId,
                              @PathVariable("Code") Integer code) {
        return userService.checkCode(specialTokenId, code);
    }


    /**
     * 直播获取用户数据
     *
     * @param request 获取请求中的token 解析出uid
     * @return resultVO<T></>
     */
    @GetMapping("consumerInfo")
    public ResultVO<Object> getRedisInfo(@Param("specialTokenId") String specialTokenId) {
        return userService.getUserInfo(specialTokenId);
    }

    /**
     * 更改手机号
     *
     * @param itucode 区号
     * @param phone   手机号
     * @param request token
     * @return
     */
    @PutMapping("ituphone/{itucode}/{phone}")
    public ResultVO updatePhone(@PathVariable("itucode") @NotBlank String itucode,
                                @PathVariable("phone") @NotBlank String phone,
                                @NotBlank @Param("specialTokenId") String specialTokenId) {
        return userService.updatePhone(specialTokenId, itucode, phone);
    }

    /**
     * 修改密码
     *
     * @param password 密码封装对象 包含新密码和旧密码
     * @param request  获取token
     * @return
     */
    @PostMapping("pwdModifier")
    public ResultVO updatePassword(@Validated @RequestBody Password password,
                                   @NotBlank @Param("specialTokenId") String specialTokenId) {
        return userService.updatePassword(password, specialTokenId);
    }

    /**
     * 验证验证码，并修改密码
     *
     * @param smsLoginOrRegisterParams
     * @param request
     * @return
     */
    @PostMapping("pwdModifierByPhone")
    public ResultVO updateByCodeAndPasswrod(@Validated @RequestBody SmsLoginOrRegisterParams smsLoginOrRegisterParams,
                                            @NotBlank @Param("specialTokenId") String specialTokenId) {
        return userService.updateByCodeAndPasswrod(smsLoginOrRegisterParams, specialTokenId);
    }

    /**
     * 判断手机是否存在
     *
     * @param itucode 区号
     * @param phone   手机号
     * @param request token
     * @return
     */
    @PostMapping("verifyphone/{itucode}/{phone}")
    public ResultVO ifExistPhone(@PathVariable("itucode") @NotBlank String itucode,
                                 @PathVariable("phone") @NotBlank String phone,
                                 @NotBlank @Param("specialTokenId") String specialTokenId) {
        return userService.ifExistPhone(itucode + phone, specialTokenId);
    }

    /**
     * 验证手机号是否存在
     *
     * @param phone
     * @param request
     * @return
     */
    @PostMapping("checkPhoneInfo")
    public ResultVO checkNewPhoneInfo(@Validated @RequestBody Phone phone,
                                      @NotBlank @Param("specialTokenId") String specialTokenId) {
        return userService.checkPhoneInfo(phone, specialTokenId);
    }

    /**
     * 验证手机号对应的密码是否存在
     *
     * @param phone
     * @param request
     * @return
     */
    @PostMapping("checkPassword")
    public ResultVO checkPassword(@Validated @RequestBody Phone phone,
                                  @NotBlank @Param("specialTokenId") String specialTokenId) {
        return userService.checkPassword(phone, specialTokenId);
    }

    /**
     * 更换手机号
     *
     * @param smsloginparams
     * @return
     */
    @PostMapping("phones")
    public ResultVO changePhones(@Validated @RequestBody SmsLoginOrRegisterParams smsLoginOrRegisterParams,
                                 @NotBlank @Param("specialTokenId") String specialTokenId) {
        return userService.changePhones(smsLoginOrRegisterParams, specialTokenId);
    }


}


