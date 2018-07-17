package com.icecream.good.service;

import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.pojo.GoodStore;
import com.icecream.common.util.bean.BeanPropertyFactory;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.time.DateUtil;
import com.icecream.good.mapper.GoodMapper;
import com.icecream.good.mapper.GoodStoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * @author Mr_h
 * @version 1.0
 * description: 商品服务service
 * create by Mr_h on 2018/7/9 0009
 */
@Slf4j


@Service
@SuppressWarnings("all")
public class GoodService {

    @Autowired
    private GoodMapper goodMapper;
    
    @Autowired
    private GoodStoreMapper goodStoreMapper;

    public ResultVO findAll(){
        return null;
    }
}
