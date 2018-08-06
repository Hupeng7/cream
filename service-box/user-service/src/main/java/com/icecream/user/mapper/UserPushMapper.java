package com.icecream.user.mapper;

import com.icecream.common.model.pojo.UserPush;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/6/15 0015
 */
@Mapper
public interface UserPushMapper extends tk.mybatis.mapper.common.Mapper<UserPush>,MySqlMapper<UserPush> {
}
