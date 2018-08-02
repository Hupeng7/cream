package com.icecream.order.mapper;

import com.icecream.common.model.pojo.WechatpayNotifyRecord;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/2 0002
 */
@org.apache.ibatis.annotations.Mapper
public interface WechatpayNotifyRecordMapper extends Mapper<WechatpayNotifyRecord>, MySqlMapper<WechatpayNotifyRecord> { }
