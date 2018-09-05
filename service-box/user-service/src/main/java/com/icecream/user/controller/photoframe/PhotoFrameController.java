package com.icecream.user.controller.photoframe;

import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.photoframe.PhotoFrameService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hp
 * @version 1.0
 * @description: 头像框相关
 * @date: 16:33 2018/9/4 0004
 */
@RestController
@RequestMapping("photoframe")
public class PhotoFrameController {
    @Autowired
    private PhotoFrameService photoFrameService;

    @RequestMapping("list")
    public ResultVO listSysPhotoFrameWithUserInfo(@Param("specialTokenId") String specialTokenId) {
        return photoFrameService.listSysPhotoFrameWithUserInfo(specialTokenId);
    }

    @RequestMapping("/star/list")
    public ResultVO listSysPhotoFrameWithStarInfo(@Param("specialTokenId") String specialTokenId) {
        return photoFrameService.listSysPhotoFrameWithStarInfo(specialTokenId);
    }


}
