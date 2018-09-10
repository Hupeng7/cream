package com.icecream.user.service.photoframe;

import com.icecream.common.model.model.CreateSysPhotoFrameModel;
import com.icecream.common.model.model.PhotoFrameResponseModel;
import com.icecream.common.model.model.SysPhotoFrameAndUserInfo;
import com.icecream.common.model.pojo.SysPhotoFrame;
import com.icecream.common.model.pojo.UserPhotoFrame;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.time.DateUtil;
import com.icecream.common.util.uuid.UUIDFactory;
import com.icecream.user.mapper.SysPhotoFrameMapper;
import com.icecream.user.mapper.UserPhotoFrameMapper;
import com.icecream.user.redis.RedisHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.icecream.common.util.constant.SysConstants.*;
import static com.icecream.user.constants.Constants.*;

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
     * @param createSysPhotoFrameModel
     * @return
     */
    public ResultVO saveSysPhotoFrame(CreateSysPhotoFrameModel createSysPhotoFrameModel) {
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

    /**
     * 删除系统头像框，并清除缓存
     *
     * @param id
     * @return
     */
    public ResultVO deleteSysPhotoFrame(String id) {
        SysPhotoFrame sysPhotoFrame = new SysPhotoFrame();
        sysPhotoFrame.setId(id);
        Integer updateIsInuse = 0;
        sysPhotoFrame.setIsInuse(updateIsInuse);
        int update = sysPhotoFrameMapper.updateByPrimaryKeySelective(sysPhotoFrame);
        log.info("删除结果: " + update);
        if (update != 1) {
            return ResultUtil.error("删除系统头像框失败", ResultEnum.MYSQL_OPERATION_FAILED);
        }
        RedisHandler.remove(SYS_PHOTOFRAME);
        return ResultUtil.success();
    }

    /**
     * 版主佩戴头像框
     *
     * @param specialTokenId
     * @param frameId
     * @return
     */
    public ResultVO saveStarUserPhotoFrame(String specialTokenId, String frameId) {
        SysPhotoFrame sysPhotoFrame = new SysPhotoFrame();
        sysPhotoFrame.setId(frameId);
        sysPhotoFrame.setIsInuse(Integer.parseInt("1"));
        SysPhotoFrame sysPhotoFrameResult = sysPhotoFrameMapper.selectOne(sysPhotoFrame);
        if (sysPhotoFrameResult == null) {
            return ResultUtil.error("未能获取该系统头像框", ResultEnum.PARAMS_ERROR);
        }
        Object object = RedisHandler.get(USER_PHOTOFRAME + SYMBOL_COLON + specialTokenId);
        String checkIsWearUserPhotoFrame = object != null ? object.toString() : "";
        if (sysPhotoFrameResult.getImg() != null && checkIsWearUserPhotoFrame.equals(sysPhotoFrameResult.getImg())) {
            return ResultUtil.success("该头像框已佩戴");
        }

        UserPhotoFrame userPhotoFrame = new UserPhotoFrame();
        Integer uid = Integer.parseInt(specialTokenId);
        userPhotoFrame.setUid(uid);
        userPhotoFrame.setIsInuse(Integer.parseInt("1"));

        userPhotoFrame.setFrameId(sysPhotoFrameResult.getId());
        UserPhotoFrame userPhotoFrameResult = userPhotoFrameMapper.selectOne(userPhotoFrame);
        Boolean flag = false;
        if (userPhotoFrameResult == null) {
            userPhotoFrame.setId(UUIDFactory.create());
            userPhotoFrame.setFrameImg(sysPhotoFrameResult.getImg());
            userPhotoFrame.setCtime(DateUtil.getNowSecondIntTime());
            userPhotoFrame.setIsWear((int) WEAR);
            userPhotoFrame.setEndTime(-1);
            int insert = userPhotoFrameMapper.insert(userPhotoFrame);
        }
        flag = wearUserPhotoFrame(uid, frameId);
        if (flag) {
            userPhotoFrame.setFrameImg(sysPhotoFrameResult.getImg());
            redisWearUserPhotoFrame(uid, userPhotoFrameResult);
        } else {
            return ResultUtil.error("佩戴头像框失败", ResultEnum.MYSQL_OPERATION_FAILED);
        }
        userPhotoFrame.setFrameImg(getSysPhotoFrameImgPrefix() + sysPhotoFrameResult.getImg());
        return ResultUtil.success(userPhotoFrame);
    }

    /**
     * 版主/粉丝佩戴头像框mysql操作
     *
     * @param uid
     * @param frameId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    private Boolean wearUserPhotoFrame(Integer uid, String frameId) {
        List<UserPhotoFrame> list = userPhotoFrameMapper.selectExcept(uid, frameId);
        int updateAll = 0;
        if (list.size() > 0) {
            updateAll = userPhotoFrameMapper.updateAllIsWear(uid, frameId, NOTWEAR);
        } else {
            updateAll = 1;
        }
        int update = userPhotoFrameMapper.updateIsWear(uid, frameId, WEAR);
        if (update > 0 && updateAll > 0) {
            return true;
        }
        return false;
    }

    /**
     * 粉丝佩戴头像框
     *
     * @param specialTokenId
     * @param frameId
     * @return
     */
    public ResultVO saveUserPhotoFrame(String specialTokenId, String frameId) {
        /**
         * 过程：
         * 1.判断头像框是否存在 不存在跳出3003 存在继续  判断等级够不够 不够则跳出 够则继续
         * 2.判断用户是否佩戴 已佩戴跳出2000  否则继续
         * 3.未佩戴  若是price==0
         *   判断用户是否拥有该图像框，已有 则佩戴
         *   没有则插入， 佩戴
         * 4.若是price>0 则购买，佩戴
         *   判断用户是否拥有该图像框，已有图像框且不过期  则佩戴
         *   没有则插入 ,过期则更新， 佩戴
         */
        SysPhotoFrame sysPhotoFrame = new SysPhotoFrame();
        sysPhotoFrame.setId(frameId);
        sysPhotoFrame.setIsInuse(Integer.parseInt("1"));
        SysPhotoFrame sysPhotoFrameResult = sysPhotoFrameMapper.selectOne(sysPhotoFrame);
        if (sysPhotoFrameResult == null) {
            return ResultUtil.error("未能获取该系统头像框", ResultEnum.PARAMS_ERROR);
        }
        //todo 判断用户等级sysPhotoFrameResult.getLevel<=user.getLevel 继续；否则跳出
        Object object = RedisHandler.get(USER_PHOTOFRAME + SYMBOL_COLON + specialTokenId);
        String checkIsWearUserPhotoFrame = object != null ? object.toString() : "";
        if (sysPhotoFrameResult.getImg() != null && checkIsWearUserPhotoFrame.equals(sysPhotoFrameResult.getImg())) {
            return ResultUtil.success("该头像框已佩戴");
        }
        UserPhotoFrame userPhotoFrame = new UserPhotoFrame();
        Integer uid = Integer.parseInt(specialTokenId);
        userPhotoFrame.setUid(uid);
        userPhotoFrame.setIsInuse(Integer.parseInt("1"));
        userPhotoFrame.setFrameId(sysPhotoFrameResult.getId());
        UserPhotoFrame userPhotoFrameResult = userPhotoFrameMapper.selectOne(userPhotoFrame);
        if (sysPhotoFrameResult.getPrice().intValue() <= 0) {
            if (userPhotoFrameResult == null) {
                userPhotoFrame.setId(UUIDFactory.create());
                userPhotoFrame.setFrameImg(sysPhotoFrameResult.getImg());
                userPhotoFrame.setCtime(DateUtil.getNowSecondIntTime());
                userPhotoFrame.setIsWear((int) WEAR);
                userPhotoFrame.setEndTime(-1);
                int insert = userPhotoFrameMapper.insert(userPhotoFrame);
            }
        } else {
            if (userPhotoFrameResult != null) {
                if (userPhotoFrameResult.getEndTime() > 0 && userPhotoFrameResult.getEndTime() < DateUtil.getNowSecondIntTime()) {
                    //todo buyPhotoFrame
                    //if true
                    userPhotoFrame.setEndTime(DateUtil.getNowSecondIntTime() + MONTH_TO_SECOND);
                    int updateEndTime = userPhotoFrameMapper.updateByPrimaryKeySelective(userPhotoFrame);
                }
            } else {
                String userPhotoFrameUUID = UUIDFactory.create();
                //todo check user balance
                //先添加后购买
                userPhotoFrame.setId(userPhotoFrameUUID);
                userPhotoFrame.setFrameImg(sysPhotoFrameResult.getImg());
                userPhotoFrame.setCtime(DateUtil.getNowSecondIntTime());
                userPhotoFrame.setIsWear((int) WEAR);
                userPhotoFrame.setEndTime(DateUtil.getNowSecondIntTime() + MONTH_TO_SECOND);
                int insert = userPhotoFrameMapper.insert(userPhotoFrame);

                //todo buyPhotoFrame
            }
        }

        Boolean flag = false;
        flag = wearUserPhotoFrame(uid, frameId);
        if (flag) {
            userPhotoFrame.setFrameImg(sysPhotoFrameResult.getImg());
            redisWearUserPhotoFrame(uid, userPhotoFrame);
        } else {
            return ResultUtil.error("佩戴头像框失败", ResultEnum.MYSQL_OPERATION_FAILED);
        }
        userPhotoFrame.setFrameImg(getSysPhotoFrameImgPrefix() + sysPhotoFrameResult.getImg());
        return ResultUtil.success(userPhotoFrame);
    }

    private void redisWearUserPhotoFrame(Integer uid, UserPhotoFrame userPhotoFrame) {
        if (userPhotoFrame.getEndTime() < 0) {
            RedisHandler.set(USER_PHOTOFRAME + SYMBOL_COLON + uid, userPhotoFrame.getFrameId());
        } else {
            Long ttl = (Long) userPhotoFrame.getEndTime().longValue() - DateUtil.getNowSecondIntTime();
            RedisHandler.set(USER_PHOTOFRAME + SYMBOL_COLON + uid, userPhotoFrame.getFrameImg(), ttl);
        }
    }


}
