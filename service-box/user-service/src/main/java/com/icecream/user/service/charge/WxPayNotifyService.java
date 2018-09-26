package com.icecream.user.service.charge;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.pojo.WechatpayNotifyRecord;
import com.icecream.user.feignclients.OrderFeignClient;
import com.icecream.user.utils.charge.PayCommonUtil;
import com.icecream.user.utils.charge.StringUtil;
import com.icecream.user.utils.time.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/8 0008
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class WxPayNotifyService {


    @Autowired
    private OrderFeignClient orderFeignClient;

    public synchronized String wxNotify(HttpServletRequest request, HttpServletResponse response) {
        log.info("微信回调开始...");
        String xml = "";
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        Map<String, String> params = getMapFromWxNotify(request);
        xml= !PayCommonUtil.isTenpaySign(params) ? signError() : signSuccess(params);
        try {
            writer = response.getWriter();
            writer.print(xml);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("进入了流处理方法");
        }finally {
            if(writer!=null){
                writer.close();
            }
        }
        return null;
    }

    //认证签名成功
    private String signSuccess(Map<String, String> params) {
        Map<String, String> return_data = new LinkedHashMap<>();
        log.info("===============收到微信通知==============");
        //获取交易状态码
        String result_code = params.get("result_code");
        wxNotifyNormalRecord(params);
        return_data.put("return_code", "SUCCESS");
        return_data.put("return_msg", "OK");
        return StringUtil.GetMapToXML(return_data);
    }

    //认证签名失败
    private String signError() {
        Map<String, String> return_data = new LinkedHashMap<String, String>();
        log.error("非法的支付签名");
        return_data.put("return_code", "FAIL");
        return_data.put("return_msg", "return_code不正确");
        return StringUtil.GetMapToXML(return_data);
    }

    //从请求中获取xml文本并转化为map
    private Map<String, String> getMapFromWxNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        InputStream inStream = null;
        ByteArrayOutputStream outSteam = null;
        try {
            inStream = request.getInputStream();
            outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            String resultxml = new String(outSteam.toByteArray(), "utf-8");
            params = PayCommonUtil.doXMLParse(resultxml);
        } catch (IOException io) {
            io.printStackTrace();
            log.error("微信回调从请求流中获取数据失败");
        } catch (JDOMException xml) {
            xml.printStackTrace();
            log.error("微信回请求流中数据xml转map失败");
        } finally {
            if (outSteam != null & outSteam != null) {
                try {
                    inStream.close();
                    outSteam.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            log.info("请求流正常关闭.");
        }
        return params;
    }

    //微信支付收到交易成功状态信息后执行
    private void wxNotifyNormalRecord(Map<String, String> return_data) {
        WechatpayNotifyRecord wechatpayNotifyRecord = JSON.parseObject(JSON.toJSONString(return_data),
                WechatpayNotifyRecord.class);
        wechatpayNotifyRecord.setUid(Integer.parseInt(wechatpayNotifyRecord.getAttach()));
        wechatpayNotifyRecord.setCtime(DateUtil.getNowTimeBySecond());
        Integer result = orderFeignClient.insertWxChargeRecord(wechatpayNotifyRecord);
        log.info("" + result);
    }
}

