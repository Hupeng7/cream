package com.icecream.user.service.binding;

import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.model.requstbody.BindingModel;
import com.icecream.common.model.requstbody.WxLoginParams;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserAuthMapper;
import com.icecream.user.service.UserService;
import com.icecream.user.service.login.auth.WxLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.icecream.user.constants.Constants.*;

/**
 * @author Mr_h
 * @version 2.0
 * description: 用户登陆方式
 * create by Mr_h on 2018/6/25 0025
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class UserAuthService {

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private WxLoginService wxLoginService;

    @Autowired
    private UserService userService;

    /**
     * 绑定第三方登录方式
     *
     * @param bindingModel
     * @param uid
     * @return
     */
    public ResultVO binding(BindingModel bindingModel, String uid) {
        BindingModel analysis = analysis(bindingModel);
        if (analysis == null) return null;
        User user = userService.getUserInfoByUid(Integer.parseInt(uid));
        if (user != null) {
            UserAuth userAuth = new UserAuth();
            userAuth.setUid(user.getId());
            userAuth.setIdentifier(analysis.getOpenId());
            userAuth.setIdentityType(analysis.getIdentityType());
            int count = userAuthMapper.insertSelective(userAuth);
            if (count > 0) {
                return ResultUtil.success();
            }
        } else {
            return ResultUtil.error(null, ResultEnum.TOKEN_INFO_ERROR);
        }
        return ResultUtil.error(null, ResultEnum.ERROR_UNKNOWN);
    }


    /**
     * 解绑第三方平台信息
     *
     * @param type
     * @param uid
     * @return
     */
    public ResultVO unbinding(Integer type, String uid) {
        if (uid == null) return ResultUtil.error(null, ResultEnum.TOKEN_INFO_ERROR);
        User user = userService.getUserInfoByUid(Integer.parseInt(uid));
        if (user != null) {
            UserAuth userAuth = new UserAuth();
            userAuth.setId(user.getId());
            userAuth.setIdentityType(type);
            int count = userAuthMapper.delete(userAuth);
            if (count > 0) {
                return ResultUtil.success();
            } else {
                return ResultUtil.error("null", ResultEnum.DATA_ERROR);
            }
        } else {
            return ResultUtil.error(null, ResultEnum.DATA_ERROR);
        }
    }


    public BindingModel analysis(BindingModel bindingModel) {
        if (bindingModel.getIdentityType() == TYPE_AUTH_WX) {
            WxLoginParams wxLoginParams = new WxLoginParams();
            wxLoginParams.setCode(bindingModel.getCode());
            List<String> strings = wxLoginService.callRemoteInterFaceForWxLoginStepOne(wxLoginParams);
            bindingModel.setOpenId(strings.get(0));
            bindingModel.setAccessToken(strings.get(1));
        }
        if (bindingModel.getIdentityType() == TYPE_AUTH_WB) {
            bindingModel.setOpenId(bindingModel.getUid());
        }
        return bindingModel;
    }


    /**
     * 注册时插入auth表
     *
     * @param user 粉丝对象
     * @param type 登录方式
     * @return
     */
    public Integer insertUserAuthByType(User user, Integer type) {
        UserAuth userAuth = new UserAuth();
        userAuth.setUid(user.getId());
        userAuth.setIdentityType(type);
        userAuth.setIdentifier(user.getItucode() + user.getPhone());
        userAuth.setCredential(user.getPassword());
        return userAuthMapper.insertSelective(userAuth);
    }


    /**
     * 版主端插入auth表
     *
     * @param star 版主对象
     * @return
     */
    public Integer insertUserAuthByType(UserStar star) {
        UserAuth userAuth = new UserAuth();
        userAuth.setIdentityType(TYPE_ACCOUNT);
        userAuth.setUid(star.getId());
        userAuth.setCredential(star.getPassword());
        return userAuthMapper.insertSelective(userAuth);
    }

    public UserAuth get(String key,Integer type){
        UserAuth args = new UserAuth();
        args.setIdentifier(key);
        args.setIdentityType(type);
        UserAuth result = userAuthMapper.selectOne(args);
        return result;
    }

    public UserAuth get(Integer uid, String password) {
        UserAuth arg = new UserAuth();
        arg.setUid(uid);
        arg.setCredential(password);
        UserAuth result = userAuthMapper.selectOne(arg);
        return result;
    }

    public UserAuth getByType(Integer type, String flag) {
        UserAuth arg = new UserAuth();
        arg.setIdentityType(type);
        arg.setIdentifier(flag);
        UserAuth result = userAuthMapper.selectOne(arg);
        return result;
    }


    public UserAuth getByIdentifer(Integer uid, String identifer) {
        UserAuth arg = new UserAuth();
        arg.setUid(uid);
        arg.setIdentifier(identifer);
        UserAuth result = userAuthMapper.selectOne(arg);
        return result;
    }

    public UserAuth getByType(Integer uid, Integer type) {
        UserAuth arg = new UserAuth();
        arg.setUid(uid);
        arg.setIdentityType(type);
        UserAuth result = userAuthMapper.selectOne(arg);
        return result;
    }

    public ResultVO getAllAuths(String uid) {
        if(uid==null||uid.equals("")) return ResultUtil.error(null,ResultEnum.TOKEN_INFO_ERROR);
        UserAuth userAuth = new UserAuth();
        userAuth.setUid(Integer.parseInt(uid));
        List<UserAuth> select = userAuthMapper.select(userAuth);
        if (!select.isEmpty()) {
            return ResultUtil.success(select);
        } else {
            return ResultUtil.error(null, ResultEnum.PARAMS_ERROR);
        }
    }


}
