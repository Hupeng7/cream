package com.icecream.user.mapper;

import com.icecream.common.model.pojo.UserAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @version 2.0
 */
@Mapper
public interface MybatisUserAuthMapper {

    UserAuth isHaveBeenRegistered(@Param("identity_type")Integer type,
                                  @Param("identifier") String openId);
}