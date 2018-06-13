package com.icecreamGroup.user.service;

import com.codingapi.tx.annotation.TxTransaction;
import com.icecreamGroup.common.model.User;
import com.icecreamGroup.common.model.UserNameAndPasswordLogin;
import com.icecreamGroup.common.model.UserStar;
import com.icecreamGroup.common.util.jwt.JwtHelper;
import com.icecreamGroup.user.mapper.UserMapper;
import com.icecreamGroup.user.feignClients.OrderFeignClient;
import com.icecreamGroup.user.mapper.UserStarMapper;
import com.icecreamGroup.user.utils.UserBuilder;
import com.icecreamGroup.user.utils.token.TokenHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserStarMapper userStarMapper;

    @Autowired
    private TokenHandler tokenHandler;

    @TxTransaction(isStart = true)
    @Transactional
    public Integer insert() {
        int count1 = userMapper.insert(UserBuilder.buildUser());
        int count2 = orderFeignClient.insert();
        if (count1 > 0 && count2 > 0) {
            log.info("插入成功");
            return 1;
        } else {
            log.error("插入失败");
            return 0;
        }
    }

    public String login(UserNameAndPasswordLogin loginArgs){
        if(loginArgs.getType()==1) {
            UserStar args = new UserStar();
            args.setName(loginArgs.getUserName());
            args.setPassword(loginArgs.getPassword());
            UserStar star= userStarMapper.selectOne(args);
            if(star!=null){
                //查出user,生成token
                return JwtHelper.createJWTForStar(3600000000L,"star",star);
            }else {
                //查不出user,拒绝登陆
                return "";
            }
        }else {
            User args= new User();
            args.setName(loginArgs.getUserName());
            args.setPassword(loginArgs.getPassword());
            User user = userMapper.selectOne(args);
            if(user!=null){
                //查出user,生成token
                return JwtHelper.createJWT(3600000000L,"customer",user);
            }else {
                //查不出user,拒绝登陆
                return "";
            }
        }

    }


}
