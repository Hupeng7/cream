package com.icecream.user.mapper;

import com.icecream.common.model.pojo.UserPhotoFrame;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 10:55 2018/9/5 0005
 */
@Mapper
public interface UserPhotoFrameMapper extends tk.mybatis.mapper.common.Mapper<UserPhotoFrame>, MySqlMapper<UserPhotoFrame> {

    @Select("SELECT id,uid,frame_id as frameId,frame_img as frameImg,ctime,end_time as endTime,is_wear as isWear," +
            "is_inuse as isInuse FROM user_photo_frame WHERE uid=#{specialTokenId} AND is_inuse=1")
    @ResultType(UserPhotoFrame.class)
    List<UserPhotoFrame> listUserPhotoFrame(String specialTokenId);
}
