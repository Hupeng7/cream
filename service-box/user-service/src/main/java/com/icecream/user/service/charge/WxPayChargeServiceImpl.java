package com.icecream.user.service.charge;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.util.idbuilder.staticfactroy.SnowflakeGlobalIdFactory;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.config.charge.WxPayConfig;
import com.icecream.user.rabbitmq.RabbitSender;
import com.icecream.user.utils.charge.PayCommonUtil;
import com.icecream.user.utils.charge.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.*;

import static com.icecream.user.config.charge.WxPayConfig.*;

/**
 * @author Mr_h
 * @version 1.0
 * description: 微信充值
 * create by Mr_h on 2018/7/27 0027
 */
@Slf4j
@Service
public class WxPayChargeServiceImpl implements ChargeService {


    @Autowired
    private SnowflakeGlobalIdFactory snowflakeGlobalIdFactory;

    @Autowired
    private RabbitSender rabbitSender;


    @Value("${rsa.public-key}")
    private String public_key;

    @Value("${rsa.private-key}")
    private String private_key;


    @Override
    public ResultVO charge(String uid, BigDecimal price) {
        log.info("开始微信支付。用户id------>{},充值金额------{}", uid, price);
        String trade_no = LocalDate.now().toString().replace("-","")+String.valueOf(snowflakeGlobalIdFactory.create().nextId());
        //第一次请求，获取perOrderId
        Map<String, String> map = weixinPrePay(trade_no, price, uid);
        if(map.isEmpty()){return ResultUtil.error(null,ResultEnum.PRE_ORDER_ERROR);}
        //准备返回给前端的请求数据
        SortedMap<String, Object> prePay = buildCallRemoteInterfaceParams(map);
        //向队列里放入订单对象json串
        rabbitSender.send(JSON.toJSONString(buildPreOrder(uid,trade_no,price)));
        try {
            //使用rsa加密
            String byte2Base64 = getDecodeStr(prePay);
            return ResultUtil.success(Optional.ofNullable(byte2Base64).orElse(""));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("RSA加密失败", ResultEnum.DATA_ERROR);
        }
    }

    private String getDecodeStr(SortedMap<String, Object> prePay) throws Exception {
        String json = JSON.toJSONString(prePay);
        //将Base64编码后的公钥转换成PublicKey对象
        PublicKey publicKey = RSAUtil.string2PublicKey(public_key);
        //用公钥加密
        byte[] publicEncrypt = RSAUtil.publicEncrypt(json.getBytes(), publicKey);
        //加密后的内容Base64编码
        String byte2Base64 = RSAUtil.byte2Base64(publicEncrypt);
        PrivateKey privateKey = RSAUtil.string2PrivateKey(private_key);
        //加密后的内容Base64解码
        byte[] base642Byte = RSAUtil.base642Byte(byte2Base64);
        //用私钥解密
        byte[] privateDecrypt = RSAUtil.privateDecrypt(base642Byte, privateKey);
        return byte2Base64;
    }

    private SortedMap<String, Object> buildCallRemoteInterfaceParams(Map<String, String> map) {
        SortedMap<String, Object> finalpackage = new TreeMap<String, Object>();
        //应用ID
        finalpackage.put("appid", AppId);
        //商户号
        finalpackage.put("partnerid", MchId);
        Long time = (System.currentTimeMillis() / 1000);
        //时间戳
        finalpackage.put("timestamp", time.toString());
        //随机字符串
        finalpackage.put("noncestr", map.get("nonce_str"));
        //预支付交易会话ID
        finalpackage.put("prepayid", map.get("prepay_id"));
        //扩展字段
        finalpackage.put("package", "Sign=WXPay");
        SortedMap<String, Object> prePay = new TreeMap<String, Object>();
        prePay.put("appid", AppId);
        prePay.put("partnerid", MchId);
        prePay.put("prepayid", map.get("prepay_id"));
        prePay.put("sign", map.get("sign"));
        prePay.put("package", "Sign=WXPay");
        prePay.put("noncestr", getRandomString(32));
        prePay.put("timestamp", System.currentTimeMillis() / 1000);
        return prePay;
    }

    public Map<String, String> weixinPrePay(String trade_no, BigDecimal totalAmount, String uid) {
        SortedMap<String, Object> parameterMap = getStringObjectSortedMap(trade_no, totalAmount, uid);
        String requestXML = PayCommonUtil.getRequestXml(parameterMap);
        String result = PayCommonUtil.httpsRequest(PrepayUrl, "POST", requestXML);
        Map<String, String> map = null;
        try {
            map = PayCommonUtil.doXMLParse(result);
        } catch (JDOMException e) {
            e.printStackTrace();
            log.error("xml解析失败");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("io异常");
        }
        return map;
    }

    private SortedMap<String, Object> getStringObjectSortedMap(String trade_no, BigDecimal totalAmount, String uid) {
        SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();
        parameterMap.put("appid", WxPayConfig.AppId);  //应用appid
        parameterMap.put("mch_id", MchId);  //商户号
        parameterMap.put("nonce_str", getRandomString(32));
        parameterMap.put("body", "雪糕群星星");
        parameterMap.put("out_trade_no", trade_no);
        parameterMap.put("fee_type", "CNY");
        parameterMap.put("total_fee", totalAmount.toString());
        parameterMap.put("spbill_create_ip", PayCommonUtil.getRemoteHost());
        parameterMap.put("notify_url", WxPayConfig.notify_url);
        parameterMap.put("trade_type", "APP");
        parameterMap.put("attach", uid);
        String sign = PayCommonUtil.createSign("UTF-8", parameterMap);
        parameterMap.put("sign", sign);
        return parameterMap;
    }

    //构建预下单对象
    private Order buildPreOrder(String uid,String order_no,BigDecimal price) {
        Order order = new Order();
        order.setUid(Integer.parseInt(uid));
        order.setOrderNo(order_no);
        order.setPayPrice(price);
        return order;
    }


    //随机字符串生成
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}

