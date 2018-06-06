package com.icecreamGroup.user.service;

import com.codingapi.tx.annotation.TxTransaction;
import com.icecreamGroup.user.mapper.UserMapper;
import com.icecreamGroup.user.feignClients.CommentsClient;
import com.icecreamGroup.user.feignClients.OrderFeignClient;
import com.icecreamGroup.user.utils.UserBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {
    @Autowired
    private CommentsClient commentsClient;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private UserMapper userMapper;

    @TxTransaction(isStart = true)
    @Transactional
    public Integer insert(){
        int count1 = userMapper.insertSelective(UserBuilder.buildUser());
        int count2 = orderFeignClient.insert();
        if(count1>0&&count2>0)
           log.info("插入成功");
        else
            log.error("插入失败");
        return 0;
    }
}