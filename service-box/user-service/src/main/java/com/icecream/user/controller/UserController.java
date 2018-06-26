package com.icecream.user.controller;

import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.feignclients.CommentsClient;
import com.icecreamGroup.common.model.*;
import com.icecreamGroup.common.util.req.RequestHandler;
import com.icecreamGroup.common.util.res.ResultEnum;
import com.icecreamGroup.common.util.res.ResultUtil;
import com.icecreamGroup.common.util.res.ResultVO;
import com.icecream.user.service.UserService;
import com.icecream.user.feignclients.OrderFeignClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;


import static com.icecreamGroup.common.util.constant.ConstantVal.*;

@Slf4j
@RestController
@RequestMapping("Consumers")
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
     * 手机号密码登陆
     *
     * @param passwordLogin 用户名（手机号）登陆的参数封装类
     * @param bindingResult 参数校验返回信息的封装类
     * @return
     */
    @RequestMapping("login")
    public ResultVO<String> login(@RequestBody @Valid PasswordLogin passwordLogin, BindingResult bindingResult) {
        log.info("用户{}，用户类型{}，正在登陆", passwordLogin.getPhone(), passwordLogin.getPhoneModel());
        if (bindingResult.hasErrors()) {
            log.info("基础参数不合法:" + bindingResult.getFieldError().getDefaultMessage());
            return ResultUtil.error("非法的参数：" + bindingResult.getFieldError().getDefaultMessage(), ResultEnum.PARAMS_ERROR);
        }
        try {
            LoginReturn login = userService.login(passwordLogin);
            if (login != null) {
                return ResultUtil.success(login);
            } else {
                return ResultUtil.error(null, ResultEnum.USER_USERNAME_OR_PASSWORD_ERROR);
            }
        } catch (Exception e) {
            log.error("查询用户/创建token出错，原因：{}", e.getCause());
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }

    /**
     * @param thirdPartyLoginParam {@link ThirdPartyLoginParam}
     * @return ResultVO
     * 第三方登陆  QQ/微信/微博
     * 登陆并注册用户，针对于新用户
     */
    @RequestMapping("loginByOpenid")
    public ResultVO<LoginReturn> thirdPartyLoginAndRegister(@RequestBody @Valid ThirdPartyLoginParam thirdPartyLoginParam,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("基础参数不合法:" + bindingResult.getFieldError().getDefaultMessage());
            return ResultUtil.error("非法的参数：" + bindingResult.getFieldError().getDefaultMessage(), ResultEnum.PARAMS_ERROR);
        }
        try {
            LoginReturn loginReturn = userService.oauthLoginAndRegister(thirdPartyLoginParam);
            if (loginReturn != null) {
                return ResultUtil.success(loginReturn);
            } else {
                return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
            }
        } catch (Exception e) {
            log.info("第三方登陆出错,错误原因{}", e.getStackTrace());
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }

    /**
     * @param thirdPartyLoginParam {@link ThirdPartyLoginParam}
     * @return ResultVO
     * 第三方登陆  QQ/微信/微博
     * 登陆，针对于已经登陆过数据库有记录的用户
     */
    @RequestMapping("loginByOldOpenid")
    public ResultVO<LoginReturn> thirdPartyLogin(@RequestBody @Valid ThirdPartyLoginParam thirdPartyLoginParam,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("基础参数不合法:" + bindingResult.getFieldError().getDefaultMessage());
            return ResultUtil.error("非法的参数：" + bindingResult.getFieldError().getDefaultMessage(), ResultEnum.PARAMS_ERROR);
        }
        try {
            LoginReturn loginReturn = userService.oauthLogin(thirdPartyLoginParam);
            if (loginReturn != null) {
                return ResultUtil.success(loginReturn);
            } else {
                return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
            }
        } catch (Exception e) {
            log.info("第三方登陆出错,错误原因{}", e.getStackTrace());
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }

    /**
     * @param smsLoginParams {@link SmsLoginParams}
     * @return LoginReturn
     * 手机验证码快速登陆
     */
    @RequestMapping("fastlogin")
    public ResultVO<LoginReturn> fastLogin(@RequestBody @Valid SmsLoginParams smsLoginParams,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("基础参数不合法:" + bindingResult.getFieldError().getDefaultMessage());
            return ResultUtil.error("非法的参数：" + bindingResult.getFieldError().getDefaultMessage(), ResultEnum.PARAMS_ERROR);
        }
        try {
            LoginReturn loginReturn = userService.fastLogin(smsLoginParams);
            if (loginReturn != null) {
                return ResultUtil.success(loginReturn);
            } else {
                return ResultUtil.error(null, ResultEnum.INSERT_REPETITION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("错误原因是---{}", e.getStackTrace());
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }

    /**
     * 发送验证码
     *
     * @param itucode 区号
     * @param phone   手机号
     * @return {@link ResultVO<T>}
     */
    @RequestMapping(value = "sendauthcode", method = RequestMethod.POST)
    public ResultVO<String> sendCode(@RequestBody SendCode sendCode) {
        try {
            Boolean flag = userService.sendCode(sendCode.getItucode(), sendCode.getPhone());
            if (flag) {
                return ResultUtil.success(flag);
            } else
                return ResultUtil.error(null, ResultEnum.SMS_CODE_SEND_FAILED);
        } catch (Exception e) {
            log.info("发送验证码出错，错误是{}", e.getStackTrace());
            e.printStackTrace();
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }

    /**
     * 注册用户
     *
     * @param itucode 区号
     * @param phone   手机号
     * @return {@link ResultVO<T>}
     */
    @RequestMapping("register")
    public ResultVO<LoginReturn> register(@RequestBody @Valid SmsLoginParams smsLoginParams,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("基础参数不合法:" + bindingResult.getFieldError().getDefaultMessage());
            return ResultUtil.error("非法的参数：" + bindingResult.getFieldError().getDefaultMessage(), ResultEnum.PARAMS_ERROR);
        }
        try {
            LoginReturn loginReturn = userService.toRigster(smsLoginParams);
            if (loginReturn != null) {
                return ResultUtil.success(loginReturn);
            } else {
                return ResultUtil.error(null, ResultEnum.INSERT_REPETITION);
            }
        } catch (Exception e) {
            log.error("错误原因是---{}", e.getStackTrace());
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }
    //------------------------------------登陆相关end------------------------------------------->

    //------------------------------------用户操作star------------------------------------------>

    /**
     * 修改用户个人信息
     *
     * @param user    需要修改的字段封装实体类
     * @param request 从该对象中解析token获取uid
     * @return ResultVO<T>
     */
    @PatchMapping(value = "update")
    public ResultVO updateUserInfo(@RequestBody @Valid User user, HttpServletRequest request) {
        try {
            return userService.update(user, RequestHandler.paramHandler(request));
        } catch (Exception e) {
            if (e instanceof ConstraintViolationException) {
                return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
            }
            e.printStackTrace();
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }

    /**
     * 根据id查询用户
     *
     * @param uid 他人用户的id
     * @return ResultVo
     */
    @GetMapping(value = "{consumerId}")
    public ResultVO getUserInfo(@PathVariable("consumerId") Integer uid) {
        try {
            return userService.get(uid);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ConstraintViolationException) {
                return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
            }
            e.printStackTrace();
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
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
                                  HttpServletRequest request) {
        try {
            return userService.isSetPassword(itucode, phone, RequestHandler.paramHandler(request));
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ConstraintViolationException) {
                return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
            }
            e.printStackTrace();
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }


    /**
     * 绑定第三方平台
     *
     * @param bindingModel 绑定实体类
     * @param request      获取请求中的token 解析出uid
     * @return
     */
    @PostMapping(value = "auth")
    public ResultVO binding(@RequestBody @Valid BindingModel bindingModel, HttpServletRequest request) {
        try {
            return userService.binding(bindingModel, RequestHandler.paramHandler(request));
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ConstraintViolationException) {
                return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
            }
            e.printStackTrace();
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }

    /**
     * 解绑第三方平台
     *
     * @param bindingModel 绑定实体类
     * @param request      获取请求中的token 解析出uid
     * @return ResultVO<T></>
     */
    @DeleteMapping("{type}/auth")
    public ResultVO unbinding(@PathVariable("type") Integer type, HttpServletRequest request) {
        try {
            return userService.unbinding(type, RequestHandler.paramHandler(request));
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ConstraintViolationException) {
                return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
            }
            e.printStackTrace();
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }

    /**
     * 直播获取用户数据
     * @param request 获取请求中的token 解析出uid
     * @return resultVO<T></>
     */
    @GetMapping("consumerInfo")
    public ResultVO<Object> getRedisInfo(HttpServletRequest request){
        try {
            return userService.getUserInfo(RequestHandler.paramHandler(request));
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ConstraintViolationException) {
                return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
            }
            e.printStackTrace();
            return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
        }
    }

    /**
     * 更改手机号
     * @param itucode 区号
     * @param phone 手机号
     * @param request token
     * @return
     */
    @PutMapping("ituphone/{itucode}/{phone}")
    public ResultVO updatePhone(@PathVariable("itucode")@NotBlank String itucode,
                                @PathVariable("phone")@NotBlank String phone,HttpServletRequest request){
        try {
            return userService.updatePhone(RequestHandler.paramHandler(request),itucode,phone);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ConstraintViolationException) {
                return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
            }
            e.printStackTrace();
            return ResultUtil.error(null, ResultEnum.MYSQL_OPERATION_FAILED);
        }
    }

    /**
     * 修改密码
     * @param password 密码封装对象 包含新密码和旧密码
     * @param request 获取token
     * @return
     */
    @PostMapping("pwdModifier")
    public ResultVO updatePassword(@Valid Password password, HttpServletRequest request) {
        return userService.updatePassword(password, RequestHandler.paramHandler(request));
    }

}


