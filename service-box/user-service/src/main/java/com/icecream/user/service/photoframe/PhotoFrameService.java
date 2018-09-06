package com.icecream.user.service.photoframe;

import com.icecream.common.model.pojo.SysPhotoFrame;
import com.icecream.common.model.pojo.UserPhotoFrame;
import com.icecream.common.model.requstbody.CreateSysPhotoFrameModel;
import com.icecream.common.model.requstbody.PhotoFrameResponseModel;
import com.icecream.common.model.requstbody.SysPhotoFrameAndUserInfo;
import com.icecream.common.redis.RedisHandler;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.time.DateUtil;
import com.icecream.common.util.uuid.UUIDFactory;
import com.icecream.user.mapper.SysPhotoFrameMapper;
import com.icecream.user.mapper.UserPhotoFrameMapper;
import com.sun.tools.javac.main.Main;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.icecream.common.util.constant.SysConstants.DEV_SYS_PHOTOFRAME_IMG_PREFIX;
import static com.icecream.common.util.constant.SysConstants.PRODUCT_SYS_PHOTOFRAME_IMG_PREFIX;
import static com.icecream.common.util.constant.SysConstants.SYS_PHOTOFRAME;

/**
 * @author hp
 * @version 1.0
 * @description: 头像框功能
 * @date: 16:36 2018/9/4 0004
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class PhotoFrameService {
    @Autowired
    private SysPhotoFrameMapper sysPhotoFrameMapper;

    @Autowired
    private UserPhotoFrameMapper userPhotoFrameMapper;

    @Value("${spring.profiles.active}")
    private String environment;

    /**
     * 获取系统头像框列表
     *
     * @return
     */
    public List<SysPhotoFrame> listSysPhotoFrame() {
        List<SysPhotoFrame> sysPhotoFramesList = new ArrayList<>();
        List<Object> redisSysPhotoFrameList = RedisHandler.getList(SYS_PHOTOFRAME);
        log.info("redis sysPhotoFrame" + redisSysPhotoFrameList.toString());
        if (redisSysPhotoFrameList.isEmpty()) {
            sysPhotoFramesList = sysPhotoFrameMapper.listSysPhotoFrameOrderByLevelAndSort();
            log.info("sysphotoframe list" + sysPhotoFramesList.toString());
            if (sysPhotoFramesList.isEmpty()) {
                return sysPhotoFramesList;
            }
            for (SysPhotoFrame mysqlSysPhotoFrame : sysPhotoFramesList) {
                RedisHandler.addList(SYS_PHOTOFRAME, mysqlSysPhotoFrame);
            }
        } else {
            for (Object redisSysPhotoFrame : redisSysPhotoFrameList) {
                sysPhotoFramesList.add((SysPhotoFrame) redisSysPhotoFrame);
            }
        }
        return sysPhotoFramesList;
    }

    /**
     * 粉丝获取系统头像框列表，加用户头像框信息
     *
     * @param specialTokenId
     * @return
     */
    public ResultVO listSysPhotoFrameWithUserInfo(String specialTokenId) {
        List<SysPhotoFrame> SysPhotoFrameList = listSysPhotoFrame();
        if (SysPhotoFrameList == null) {
            return ResultUtil.error("系统头像框列表为空", ResultEnum.QUERY_RESULT_IS_NULL);
        }
        List<UserPhotoFrame> userPhotoFrameList = userPhotoFrameMapper.listUserPhotoFrame(specialTokenId);
        log.info("userPhotoFrameList=======>" + userPhotoFrameList.toString());
        List<SysPhotoFrameAndUserInfo> listSysPhotoFrameAndUserInfo = new ArrayList<SysPhotoFrameAndUserInfo>();

        for (int i = 0; i < SysPhotoFrameList.size(); i++) {
            SysPhotoFrameAndUserInfo sysPhotoFrameAndUserInfo = new SysPhotoFrameAndUserInfo();
            sysPhotoFrameAndUserInfo.setId(SysPhotoFrameList.get(i).getId());
            sysPhotoFrameAndUserInfo.setName(SysPhotoFrameList.get(i).getName());
            sysPhotoFrameAndUserInfo.setGroupName(SysPhotoFrameList.get(i).getGroupName());
            sysPhotoFrameAndUserInfo.setImg(getSysPhotoFrameImgPrefix() + SysPhotoFrameList.get(i).getImg());
            sysPhotoFrameAndUserInfo.setPrice(SysPhotoFrameList.get(i).getPrice());
            sysPhotoFrameAndUserInfo.setLevel(SysPhotoFrameList.get(i).getLevel());
            int term = SysPhotoFrameList.get(i).getTerm() / 86400;
            sysPhotoFrameAndUserInfo.setTerm(term);

            sysPhotoFrameAndUserInfo.setEndTime(0);
            sysPhotoFrameAndUserInfo.setIsWear(0);
            sysPhotoFrameAndUserInfo.setEndTimeNote("0");
            for (int j = 0; j < userPhotoFrameList.size(); j++) {
                if (userPhotoFrameList.get(j).getFrameId().equals(SysPhotoFrameList.get(i).getId())) {
                    String endTimeNote = "0";
                    if (userPhotoFrameList.get(j).getEndTime() < 0) {
                        endTimeNote = "-9999";
                    }
                    if (userPhotoFrameList.get(j).getEndTime() > 0 && userPhotoFrameList.get(j).getEndTime() > DateUtil.getNowSecondIntTime()) {
                        endTimeNote = (int) Math.ceil((userPhotoFrameList.get(j).getEndTime() - DateUtil.getNowSecondIntTime()) / 86400) + "";
                    }
                    sysPhotoFrameAndUserInfo.setEndTime(userPhotoFrameList.get(j).getEndTime());
                    sysPhotoFrameAndUserInfo.setIsWear(userPhotoFrameList.get(j).getIsWear());
                    sysPhotoFrameAndUserInfo.setEndTimeNote(endTimeNote);
                }
            }
            listSysPhotoFrameAndUserInfo.add(sysPhotoFrameAndUserInfo);
        }

        Map<String, List<SysPhotoFrameAndUserInfo>> map = listSysPhotoFrameAndUserInfo.stream()
                .collect(Collectors.groupingBy(SysPhotoFrameAndUserInfo::getGroupName));

        List<PhotoFrameResponseModel> photoFrameResponseModels = new ArrayList<>();

        Iterator<Map.Entry<String, List<SysPhotoFrameAndUserInfo>>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<SysPhotoFrameAndUserInfo>> entry = entries.next();
            PhotoFrameResponseModel photoFrameResponseModel = new PhotoFrameResponseModel();
            photoFrameResponseModel.setGroupName(entry.getKey());
            photoFrameResponseModel.setSysPhotoFrameResArr(entry.getValue());
            photoFrameResponseModels.add(photoFrameResponseModel);
        }
        return ResultUtil.success(photoFrameResponseModels);
    }

    /**
     * 版主获取头像框列表和是否佩戴
     *
     * @param specialTokenId
     * @return
     */
    public ResultVO listSysPhotoFrameWithStarInfo(String specialTokenId) {
        List<SysPhotoFrame> SysPhotoFrameList = listSysPhotoFrame();
        if (SysPhotoFrameList == null) {
            return ResultUtil.error("系统头像框列表为空", ResultEnum.QUERY_RESULT_IS_NULL);
        }
        List<UserPhotoFrame> userPhotoFrameList = userPhotoFrameMapper.listUserPhotoFrame(specialTokenId);
        log.info("userPhotoFrameList=======>" + userPhotoFrameList.toString());
        List<SysPhotoFrameAndUserInfo> listSysPhotoFrameAndUserInfo = new ArrayList<SysPhotoFrameAndUserInfo>();

        for (int i = 0; i < SysPhotoFrameList.size(); i++) {
            SysPhotoFrameAndUserInfo sysPhotoFrameAndUserInfo = new SysPhotoFrameAndUserInfo();
            sysPhotoFrameAndUserInfo.setId(SysPhotoFrameList.get(i).getId());
            sysPhotoFrameAndUserInfo.setName(SysPhotoFrameList.get(i).getName());
            sysPhotoFrameAndUserInfo.setImg(getSysPhotoFrameImgPrefix() + SysPhotoFrameList.get(i).getImg());
            sysPhotoFrameAndUserInfo.setIsWear(0);

            for (int j = 0; j < userPhotoFrameList.size(); j++) {
                if (userPhotoFrameList.get(j).getFrameId().equals(SysPhotoFrameList.get(i).getId())) {
                    sysPhotoFrameAndUserInfo.setIsWear(userPhotoFrameList.get(j).getIsWear());
                }
            }
            listSysPhotoFrameAndUserInfo.add(sysPhotoFrameAndUserInfo);
        }
        return ResultUtil.success(listSysPhotoFrameAndUserInfo);
    }

    public String getSysPhotoFrameImgPrefix() {
        if ("pro".equals(environment)) {
            return PRODUCT_SYS_PHOTOFRAME_IMG_PREFIX;
        } else {
            return DEV_SYS_PHOTOFRAME_IMG_PREFIX;
        }
    }

    /**
     * 新增系统头像框,并清除redis缓存
     *
     * @param uid
     * @param createSysPhotoFrameModel
     * @return
     */
    public ResultVO saveSysPhotoFrame(int uid, CreateSysPhotoFrameModel createSysPhotoFrameModel) {
        SysPhotoFrame sysPhotoFrame = new SysPhotoFrame();
        Integer maxSort = sysPhotoFrameMapper.getMaxSortByLevel(createSysPhotoFrameModel.getLevel());
        log.info("maxSort: " + maxSort);
        if (maxSort != null) {
            sysPhotoFrame.setSort(maxSort + 1);
        } else {
            sysPhotoFrame.setSort(1);
        }
        sysPhotoFrame.setId(UUIDFactory.create());
        sysPhotoFrame.setName(createSysPhotoFrameModel.getName());
        sysPhotoFrame.setGroupName(createSysPhotoFrameModel.getGroupName());
        sysPhotoFrame.setImg(createSysPhotoFrameModel.getImg());
        sysPhotoFrame.setPrice(createSysPhotoFrameModel.getPrice());
        sysPhotoFrame.setLevel(createSysPhotoFrameModel.getLevel());
        sysPhotoFrame.setCtime(DateUtil.getNowSecondIntTime());
        int insert = sysPhotoFrameMapper.insertSelective(sysPhotoFrame);
        log.info("插入结果: " + insert);
        if (insert != 1) {
            return ResultUtil.error("插入系统头像框失败", ResultEnum.MYSQL_OPERATION_FAILED);
        }
        RedisHandler.remove(SYS_PHOTOFRAME);
        return ResultUtil.success();
    }

    public ResultVO deleteSysPhotoFrame(String specialTokenId, String id) {
        SysPhotoFrame sysPhotoFrame = new SysPhotoFrame();
        sysPhotoFrame.setId(id);
        Short updateIsInuse = 0;
        sysPhotoFrame.setIsInuse(updateIsInuse);
        int update = sysPhotoFrameMapper.updateByPrimaryKeySelective(sysPhotoFrame);
        log.info("删除结果: " + update);
        if (update != 1) {
            return ResultUtil.error("删除系统头像框失败", ResultEnum.MYSQL_OPERATION_FAILED);
        }
        RedisHandler.remove(SYS_PHOTOFRAME);
        return ResultUtil.success();
    }
}
