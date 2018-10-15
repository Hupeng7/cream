package com.icecream.user.service.channel;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.model.ListChannelDisplayModel;
import com.icecream.common.model.pojo.ChannelDisplayUserSort;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.uuid.UUIDFactory;
import com.icecream.user.mapper.ChannelDisplayMapper;
import com.icecream.user.mapper.ChannelDisplayUserSortMapper;
import com.icecream.user.mapper.ChannelMapper;
import com.icecream.user.redis.RedisHandler;
import com.icecream.user.utils.time.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.icecream.common.util.constant.SysConstants.SYMBOL_COLON;
import static com.icecream.user.constants.Constants.USER_CHANNELDISPLAY_SORT;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 11:44 2018/9/10 0010
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class ChannelService {
    @Autowired
    private ChannelDisplayMapper channelDisplayMapper;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private ChannelDisplayUserSortMapper channelDisplayUserSortMapper;

    public ResultVO updateAndListChannelDisplay(String specialTokenId, ListChannelDisplayModel listChannelDisplayModel) {
        /**
         * 1.判断channeldisplays不为空判断上次更新时间
         *    1.不存在 则新增
         *    2.存在 lastUpdateTime<mtime 返回最近排序列表；否则继续修改操作操作
         * 2.判断channeldisplays为空，为空则返回最近一次修改的正确的列表
         */
        List list = new ArrayList();
        Integer uid = Integer.parseInt(specialTokenId);
        String channelDisplays = listChannelDisplayModel.getChannelDisplays();
        if (!"".equals(listChannelDisplayModel.getChannelDisplays())) {
            String[] channelDisplayArr = listChannelDisplayModel.getChannelDisplays().split(",");
            Boolean aBoolean = checkRepeat(channelDisplayArr);
            if (!aBoolean) {
                return ResultUtil.error("channelDisplays有重复值", ResultEnum.PARAMS_ERROR);
            }
            ChannelDisplayUserSort channelDisplayUserSort = getUserChannelDisplaySort(uid, channelDisplays);
        } else {
            String a;
        }
        return ResultUtil.success(list);
    }

    public ChannelDisplayUserSort getUserChannelDisplaySort(Integer uid, String channelDisplays) {
        return Optional.ofNullable(getChannelDisplayUserSort(uid)).orElse(saveChannelDisplayUserSort(uid, channelDisplays));
    }

    public ChannelDisplayUserSort saveChannelDisplayUserSort(Integer uid, String channelDisplays) {
        ChannelDisplayUserSort channelDisplayUserSort = new ChannelDisplayUserSort();
        channelDisplayUserSort.setUid(uid);
        channelDisplayUserSort.setChannelDisplays(channelDisplays);
        channelDisplayUserSort.setId(UUIDFactory.create());
        Integer now = DateUtil.getNowTimeBySecond();
        channelDisplayUserSort.setCtime(now);
        channelDisplayUserSort.setMtime(now);
        int insert = channelDisplayUserSortMapper.insertSelective(channelDisplayUserSort);
        if (insert == 1) {
            return channelDisplayUserSort;
        } else {
            return null;
        }
    }

    public ChannelDisplayUserSort getChannelDisplayUserSort(Integer uid) {
        ChannelDisplayUserSort channelDisplayUserSort = new ChannelDisplayUserSort();
        Object redisResult = RedisHandler.get(USER_CHANNELDISPLAY_SORT + SYMBOL_COLON + uid);
        if (redisResult == null) {
            channelDisplayUserSort.setUid(uid);
            ChannelDisplayUserSort mysqlResult = channelDisplayUserSortMapper.selectOne(channelDisplayUserSort);
            if (mysqlResult == null) {
                return null;
            } else {
                RedisHandler.set(USER_CHANNELDISPLAY_SORT + SYMBOL_COLON + uid, JSON.toJSONString(mysqlResult));
                return mysqlResult;
            }
        } else {
            channelDisplayUserSort = JSON.parseObject(redisResult.toString(), ChannelDisplayUserSort.class);
        }
        return channelDisplayUserSort;
    }


    /**
     * 判断数组是否有重复值
     *
     * @param array
     * @return
     */
    public Boolean checkRepeat(Object[] array) {
        Set set = new HashSet<>();
        for (Object o : array) {
            set.add(o);
        }
        if (set.size() != array.length) {
            return false;
        } else {
            return true;
        }
    }


}
