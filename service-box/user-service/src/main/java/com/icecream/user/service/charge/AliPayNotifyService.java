package com.icecream.user.service.charge;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.icecream.common.model.pojo.AlipayNotifyRecord;
import com.icecream.common.model.pojo.AlipayNotifyRecordErrorLog;
import com.icecream.common.util.time.DateUtil;
import com.icecream.user.config.charge.AlipayConfig;
import com.icecream.user.feignclients.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/2 0002
 */

@Slf4j
@Service
public class AliPayNotifyService {

    @Autowired
    private OrderFeignClient orderFeignClient;

    public String aliNotify(HttpServletRequest request){
        log.debug("支付宝回调");
        // 获取支付宝POST过来反馈信息
        Map requestParams = request.getParameterMap();
        log.debug("支付宝回调结果：" + requestParams.toString());
        Map<String, String> params = new HashMap<>();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        Map<String,String> paramsbak = new HashMap<>();
        paramsbak.putAll(params);
        try {
            //验证签名
            boolean flag = AlipaySignature.rsaCheckV1(params, AlipayConfig.ali_public_key, "utf-8", "RSA2");
            if (flag) { // 签名验证成功
                if ("TRADE_SUCCESS".equals(paramsbak.get("trade_status"))) {
                    //付款金额
                    log.info("支付宝回调参数------>{}",paramsbak);
                    String body = paramsbak.get("body");
                    Integer uid = Integer.parseInt(body);
                    String notify_time =paramsbak.get("notify_time");
                    String gmt_create =paramsbak.get("gmt_create");
                    String gmt_payment =paramsbak.get("gmt_payment");
                    paramsbak.put("notify_time", DateUtil.getStringSecond(notify_time));
                    paramsbak.put("gmt_create",DateUtil.getStringSecond(gmt_create));
                    paramsbak.put("gmt_payment",DateUtil.getStringSecond(gmt_payment));
                    paramsbak.put("ctime",DateUtil.getNowSecond());
                    paramsbak.put("uid",uid.toString());
                    String json = JSON.toJSONString(paramsbak);
                    //附加数据
                    AlipayNotifyRecord alipayNotifyRecord = JSON.parseObject(json, AlipayNotifyRecord.class);
                    String result = orderFeignClient.insertAliChargeRecord(alipayNotifyRecord);
                    log.info("支付信息录入状态----->{}",result);
                    return "SUCCESS";
                }else {
                    String notify_time = paramsbak.get("notify_time");
                    paramsbak.put("notify_time",DateUtil.getStringSecond(notify_time));
                    paramsbak.put("ctime",DateUtil.getNowSecond());
                    String json = JSON.toJSONString(paramsbak);
                    AlipayNotifyRecordErrorLog alipayNotifyRecordErrorLog = JSON.parseObject(json, AlipayNotifyRecordErrorLog.class);
                    String result = orderFeignClient.insertAliChargeErrorRecord(alipayNotifyRecordErrorLog);
                    log.error("支付错误信息录入------>",result);
                    return "SUCCESS";
                }
            } else {
                log.debug("签名验证失败！");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return "SUCCESS";
        }
        return "";
    }
}
