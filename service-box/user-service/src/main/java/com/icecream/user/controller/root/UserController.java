package com.icecream.user.controller.root;

import com.icecream.common.model.model.Password;
import com.icecream.common.model.model.Phone;
import com.icecream.common.model.model.SmsLoginOrRegisterParams;
import com.icecream.common.model.pojo.User;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.config.login.AppIdConfig;
import com.icecream.user.feignclients.CommentsClient;
import com.icecream.user.feignclients.OrderFeignClient;
import com.icecream.user.redis.RedisHandler;
import com.icecream.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @version 2.0
 */
@Slf4j
@RestController
@Api(value = "用户接口",description = "粉丝操作",tags ="user-root-api")
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
    private RedisHandler redisHandler;


   /* @GetMapping("loading")
    public void loadingUserCache() {
        userService.getList().stream().filter(user -> user != null & user.getId() != null)
                .forEach(user -> redisHandler.addMap(USER_HASH_PREFIX,
                        user.getId().toString(), JSON.toJSONString(user)));
    }*/

    /**
     * 修改个人信息
     *
     * @param user    需要修改的字段封装实体类
     * @param request 从该对象中解析token获取uid
     * @return ResultVO<T>
     */
    @ApiOperation(value = "【需要粉丝端token】修改个人信息")
    @PatchMapping(value = "update")
    public ResultVO updateUserInfo(@RequestBody User user, @RequestParam("specialTokenId") String specialTokenId) {
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
    @ApiOperation(value = "【需要粉丝端token】根据id查询用户")
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
    @ApiOperation(value = "【需要粉丝端token】查询用户是否设置了密码")
    public ResultVO isSetPassword(@PathVariable("itucode") String itucode,
                                  @PathVariable("phone") String phone,
                                  @RequestParam("specialTokenId") String specialTokenId) {
        return userService.isSetPassword(itucode, phone, specialTokenId);
    }


    /**
     * 验证手机码
     * @param specialTokenId
     * @param code
     * @return
     */
    @GetMapping("authCodes/{Code}/verify")
    @ApiOperation(value = "【需要粉丝端token】查询用户是否设置了密码")
    public ResultVO checkCode(@RequestParam("specialTokenId") String specialTokenId,
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
    public ResultVO<Object> getRedisInfo(@RequestParam("specialTokenId") String specialTokenId) {
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
    @ApiOperation(value = "【需要粉丝端token】更改手机号")
    public ResultVO updatePhone(@PathVariable("itucode") @NotBlank String itucode,
                                @PathVariable("phone") @NotBlank String phone,
                                @NotBlank @RequestParam("specialTokenId") String specialTokenId) {
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
    @ApiOperation(value = "【需要粉丝端token】修改密码")
    public ResultVO updatePassword(@Validated @RequestBody Password password,
                                   @NotBlank @RequestParam("specialTokenId") String specialTokenId) {
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
    @ApiOperation(value = "【需要粉丝端token】验证验证码，并修改密码")
    public ResultVO updateByCodeAndPasswrod(@Validated @RequestBody SmsLoginOrRegisterParams smsLoginOrRegisterParams,
                                            @NotBlank @RequestParam("specialTokenId") String specialTokenId) {
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
    @ApiOperation(value = "【需要粉丝端token】判断手机是否存在")
    public ResultVO ifExistPhone(@PathVariable("itucode") @NotBlank String itucode,
                                 @PathVariable("phone") @NotBlank String phone,
                                 @NotBlank @RequestParam("specialTokenId") String specialTokenId) {
        return userService.ifExistPhone(itucode + phone, specialTokenId);
    }

    /**
     * 验证手机号
     *
     * @param phone
     * @param request
     * @return
     */
    @PostMapping("checkPhoneInfo")
    @ApiOperation(value = "【需要粉丝端token】验证手机号")
    public ResultVO checkNewPhoneInfo(@Validated @RequestBody Phone phone,
                                      @NotBlank @RequestParam("specialTokenId") String specialTokenId) {
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
    @ApiOperation(value = "【需要粉丝端token】验证手机号对应的密码是否存在")
    public ResultVO checkPassword(@Validated @RequestBody Phone phone,
                                  @NotBlank @RequestParam("specialTokenId") String specialTokenId) {
        return userService.checkPassword(phone, specialTokenId);
    }

    /**
     * 更换手机号
     *
     * @param smsloginparams
     * @return
     */
    @PostMapping("phones")
    @ApiOperation(value = "【需要粉丝端token】更换手机号")
    public ResultVO changePhones(@Validated @RequestBody SmsLoginOrRegisterParams smsLoginOrRegisterParams,
                                 @NotBlank @RequestParam("specialTokenId") String specialTokenId) {
        return userService.changePhones(smsLoginOrRegisterParams, specialTokenId);
    }


    /**
     * 获取用户列表
     * @return
     */
    @GetMapping("getUserList")
    @ApiOperation(value = "【需要粉丝端token】获取用户列表")
    public List<User> getUserList() {
        return userService.getList();
    }


}


