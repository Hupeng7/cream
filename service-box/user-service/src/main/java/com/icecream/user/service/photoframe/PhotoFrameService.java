package com.icecream.user.service.photoframe;

import com.icecream.common.model.pojo.SysPhotoFrame;
import com.icecream.common.model.pojo.UserPhotoFrame;
import com.icecream.common.model.requstbody.PhotoFrameResponseModel;
import com.icecream.common.model.requstbody.SysPhotoFrameAndUserInfo;
import com.icecream.common.redis.RedisHandler;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.time.DateUtil;
import com.icecream.user.mapper.SysPhotoFrameMapper;
import com.icecream.user.mapper.UserPhotoFrameMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.icecream.common.util.constant.SysConstants.DEV_SYS_PHOTOFRAME_PREFIX;
import static com.icecream.common.util.constant.SysConstants.SYS_PHOTOFRAME;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
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
            sysPhotoFrameAndUserInfo.setImg(DEV_SYS_PHOTOFRAME_PREFIX + SysPhotoFrameList.get(i).getImg());
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
}
