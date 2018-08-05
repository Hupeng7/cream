package com.icecream.user.service.monitoring;


import com.icecream.common.model.requstbody.BaseTimeSection;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserMapper;
import com.icecream.user.utils.time.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @version 2.0
 */
@Service
@SuppressWarnings("all")
public class MonitoringService {

    @Autowired
    private UserMapper userMapper;

    public ResultVO getTheNumberOfUsersInWeek(Integer year, Integer week) {
        Date startTime = DateUtil.getFirstDayOfWeek(year, week);
        Date endTime = DateUtil.getLastDayOfWeek(year, week);
        BaseTimeSection timeSection = DateUtil.getTimeSection(startTime.getTime(), endTime.getTime());
        Integer count = userMapper.getUserCountInWeek(timeSection.getStartTime(), timeSection.getEndTime());
        return ResultUtil.success(count);
    }

    public ResultVO GetTheTotalNumberOfUsers() {
        int count = userMapper.selectCount(null);
        return ResultUtil.success(count);
    }
}
