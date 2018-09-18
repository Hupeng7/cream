package com.icecream.user.mapper;

import com.icecream.common.model.pojo.UserStar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.MySqlMapper;

@Mapper
public interface UserStarMapper extends tk.mybatis.mapper.common.Mapper<UserStar>,MySqlMapper<UserStar> {

    @Select("select id,smallavatar,avatar,driver,role,status,sex,birthday,exp from user_star where id=#{uid}")
    @ResultType(UserStar.class)
    UserStar getUserStarBriefInfo(Integer uid);
}