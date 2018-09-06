package com.icecream.common.model.model;

import lombok.Data;

import java.util.List;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 11:22 2018/9/5 0005
 */
@Data
public class PhotoFrameResponseModel {
    private String groupName;

    private List<SysPhotoFrameAndUserInfo> sysPhotoFrameResArr;


}
