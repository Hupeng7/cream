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
import com.icecream.common.model.pojo.ScoreRule;
import com.icecream.common.util.idbuilder.staticfactroy.SnowflakeGlobalIdFactory;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.common.util.time.DateUtil;
import com.icecream.user.feignclients.OrderFeignClient;
import com.icecream.user.rabbitmq.sender.Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.icecream.common.util.constant.SysConstants.CHARGE_QUEUE;
import static com.icecream.common.util.constant.SysConstants.ORDER_EXCHANGE;

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
    private Sender rabbitSender;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Override
    public ResultVO charge(@Param("specialTokenId")String uid, BigDecimal price) {
        String trade_no = LocalDate.now().toString().replace("-", "") + String.valueOf(snowflakeGlobalIdFactory.create().nextId());
        log.info("收到金额数据------>{},准备请求支付宝接口", price);
        rabbitSender.send(ORDER_EXCHANGE,CHARGE_QUEUE,JSON.toJSONString(buildPreOrder(uid, trade_no, price)));
        String result = callAliPayOpenApi(uid,price,trade_no);
        if(StringUtils.areNotEmpty(result)){
            return ResultUtil.success(result);
        }
        return ResultUtil.error("支付宝预下单请求失败",ResultEnum.PARAMS_ERROR);
    }

    @Override
    public Order buildPreOrder(String uid, String orderNo, BigDecimal price) {
        ScoreRule rule = orderFeignClient.getRuleForCreateOrder(6, price, 1);
        Order order = new Order();
        order.setSid(1);//粉丝端
        order.setPayPrice(price);
        order.setUid(Integer.parseInt(uid));
        order.setOrderNo(orderNo);
        order.setAccount("-1");
        order.setPaymentType(1); //支付类型支付宝
        order.setStatus(1); //状态为正常
        order.setOrderStatus(0);//订单状态为待付款
        order.setIsDigital(1);//虚拟商品
        order.setIsPay(0);//未支付
        order.setPayTime(0);//支付时间(预下单时还没有支付)
        order.setChangeTime(0);//变动时间
        order.setOrderType(1);//平台交易
        order.setReportType(1);//充值账单
        order.setAmount(new BigDecimal(1));
        order.setChangePrice(rule.getPrice());
        order.setGoodsId(rule.getCode());
        order.setGoodsPrice(rule.getPrice());
        order.setCtime(DateUtil.getNowSecondIntTime());
        return order;
    }

    public String callAliPayOpenApi(String uid,BigDecimal price,String tradeNo) {
        AlipayClient alipayClient = new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                "2018092661539460",
                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCp7Qi4SticzTLzykoQCbeMh+gKQd6u2NpIzlFE056A5E+tpaS5HLHrwoX+Miur3P6bRkk6sGGTE+hVWUZRVQMQPkzVXXq2ck/7OO5AHsqPVRIpMso7RmpAgtE6djPmmMY2o+IIOReQjq4pQK4yfb7ct9aT1HnDhC+YBdEXqEWXWvvlJNvfMtju+u6kmzU3qv4WUiDZRjKoU4RwT06UuAXzpd5jElZAFxk7SKYvQXFBYmbeACrtPVa4s3Ir/puBKT39J++ZK4swk+gExFBrY80iiobWklEZB1U5nNm000uM5tLTCT1QLQBnL6olB6sMD0IdfTcPHSD9kDQnwGEnDEG7AgMBAAECggEAUSxtbPQ8M/OGcpVg+fZaW2SVAUlkIixvYjZE9lcNMc9fDLnDTbaae+BJ+BtpZ0lDy1MxKjsV8Zeh9huWjsXIl9rbA1YLh9plwMH6dmD/LKdb7JhZqSVDHpwfbQkIVBEJJxL0qC71xdWS9xvx2ybcpT2g4K+wlyivP7UPpHh6/gjuHIXaca5fEq+mL5na5DaGDa/89Z1a5yx+SSXrbRPI3Iq6nxwz+nP7H5puPx0phA6lAEqiPNTew2UZHxgrbpe5bR0mqkPyxvxftyQXhfmb7QcKe3RwUt/9p1RnzcKxaKFibGnZa9hlq7ZlNrjMjrLgchfEd+Sin5so41rBkKn6+QKBgQDb/SjNxqCKUY3hO4MfubHWQlF+nj+d/wfRNVGHFqAzYYjBaQwZBbbIEWjAUnFkj/zFNdIGRhvrxLPYTP8rjNWvr0K21ujhW+rKX54t91FCYrEol0+zl12A93qCM9OISnQaFxh3C0so1VdwZK+C1md1kxsVxZRKkpvj8lSq6OpotwKBgQDFvfA07LHdTfrNnnlH62uPGl2vA7zkjA8zyQq+UWEIUOm6IAybv/DqeJD7abNVrgjvgNn43FqzmrbHpyujaixa0prR+kbPD786pSBCDJLwe8ZvHMUZS/yHISOdyNrwn5Y54GIxJ3yv82m+35KSZr0sNnO0x7TCubOSQDN4dh3DHQKBgHSV2gYT+gjT58kx7O/nTaQrSV09KKHnApGRHD/nccdJLVyy+0JXkOK+tzEVgBq3ZFJvj3rbtPhiKp27UJAX9zdAPq7/fjPQrsHJot7hbyMrgo/sgMTAt0Ed5sMSDEzyiE07aC/OwGpHhit+cLV6QyJAb94987UBtbQ8PTrGbVRJAoGAShLatpisEECz4O6qc/yGcDbqPTNjQSIOV9HJyn1lod3dkDGNR5LhRpQfi26PFVt6UW2tOYIiIAGm0qeWu0J8lxEpmdrtR4eYlPliWhunt5pGPT1DwDQsxtntI6AoE9dUSR366nrmRGskg7HwkBMYbkV/lorw9bjmwbR3E3r7rykCgYB2BBAVKUyel3Ial56dMNiB/DbEJ1FwOQwixCWPMkxgZYHhb7kryXNyriaiL/ucrliM3e8J9EdT3vcBNfBm2noW2t7Pz2FHS6ylWwylBBfyBADj0BIwWPReHsSIfGTO3QwOrMfqb8wY9ygnu/ve+vkZIsJHrg+4igyIIw5GJ1ofEg==",
                "json",
                "UTF-8",
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjhRu4mw0PkAGvsZQ8lOuDMSb6nBou6LX3Sw0U0WwOTBzLzda4HmqBR4bT6sGxfIOq3hWEF9pQb8d/QcLfoMYsT6GmG7Rpv0Nt1uOQ0hx2r8vt5tyhSJ3XKgXNUYyCMv3cj14kYeBeMmt5gaF16DYEO17gnzT1FwJ9bqwksFsvXWhpDTQKKHwkDqmjjbAtatBEWmbAcR/URHo+0zhx76PdHYBLoNPh2yPw2kF7l06D+CyGlDLIrrGdok5XkgVgmDdsMalNFUIm2I5FHazGXyKy++2FGheG+X8PoTGJyxSnHw001sereW4m32cBC58eCiwQwJdxHpxyAVhWqmqlRMDXwIDAQAB",
                "RSA2");
       //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setSubject("雪糕群星星");
        model.setOutTradeNo(tradeNo);
        model.setTimeoutExpress("30m");
        model.setTotalAmount(price.toString());
        model.setProductCode("QUICK_MSECURITY_PAY");
        model.setBody(uid);
        request.setBizModel(model);
        request.setNotifyUrl("http://eeezne.natappfree.cc/notify/ali");
        try {
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            return response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "";
    }

}
