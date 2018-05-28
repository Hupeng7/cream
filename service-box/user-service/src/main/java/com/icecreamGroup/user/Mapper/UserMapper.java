package com.icecreamGroup.user.Mapper;

import com.icecreamGroup.common.model.User;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

@Mapper
public interface UserMapper extends tk.mybatis.mapper.common.Mapper<User>,MySqlMapper<User> {
}