package com.icecream.order.controller;

import com.icecream.common.model.pojo.AlipayNotifyRecord;
import com.icecream.common.model.pojo.AlipayNotifyRecordErrorLog;
import com.icecream.common.model.pojo.WechatpayNotifyRecord;
import com.icecream.common.util.res.ResultVO;
import com.icecream.order.service.ChargeMealService;
import com.icecream.order.service.ChargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/3 0003
 */
@RestController
@RequestMapping("charge")
public class ChargeController {


    @Autowired
    private ChargeMealService chargeMealService;

    @Autowired
    private ChargeRecordService chargeRecordService;

    /**
     * 支付宝回调处理充值订单接口
     * @param alipayNotifyRecord
     * @see AlipayNotifyRecord
     * @return 是否插入成功
     */
    @PostMapping("insertAliChargeRecord")
    public String insertAliChargeRecord(@Validated @RequestBody AlipayNotifyRecord alipayNotifyRecord){
        return chargeRecordService.insert(alipayNotifyRecord);
    }

    /**
     * 支付宝回调处理充值订单失败后接口
     * @param alipayNotifyRecordErrorLog
     * @see AlipayNotifyRecordErrorLog
     * @return 是否插入成功
     */
    @PostMapping("insertAliChargeErrorRecord")
    public String insertAliChargeErrorRecord(@RequestBody AlipayNotifyRecordErrorLog alipayNotifyRecordErrorLog){
        return chargeRecordService.insert(alipayNotifyRecordErrorLog);
    }

    /**
     * 微信回调处理订单接口
     * @param WechatpayNotifyRecord
     * @see WechatpayNotifyRecord
     */
    @PostMapping("insertWxChargeRecord")
    public Integer insertWxChargeRecord(@RequestBody WechatpayNotifyRecord WechatpayNotifyRecord){
        return chargeRecordService.insert(WechatpayNotifyRecord);
    }

    /**
     * 用户获取充值星星的套餐列表
     * @return resultVo
     */
    @GetMapping("meal/get")
    public ResultVO getMeal(){
        return chargeMealService.getMeal();
    }
}
