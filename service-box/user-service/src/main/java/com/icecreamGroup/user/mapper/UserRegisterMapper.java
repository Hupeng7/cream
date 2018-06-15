package com.icecreamGroup.user.mapper;

import com.icecreamGroup.common.model.UserRegister;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/15 0015
 */
@Mapper
public interface UserRegisterMapper extends tk.mybatis.mapper.common.Mapper<UserRegister>,MySqlMapper<UserRegister> {
}
