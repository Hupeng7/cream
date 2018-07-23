package com.icecream.user.mapper;

import com.icecream.common.model.pojo.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/23 0023
 */
@Mapper
public interface RolePermissionMapper extends tk.mybatis.mapper.common.Mapper<RolePermission>,MySqlMapper<RolePermission> {
}
