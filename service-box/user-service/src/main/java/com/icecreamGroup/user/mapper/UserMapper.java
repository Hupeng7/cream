package com.icecreamGroup.user.mapper;

import com.icecreamGroup.common.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.MySqlMapper;

@Mapper
public interface UserMapper extends tk.mybatis.mapper.common.Mapper<User>,MySqlMapper<User> {
}