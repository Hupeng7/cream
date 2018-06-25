package com.icecream.user.mapper;

import com.icecreamGroup.common.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.MySqlMapper;

@Mapper
public interface UserMapper extends tk.mybatis.mapper.common.Mapper<User>,MySqlMapper<User> {

    @Select("select id,smallavatar,avatar,driver,role,status,sex,birthday,exp,expid,scoreid from user where id=#{uid}")
    @ResultType(com.icecreamGroup.common.model.User.class)
    public User getCache(Integer uid);
}