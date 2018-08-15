package com.icecream.common.util.req;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/14 0014
 */
public class RequestHandler {

    public static String getParams(HttpServletRequest request,String key){
        try {
            ServletInputStream inputStream = request.getInputStream();
            String body = StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"));
            JSONObject jsonObject = JSON.parseObject(body);
            return jsonObject.getString(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
