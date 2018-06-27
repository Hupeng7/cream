package com.icecream.user.sms;

import com.icecreamGroup.common.model.SmsOpenApiResponse;
import com.icecreamGroup.common.model.SmsSendEntity;
import com.icecreamGroup.common.util.constant.ConstantVal;
import com.icecreamGroup.common.util.json.JsonUtil;
import com.icecreamGroup.common.util.redis.RedisHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;


/**
 * @author Mr_h
 * @version 1.0
 * description: 验证码发送
 * create by Mr_h on 2018/6/19 0019
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class AuthCodeHandler {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private RedisHandler redisHandler;

    //短信验证码发送
    public String smsSend(String itucode,String phone){
        String url = environment.getProperty(ConstantVal.SMS_CHUANGLAN_URL);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        Integer authCode = RandomCreator.createAuthCode();
        String msg="【雪糕群】您的验证码为"+authCode+",10分钟内有效,不要告诉其他人哦";
        SmsSendEntity sendEntity = new SmsSendEntity();
        sendEntity.setAccount(environment.getProperty(ConstantVal.SMS_CHUANGLAN_ACCOUNT));
        sendEntity.setPassword(environment.getProperty(ConstantVal.SMS_CHUANGLAN_PASSWORD));
        sendEntity.setPhone(phone);
        sendEntity.setMsg(msg);
        String params = JsonUtil.toJSONString(sendEntity);
        HttpEntity<String> formEntity = new HttpEntity<String>(params, headers);
        SmsOpenApiResponse response = restTemplate.postForObject(url, formEntity, SmsOpenApiResponse.class);
        if("0".equals(response.getCode())){
            //向redis中存验证码，过期时间为10分钟
                redisHandler.set(itucode+phone,authCode.toString(),
                        Long.valueOf(environment.getProperty(ConstantVal.SMS_CHUANGLAN_CODE_TIMEOUT)));
                return response.getCode();
        }
        return "";
    }

    //短信验证码和redis中的做对比
    public Boolean checkCode(String key,Integer code){
        int mirror = Integer.parseInt(redisHandler.get(key).toString());
        if(mirror==code)
            return true;
            return false;
    }

}
