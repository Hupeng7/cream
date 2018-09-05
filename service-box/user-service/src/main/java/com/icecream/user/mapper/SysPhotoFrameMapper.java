package com.icecream.user.mapper;

import com.icecream.common.model.pojo.SysPhotoFrame;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 17:06 2018/9/4 0004
 */
@Mapper
public interface SysPhotoFrameMapper extends tk.mybatis.mapper.common.Mapper<SysPhotoFrame>, MySqlMapper<SysPhotoFrame> {

    /**
     * 查询系统头像框
     * 根据等级正序、排序字段正序
     * is_inuse=1
     *
     * @return
     */
    @Select("SELECT id,name,group_name as groupName,img,price,level,term,sort,sale_num as saleNum,ctime,is_inuse as isInuse " +
            "FROM sys_photo_frame WHERE is_inuse=1 order by level asc,sort asc")
    @ResultType(SysPhotoFrame.class)
    List<SysPhotoFrame> listSysPhotoFrameOrderByLevelAndSort();


}
