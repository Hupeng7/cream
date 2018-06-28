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
    User getCache(Integer uid);

    @Select("SELECT *FROM `user`WHERE ctime BETWEEN #{startTime} AND #{endTime}")
    @ResultType(Integer.class)
    Integer getUserCountInWeek(Integer startTime,Integer endTime);
}