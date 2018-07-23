package com.icecream.user.controller;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.requstbody.*;
import com.icecream.common.redis.RedisHandler;
import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.feignclients.CommentsClient;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.UserService;
import com.icecream.user.feignclients.OrderFeignClient;
import com.icecream.user.utils.req.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("Consumers")
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


    //登陆相关start------------------------------------------------------------------------------>

    /**
     * 手机号密码登陆
     *
     * @param passwordLogin 用户名（手机号）登陆的参数封装类
     * @param bindingResult 参数校验返回信息的封装类
     * @return
     */
    @PostMapping("login")
    public ResultVO<String> login(@Validated @RequestBody PasswordLogin passwordLogin) {
        log.info("用户{}，用户类型{}，正在登陆", passwordLogin.getPhone(), passwordLogin.getPhoneModel());
        return userService.login(passwordLogin);
    }

    /**
     * @param thirdPartyLoginParam {@link ThirdPartyLoginParam}
     * @return ResultVO
     * 第三方登陆  QQ/微信/微博
     * 登陆并注册用户，针对于新用户
     */
    @PostMapping("loginByOpenid")
    public ResultVO<LoginReturn> thirdPartyLoginAndRegister(
            @Validated @RequestBody ThirdPartyLoginParam thirdPartyLoginParam) {
        return userService.oauthLoginAndRegister(thirdPartyLoginParam);

    }

    /**
     * @param thirdPartyLoginParam {@link ThirdPartyLoginParam}
     * @return ResultVO
     * 第三方登陆  QQ/微信/微博
     * 登陆，针对于已经登陆过数据库有记录的用户
     */
    @RequestMapping("loginByOldOpenid")
    public ResultVO<LoginReturn> thirdPartyLogin(
            @Validated @RequestBody ThirdPartyLoginParam thirdPartyLoginParam) {
        return userService.oauthLogin(thirdPartyLoginParam);
    }

    /**
     * @param smsLoginParams {@link SmsLoginParams}
     * @return LoginReturn
     * 手机验证码快速登陆
     */
    @PostMapping("fastlogin")
    public ResultVO<LoginReturn> fastLogin(@Validated @RequestBody SmsLoginParams smsLoginParams) {
        return userService.fastLogin(smsLoginParams);
    }

    /**
     * 发送验证码
     *
     * @param itucode 区号
     * @param phone   手机号
     * @return {@link ResultVO<T>}
     */
    @PostMapping(value = "sendauthcode")
    public ResultVO<String> sendCode(@Validated @RequestBody SendCode sendCode) {
        return userService.sendCode(sendCode.getItucode(), sendCode.getPhone());
    }

    /**
     * 注册用户
     *
     * @param itucode 区号
     * @param phone   手机号
     * @return {@link ResultVO<T>}
     */
    @PostMapping("register")
    public ResultVO<LoginReturn> register(@Validated @RequestBody SmsLoginParams smsLoginParams) {
        return userService.toRigster(smsLoginParams);
    }
    //------------------------------------登陆相关end------------------------------------------->

    //------------------------------------用户操作star------------------------------------------>

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


    /**
     * 绑定第三方平台
     *
     * @param bindingModel 绑定实体类
     * @return
     */
    @PostMapping(value = "auth")
    public ResultVO binding(@Validated @RequestBody BindingModel bindingModel,
                            @Param("specialTokenId") String specialTokenId) {
        return userService.binding(bindingModel, specialTokenId);

    }

    /**
     * 解绑第三方平台
     *
     * @param bindingModel 绑定实体类
     * @param request      获取请求中的token 解析出uid
     * @return ResultVO<T></>
     */
    @DeleteMapping("{type}/auth")
    public ResultVO unbinding(@PathVariable("type") Integer type,
                              @Param("specialTokenId") String specialTokenId) {
        return userService.unbinding(type, specialTokenId);
    }

    @GetMapping("authCodes/{Code}/verify")
    public ResultVO checkCode(@Param("specialTokenId") String specialTokenId,
                              @PathVariable("Code") Integer code) {
        return userService.checkCode(specialTokenId, code);
    }

    /**
     * 获取所有的登陆方式
     *
     * @param request
     * @return
     */
    @GetMapping("auths")
    public ResultVO getAllAuths(@Param("specialTokenId") String specialTokenId) {
        return userService.getAllAuths(specialTokenId);
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
     * @param smsLoginParams
     * @param request
     * @return
     */
    @PostMapping("pwdModifierByPhone")
    public ResultVO updateByCodeAndPasswrod(@Validated @RequestBody SmsLoginParams smsLoginParams,
                                            @NotBlank @Param("specialTokenId") String specialTokenId) {
        return userService.updateByCodeAndPasswrod(smsLoginParams, specialTokenId);
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
    public ResultVO changePhones(@Validated @RequestBody SmsLoginParams smsLoginParams,
                                 @NotBlank @Param("specialTokenId") String specialTokenId) {
        return userService.changePhones(smsLoginParams, specialTokenId);
    }


}


