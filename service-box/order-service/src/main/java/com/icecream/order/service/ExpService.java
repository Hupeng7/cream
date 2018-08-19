package com.icecream.order.service;

import com.icecream.common.model.pojo.UserExp;
import com.icecream.common.util.time.DateUtil;
import com.icecream.order.mapper.ExpMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author Mr_h
 * @version 1.0
 * description: 下订单加用户经验
 * create by Mr_h on 2018/8/15 0015
 */
@Service
@SuppressWarnings("all")
public class ExpService {

    @Autowired
    private ExpMapper expMapper;

    public UserExp query(Integer uid){
        UserExp userExp = new UserExp();
        return expMapper.selectOne(userExp);
    }

    //插入经验数据
    public int insert(UserExp userExp){
        return expMapper.insertSelective(userExp);
    }

    //更新经验数据
    public int update(UserExp userExp,BigDecimal price){
        BigDecimal exp = userExp.getExp();
        exp = exp.add(price);
        userExp.setExp(exp);
        return expMapper.updateByPrimaryKey(userExp);
    }


    //插入经验或者更新
    public void insertOrUpdateHandler(Integer uid,Integer sid,BigDecimal price){
        expMapper.concurrentInsertExp(sid, uid, price.intValue(), DateUtil.getNowSecondIntTime());
    }




    private UserExp builUserExp(Integer uid,Integer sid,BigDecimal price){
        UserExp userExp = new UserExp();
        userExp.setCtime(DateUtil.getNowSecondIntTime());
        userExp.setExp(price);
        userExp.setSid(sid);
        userExp.setUid(uid);
        return userExp;
    }
}
