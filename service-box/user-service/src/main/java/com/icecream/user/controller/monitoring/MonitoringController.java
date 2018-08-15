package com.icecream.user.controller.monitoring;


import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.UserService;
import com.icecream.user.service.monitoring.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务监控接口
 * @author mr_h
 * @version 2.0
 */
@RestController
@RequestMapping("user/star")
public class MonitoringController {


    @Autowired
    private MonitoringService monitoringService;

    /**
     * 获取某年某周用户增长的数量
     * @param year x年
     * @param week 第x周
     * @return count (Integer)
     */
    @GetMapping("monitor/week")
    public ResultVO getTheNumberOfUsersInWeek(@Param("year") Integer year, @Param("weekOfYear") Integer week) {
        return monitoringService.getTheNumberOfUsersInWeek(year, week);
    }


    /**
     * 获取全部用户的总数
     *
     * @return count (Integer)
     */
    @GetMapping("monitor/all")
    public ResultVO getTheTotalNumberOfUsers() {
        return monitoringService.GetTheTotalNumberOfUsers();
    }
}