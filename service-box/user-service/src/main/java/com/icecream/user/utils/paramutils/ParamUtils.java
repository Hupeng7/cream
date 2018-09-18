package com.icecream.user.utils.paramutils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Mr_h
 * @version 1.0
 * description:参数处理工具类
 * create by Mr_h on 2018/9/17 0017
 */
@Slf4j
public class ParamUtils {


    public static JSONObject transitionParamsToJSONObject(){
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            streamReader.close();
            return JSONObject.parseObject(responseStrBuilder.toString());
        }catch (IOException io){
            log.error("从登陆请求中处理参数失败");
            io.printStackTrace();
            return null;
        }
    }
}
