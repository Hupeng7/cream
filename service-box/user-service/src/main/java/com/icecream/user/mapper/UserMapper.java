package com.icecream.user.mapper;

import com.icecream.common.model.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.MySqlMapper;

@Mapper
public interface UserMapper extends tk.mybatis.mapper.common.Mapper<User>,MySqlMapper<User> {

    @Select("select id,smallavatar,avatar,driver,role,status,sex,birthday,exp,expid,scoreid from user where id=#{uid}")
    @ResultType(User.class)
    User getCache(Integer uid);

    @Select("SELECT count(*) FROM `user` WHERE ctime BETWEEN #{start} AND #{end}")
    @ResultType(Integer.class)
    Integer getUserCountInWeek(@Param("start") Integer startTime,@Param("end") Integer endTime);
}