package com.icecream.user.mapper;

import com.icecreamGroup.common.model.UserStar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.MySqlMapper;

@Mapper
public interface UserStarMapper extends tk.mybatis.mapper.common.Mapper<UserStar>,MySqlMapper<UserStar> {

    @Select("select id,smallavatar,avatar,driver,role,status,sex,birthday,exp,expid,scoreid from user_star where id=#{uid}")
    @ResultType(com.icecreamGroup.common.model.UserStar.class)
    public UserStar getCache(Integer uid);
}