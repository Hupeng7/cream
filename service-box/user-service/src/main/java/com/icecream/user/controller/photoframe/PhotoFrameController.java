package com.icecream.user.controller.photoframe;

import com.icecream.common.model.requstbody.CreateSysPhotoFrameModel;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.photoframe.PhotoFrameService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("list")
    public ResultVO listSysPhotoFrameWithUserInfo(@Param("specialTokenId") String specialTokenId) {
        return photoFrameService.listSysPhotoFrameWithUserInfo(specialTokenId);
    }

    @GetMapping("/star/list")
    public ResultVO listSysPhotoFrameWithStarInfo(@Param("specialTokenId") String specialTokenId) {
        return photoFrameService.listSysPhotoFrameWithStarInfo(specialTokenId);
    }

    @PostMapping("/saveSysPhotoFrame")
    public ResultVO saveSysPhotoFrame(@Param("specialTokenId") String specialTokenId, @Validated @RequestBody CreateSysPhotoFrameModel createSysPhotoFrameModel) {
        return photoFrameService.saveSysPhotoFrame(Integer.parseInt(specialTokenId), createSysPhotoFrameModel);
    }

    @DeleteMapping("/deleteSysPhotoFrame/{id}")
    public ResultVO deleteSysPhotoFrame(@Param("specialTokenId") String specialTokenId, @PathVariable("id") String id) {
        return photoFrameService.deleteSysPhotoFrame(specialTokenId, id);
    }


}
