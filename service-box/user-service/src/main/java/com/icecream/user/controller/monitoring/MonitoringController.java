package com.icecream.user.controller.monitoring;


import com.icecream.common.model.eunm.OperatorRole;
import com.icecream.common.util.aspect.annotation.Operator;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.monitoring.MonitoringService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(description = "管理员统计")
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
    @Operator(role =OperatorRole.STAR)
    @ApiOperation(value = "【需要版主token】获取某年某周用户增长的数量")
    public ResultVO getTheNumberOfUsersInWeek(@Param("year") Integer year, @Param("weekOfYear") Integer week) {
        return monitoringService.getTheNumberOfUsersInWeek(year, week);
    }


    /**
     * 获取全部用户的总数
     *
     * @return count (Integer)
     */
    @GetMapping("monitor/all")
    @Operator(role = OperatorRole.STAR)
    @ApiOperation(value = "【需要版主token】获取全部用户数量")
    public ResultVO getTheTotalNumberOfUsers() {
        return monitoringService.GetTheTotalNumberOfUsers();
    }
}
