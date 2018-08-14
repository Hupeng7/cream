package com.icecream.user.service.charge;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.StringUtils;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.util.idbuilder.staticfactroy.SnowflakeGlobalIdFactory;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.rabbitmq.RabbitSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;

/**
 * @author Mr_h
 * @version 1.0
 * description: 支付宝充值接口实现
 * create by Mr_h on 2018/7/27 0027
 */
@Slf4j
@Service
public class AilPayChargeServiceImpl implements ChargeService {


    @Autowired
    private SnowflakeGlobalIdFactory snowflakeGlobalIdFactory;

    @Autowired
    private RabbitSender rabbitSender;

    @Override
    public ResultVO charge(@Param("specialTokenId")String uid, BigDecimal price) {
        String trade_no = LocalDate.now().toString().replace("-", "") + String.valueOf(snowflakeGlobalIdFactory.create().nextId());
        log.info("收到金额数据------>{},准备请求支付宝接口", price);
        String result = callAliPayOpenApi(uid,price);
        if(StringUtils.areNotEmpty(result)){
            return ResultUtil.success(result);
        }
        rabbitSender.send(JSON.toJSONString(buildPreOrder(uid, trade_no, price)));
        return ResultUtil.error("支付宝预下单请求失败",ResultEnum.PARAMS_ERROR);
    }

    @Override
    public Order buildPreOrder(String uid, String orderNo, BigDecimal price) {
        Order order = new Order();
        order.setUid(Integer.parseInt(uid));
        order.setOrderNo(orderNo);
        order.setPayPrice(price);
        order.setPaymentType(1); //支付类型 支付宝
        order.setStatus(1); //状态为正常
        order.setOrderStatus(0);//订单状态为待付款
        order.setIsDigital(1);//虚拟商品
        order.setIsPay(0);//未支付
        return order;
    }

    public String callAliPayOpenApi(String uid,BigDecimal price) {
        AlipayClient alipayClient = new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                "2017062207542763",
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCQYhvEsWao4JwaqAs4p+GBP5/u4qEpmHNMOnqA4euWyGRJTcwTualURgacoLQ+0CQ7EsERJjANG04qLvPWG/mTJN5SPlmPWOBDAGsJlUWo44xug4H+pvi8x6kiwId3RZv5sPaRdrMJIEV48JiMYfU71wQEMPLf/xUdR6r9SkJ7ZTiebwfqpmoEOXZ5Us9ZYisGgZ+L9HZk+GnR4qbp3oavgVRXg5HT52WUhVsDEUs1kVxYLppAAgpZd44x+793LMLCoNCrll+8fF+/1pBdAsfPEwQqAPfTKUq7ReybsMH1lEKu5ljj/W7VFH901ZLQ97+k+7A1QM5+hPxMOsymKccVAgMBAAECggEATBgo4mVM4bxfCX1/TIDOTLwnolM29SEvwwEfB0YKUBGVKT0jTVIQeCXf3jSkSmaQccHUlyE1OfMmIv1T7YgY5QANW/MYaIBC0Y0q3IYbjINAxA3zOS7u8S+ZaINn4eiB5/roOIoCmdC0TKUhAPuvr7YGm97gYkWh7yiSaaP1nepUQgohcb0QxKiyaRYrrx8tYzsP9rIJLhqMm3ZRKuA7Wx2IbPoK5R9aVB8z4i/jhAJbZgKfFHHxHHchJEzzgxz5lYfty07pE1sVi4IJw86nEe3/H924fnBFCEqUpc+YeD7f/z2SRVaq9yugyDIytVZJlaTicAJ4GXU5uP5YTQdXgQKBgQDHHL7X0ArGQX6LjO19Mtct+yRYdxpJ2S0yVrUoOaA2z+JmNkhjiyCdm5eO8GqjDxzbLqnVjBW0x15BvDYOtDlTiQpEcPzPU8D1f8wtD3BripW9QAdlZzMBlsKo3u1ketyOyJs365u1UpwJwtiC5rJ/VIhMW78S5aqu5konxoMinQKBgQC5om4jgUksY9HmnIXlojzdiqoRnYJU3VqoTUrew8zHJbqYE4Qc40dkmamUcfeUdytezIBujmH9pm9Kxj9r69SJj/e0jd9Zi7bQKBbM67v2kyJm3AFHB11mskHElFK4WRLGnuRzZXmWit76izfUiTqs4Zzdg294NeHHhsnEe1Qw2QKBgQC5+//HVmy6AzNQ5rJu365fJNcuSxIjKNkuzA8rI6ijikrPbqTvVmWA0nUe7zKsXNF3an75GYCs/AzvGf7kfTOO89LDW0bJ4lG6/0SYUnOQAEMeI1DFR0A9m7T4SEM2OA0M0hUqhslK9X8LHxVeMF9K0Ir/yDMSU7S66iEaRjL3gQKBgHbPUACnaYbgqGIZwdT0HlKIwkqd7eGU/sYDGi0zUigPrLpSm1bF3Fa1xoR84MGD+B0nc/fOZ0cps8c+1S6kdJZKr3Y+6zlro2jcj6M+KUIqb3U30BV+0De/VTqU19CnKc43ue1lgAlq/kWKvwPnhMdLatOXoMtmaQgD67U2Xe8ZAoGBALhnXooU2SVaaxlAKw4X/JcweVfq/sSfblSgBUYRAWoBqZXCIz0fOzzkkThqG45BK+17gnrWWZOf5bIrM9E3qgm1xXD3xJ0QTQYYz4zKZRqFHUD0lrQ2bqYKYcrWYpaw0E/pwyX1v+GjdaZLloBCwcXUkJM6J6dzoMmZj0K15VBH",
                "json",
                "UTF-8",
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAupYE6SccqNg+/goE/hBc47Tbn06XmxLs2g/rAJOHMwhCi/tNbS4AtrkRMS7is0tSxxnU7iXqufAvmbNeUPOawbGQlPzSslMGh5I6Rvj1BiC0CRv3+9u+7n+Ig4Bi85j3f15k6VlHt+tzHVTqOmETLFdRvAwSSzlAsL28oL6Qy1sv/ivrJFlNRRXOlb5vEmmmDJhA7O59MLMbgA+YSn1P1+2lNhaQAkoQH+KA5tlmmQcVZXra2wsUc+0EgIXnOTMBgAXvAz0RUEzsR0Jsl3ryEhKugaKgpzaxnIy8RysPSxNh25A9vMSAd4olZZhrt9Jo/MpLD8enthRnsDCVl2sRlwIDAQAB",
                "RSA2");
       //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setSubject("雪糕群");
        model.setOutTradeNo(String.valueOf(snowflakeGlobalIdFactory.create().nextId()));
        model.setTimeoutExpress("30m");
        model.setTotalAmount(price.toString());
        model.setProductCode("QUICK_MSECURITY_PAY");
        model.setBody(uid);
        request.setBizModel(model);
        request.setNotifyUrl("http://icecream.natapp1.cc/notify/ali");
        try {
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            return response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "";
    }

}
