package com.icecream.user.controller.photoframe;

import com.icecream.common.model.eunm.OperatorRole;
import com.icecream.common.model.model.CreateSysPhotoFrameModel;
import com.icecream.common.util.aspect.annotation.Operator;
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
    @Operator(role = OperatorRole.CONSUMER)
    public ResultVO listSysPhotoFrameWithUserInfo(@Param("specialTokenId") String specialTokenId) {
        return photoFrameService.listSysPhotoFrameWithUserInfo(specialTokenId);
    }

    @GetMapping("/star/list")
    @Operator(role = OperatorRole.STAR)
    public ResultVO listSysPhotoFrameWithStarInfo(@Param("specialTokenId") String specialTokenId) {
        return photoFrameService.listSysPhotoFrameWithStarInfo(specialTokenId);
    }

    @PostMapping("/saveSysPhotoFrame")
    @Operator(role = OperatorRole.STAR)
    public ResultVO saveSysPhotoFrame(@Param("specialTokenId") String specialTokenId, @Validated @RequestBody CreateSysPhotoFrameModel createSysPhotoFrameModel) {
        return photoFrameService.saveSysPhotoFrame(createSysPhotoFrameModel);
    }

    @DeleteMapping("/deleteSysPhotoFrame/{id}")
    @Operator(role = OperatorRole.STAR)
    public ResultVO deleteSysPhotoFrame(@Param("specialTokenId") String specialTokenId, @PathVariable("id") String frameId) {
        return photoFrameService.deleteSysPhotoFrame(frameId);
    }

    @PostMapping("/star/saveUserPhotoFrame/{id}")
    //@Operator(role = OperatorRole.STAR)
    public ResultVO saveStarUserPhotoFrame(@Param("specialTokenId") String specialTokenId, @PathVariable("id") String frameId) {
        return photoFrameService.saveStarUserPhotoFrame(specialTokenId, frameId);
    }
}
