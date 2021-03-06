package com.icecream.order.service;

import com.icecream.common.model.pojo.PointInout;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.time.DateUtil;
import com.icecream.common.util.uuid.UUIDFactory;
import com.icecream.order.mapper.PointInoutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.icecream.order.contants.Contants.ADD;
import static com.icecream.order.contants.Contants.REDUCE;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/7 0007
 */
@Service
@SuppressWarnings("all")
public class PointInoutService {

    @Autowired
    private PointInoutMapper pointInoutMapper;


    public ResultVO getPointListAndSort(Integer count, Integer lastTime, Integer sort,Integer uid){
        Example example = new Example(PointInout.class);
        example.setOrderByClause(sort==-1?"ctime desc limit "+count:" ctime asc limit "+count);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uid",uid);
        criteria.andGreaterThan("ctime",lastTime);
        List<PointInout> pointInouts = pointInoutMapper.selectByExample(example);
        return ResultUtil.success(Optional.ofNullable(pointInouts).orElse(null));
    }

    //签到时插入流水表
    public int insertPointInout(BigDecimal stars,Integer uid){
        PointInout pointInout = new PointInout();
        pointInout.setPoint(stars.intValue());
        pointInout.setObjectType(4);
        pointInout.setIsInuse(1);
        pointInout.setUid(uid);
        pointInout.setIntout(ADD);
        pointInout.setPoint(stars.intValue());
        pointInout.setCtime(Integer.parseInt(DateUtil.getNowSecond()));
        pointInout.setObjectId(uid.toString());
        pointInout.setId(UUIDFactory.create());
        return pointInoutMapper.insertSelective(pointInout);
    }

    //购买商品时插入流水表
    public int insertPointInoutOrder(BigDecimal stars,Integer uid,String orderNo){
        PointInout pointInout = new PointInout();
        pointInout.setPoint(stars.intValue());
        pointInout.setObjectType(2);
        pointInout.setIsInuse(1);
        pointInout.setUid(uid);
        pointInout.setIntout(REDUCE);
        pointInout.setPoint(stars.intValue());
        pointInout.setCtime(Integer.parseInt(DateUtil.getNowSecond()));
        pointInout.setObjectId(orderNo);
        pointInout.setId(UUIDFactory.create());
        return pointInoutMapper.insertSelective(pointInout);
    }
}
