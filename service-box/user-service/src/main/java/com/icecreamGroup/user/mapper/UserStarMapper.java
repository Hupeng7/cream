package com.icecreamGroup.user.mapper;

import com.icecreamGroup.common.model.UserStar;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

@Mapper
public interface UserStarMapper extends tk.mybatis.mapper.common.Mapper<UserStar>,MySqlMapper<UserStar> {
}