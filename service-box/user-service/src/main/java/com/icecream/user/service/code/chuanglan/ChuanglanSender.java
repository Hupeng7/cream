package com.icecream.user.service.code.chuanglan;

import com.icecream.common.model.model.SmsLoginOrRegisterParams;
import com.icecream.common.model.model.SmsOpenApiResponse;
import com.icecream.common.model.model.SmsSendEntity;
import com.icecream.common.model.pojo.UserAuth;
import com.icecream.common.util.constant.SysConstants;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.mapper.UserAuthMapper;
import com.icecream.user.redis.RedisHandler;
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

import java.util.concurrent.TimeUnit;


/**
 * @author Mr_h
 * @version 2.0
 * description: 验证码发送
 * create by Mr_h on 2018/6/19 0019
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class ChuanglanSender implements CodeHandler {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private RedisHandler redisHandler;

    @Autowired
    private UserAuthMapper userAuthMapper;

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
            RedisHandler.setExpireTime(itucode+phone,600,TimeUnit.SECONDS);
            return response.getCode();
        }
        return "";
    }

    public ResultVO sendAndCheck(String itucode, String phone){
        return check(itucode,phone)?ResultUtil.success(send(itucode,phone)):
                ResultUtil.error(null,ResultEnum.EXIST_PHONE);
    }

    @Override
    public Boolean check(String key,Integer code) {
        int mirror =RedisHandler.get(key)!=null?Integer.parseInt(RedisHandler.get(key).toString()):0;
        if(mirror==code)
            return true;
        return false;
    }

    @Override
    public Boolean check(SmsLoginOrRegisterParams smsLoginOrRegisterParams) {
        String key = smsLoginOrRegisterParams.getItucode() + smsLoginOrRegisterParams.getPhone();
        return check(key, smsLoginOrRegisterParams.getCode());
    }

    @Override
    public Boolean check(String itucode, String phone) {
        UserAuth userAuth = new UserAuth();
        userAuth.setIdentityType(1);//手机号注册的类型为1
        userAuth.setIdentifier(itucode+phone);
        UserAuth result = userAuthMapper.selectOne(userAuth);
        return result==null?true:false;
    }
}
