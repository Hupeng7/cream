package com.icecream.user.service.code.chuanglan;

import com.icecream.common.model.requstbody.SmsLoginOrRegisterParams;
import com.icecream.common.model.requstbody.SmsOpenApiResponse;
import com.icecream.common.model.requstbody.SmsSendEntity;
import com.icecream.common.redis.RedisHandler;
import com.icecream.common.util.constant.SysConstants;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.user.service.code.CodeHandler;
import com.icecream.user.utils.random.RandomCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


/**
 * @author Mr_h
 * @version 2.0
 * description: 验证码发送
 * create by Mr_h on 2018/6/19 0019
 */
@Slf4j
@Component
public class ChuanglanSender implements CodeHandler {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Override
    public String send(String itucode, String phone) {
        String url = environment.getProperty(SysConstants.SMS_CHUANGLAN_URL);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        Integer authCode = RandomCreator.createAuthCode();
        String msg="【雪糕群】您的验证码为"+authCode+",10分钟内有效,不要告诉其他人哦";
        SmsSendEntity sendEntity = new SmsSendEntity();
        sendEntity.setAccount(environment.getProperty(SysConstants.SMS_CHUANGLAN_ACCOUNT));
        sendEntity.setPassword(environment.getProperty(SysConstants.SMS_CHUANGLAN_PASSWORD));
        sendEntity.setPhone(phone);
        sendEntity.setMsg(msg);
        String params = JsonUtil.toJSONString(sendEntity);
        HttpEntity<String> formEntity = new HttpEntity<>(params, headers);
        SmsOpenApiResponse response = restTemplate.postForObject(url, formEntity, SmsOpenApiResponse.class);
        if("0".equals(response.getCode())){
            //向redis中存验证码，过期时间为10分钟
            RedisHandler.set(itucode+phone,authCode.toString(),
                    Long.valueOf(environment.getProperty(SysConstants.SMS_CHUANGLAN_CODE_TIMEOUT)));
            return response.getCode();
        }
        return "";
    }

    @Override
    public Boolean check(String key,Integer code) {
        int mirror = Integer.parseInt(RedisHandler.get(key).toString());
        if(mirror==code)
            return true;
        return false;
    }

    @Override
    public Boolean check(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        String key = smsLoginOrRegisterParams.getItucode() + smsLoginOrRegisterParams.getPhone();
        return check(key, smsLoginOrRegisterParams.getCode());
    }
}
