package com.icecream.order.service;

import com.icecream.common.model.pojo.CheckInfo;
import com.icecream.common.model.pojo.ScoreRule;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.time.DateUtil;
import com.icecream.order.mapper.CheckInfoMapper;
import com.icecream.order.mapper.PointInoutMapper;
import com.icecream.order.mapper.ScoreRuleMapper;
import com.icecream.order.mapper.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/6 0006
 */
@Service
@SuppressWarnings("all")
public class CheckInfoService {

    @Autowired
    private CheckInfoMapper checkInfoMapper;

    @Autowired
    private PointInoutMapper pointInoutMapper;

    @Autowired
    private WalletService walletService;

    @Autowired
    private ScoreRuleMapper scoreRuleMapper;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private ScoreRuleService scoreRuleService;

    @Autowired
    private PointInoutService pointInoutService;

    //签到操作涉及表check_info,wallet,pointInput等表，事务操作(默认隔离级别，默认事务策略)
    @Transactional(rollbackFor = Exception.class)
    public ResultVO signIn(Integer uid) {
        if (isHasBeenSignIn(uid))
            return ResultUtil.error("您已经签到", ResultEnum.SIGN_IN_REPETITION);
        BigDecimal stars = checkInfoHandler(uid);
        int row1 = pointInoutService.insertPointInout(stars, uid);
        int row2 = walletService.insertOrUpateHandler(uid, stars);
        if(row1>0&row2>0) return ResultUtil.success();
        return ResultUtil.error("签到失败", ResultEnum.MYSQL_OPERATION_FAILED);
    }


    //获取用户之前的签到信息
    private BigDecimal checkInfoHandler(Integer uid) {
        List<CheckInfo> infos = getUserLastSignInRecord(uid);
        Integer hit = timeHits(1, 1, infos);
        Map<String, BigDecimal> ruleMapping = scoreRuleService.getRuleMapping();
        List<ScoreRule> rule = scoreRuleService.getRule();
        Integer count = insertCheckInfo(hit, infos, uid);
        return (count > 0) ? getBigDecimal(hit, ruleMapping) : new BigDecimal(0);
    }

    //用户在某个时间段(3天)内有多少次击中签到叠加奖励(连续签到附加奖励)
    private Integer timeHits(Integer hit, Integer days, List<CheckInfo> infos) {
        if (infos.size() <= 0) { return hit; }
        LocalDate localDate = LocalDate.now().minusDays(days);
        int startTime = (int) DateUtil.caseLocalDateTimeSecond(
                LocalDateTime.of(localDate, LocalTime.MIN));
        int endTime = (int) DateUtil.caseLocalDateTimeSecond(
                LocalDateTime.of(localDate, LocalTime.MAX));
        List<CheckInfo> list = infos.stream()
                .filter(i -> i.getCtime() >= startTime & i.getCtime() < endTime)
                .collect(Collectors.toList());
        if (list.size() > 0) { hit++; } else { return hit; }
        if (days >= 3) { return hit; }
        return timeHits(hit, ++days, infos);
    }

    //根据规则将签到命中数换算成应该赠送的星星
    private BigDecimal getBigDecimal(Integer hit, Map<String, BigDecimal> mapping) {
        switch (hit.toString()) {
            case "1":
                String frist = "第一天签到";
                return mapping.get(frist);
            case "2":
                String second = "第二天签到";
                return mapping.get(second);
            case "3":
                String third = "第三天签到";
                return mapping.get(third);
            default:
                String mostOfTheKing = "三天以上签到";
                return mapping.get(mostOfTheKing);
        }
    }

    //判断是否已经在今日签到
    private boolean isHasBeenSignIn(Integer uid) {
        LocalDate localDate = LocalDate.now();
        int format = Integer.parseInt(localDate.toString().replace("-", ""));
        CheckInfo checkInfo = new CheckInfo();
        checkInfo.setUid(uid);
        List<CheckInfo> select = checkInfoMapper.select(checkInfo);
        return select.stream().anyMatch(s -> s.getCheckTime() == format);
    }

    //根据uid和时间阈值查询用户在当前时间前的所有签到信息
    private List<CheckInfo> getUserLastSignInRecord(Integer uid) {
        int wall = Integer.parseInt(DateUtil.getNowSecond());
        CheckInfo checkInfo = new CheckInfo();
        Example example = new Example(CheckInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uid", uid);
        criteria.andLessThan("ctime", wall);
        example.setOrderByClause("ctime desc");
        List<CheckInfo> infos = checkInfoMapper.select(checkInfo);
        return infos;
    }

    //checkInfo表插入并且组装数据
    private Integer insertCheckInfo(Integer hit, List<CheckInfo> infos, Integer uid) {
        CheckInfo result = new CheckInfo();
        result.setCheckTime(Integer.parseInt(LocalDate.now().toString().replace("-", "")));
        result.setCtime(Integer.parseInt(DateUtil.getNowSecond()));
        result.setConNum(hit);
        result.setTotalNum(1 + infos.size());
        result.setSid(1);
        result.setUid(uid);
        return checkInfoMapper.insertSelective(result);
    }

}
