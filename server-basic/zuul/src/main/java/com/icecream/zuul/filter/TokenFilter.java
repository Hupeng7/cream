package com.icecream.zuul.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.requstbody.TokenInfo;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.zuul.feign.UserTokenFeignClient;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.stream.Stream;

import static com.netflix.zuul.context.RequestContext.getCurrentContext;

/**
 * @author Mr_h
 * @version 1.0
 * description:token的创建与验证的过滤器
 * create by Mr_h on 2018/6/12 0012
 */
@Slf4j
@Component
@ResponseBody
public class TokenFilter extends ZuulFilter {

    private static final String[] whiteList = {"login", "sendauthcode", "register", "loginByOldOpenid", "loginByOldOpenid"};

    @Autowired
    private UserTokenFeignClient userTokenFeignClient;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return Stream.of(whiteList)
                .noneMatch(s -> getCurrentContext()
                        .getRequest().getRequestURL().toString().contains(s));
    }

    /**
     * @return 过滤器核心逻辑
     * 过滤白名单外外所有请求
     * 验证是否有token或者token是否正确或者token是否将要过期
     * 验证失败，返回401
     * 验证成功，放行
     */
    @Override
    public ResultVO run() {
        RequestContext ctx = getCurrentContext();
        String token = ctx.getRequest().getParameter("access_token");
        if (token == null) {
            //如果token没有,不允许访问api
            log.error("token is null ...");
            setBadResponse(ctx);
        } else {
            log.info("token:" + token);
            parseJwt(ctx, token);
        }
        return null;
    }

    private void parseJwt(RequestContext ctx, String token) {
        //有token，则验证token
        ResultVO resultVO = null;
        if (token.startsWith("star")) {
            resultVO = userTokenFeignClient.checkStar(token.replace("star", ""));
        } else if (token.startsWith("consumer")) {
            resultVO = userTokenFeignClient.checkConsumer(token.replace("consumer", ""));
        } else {
            setBadResponse(ctx);
        }
        if (resultVO != null) {
            localHandler(resultVO, ctx);
        }

    }


    private void localHandler(ResultVO resultVO, RequestContext ctx) {
        if (resultVO.getResult() != null) {
            log.info("result ={}", resultVO.getResult());
            String json = JSON.toJSONString(resultVO.getResult());
            TokenInfo tokenInfo = JSON.parseObject(json, TokenInfo.class);
            try {
                setSuccessResponse(ctx, tokenInfo.getUid());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            setBadResponse(ctx);
        }
    }

    private void setSuccessResponse(RequestContext ctx, Integer id) throws IOException {
        InputStream in = ctx.getRequest().getInputStream();
        String body = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
        System.out.println("body:" + body);
        JSONObject json = JSONObject.parseObject(body);
        if(json==null||json.isEmpty()){
            json = new JSONObject();
        }
        json.put("id", id);
        String newBody = json.toString();
        final byte[] reqBodyBytes = newBody.getBytes();
        ctx.setRequest(new HttpServletRequestWrapper(ctx.getRequest()) {
            @Override
            public ServletInputStream getInputStream() {
                return new ServletInputStreamWrapper(reqBodyBytes);
            }

            @Override
            public int getContentLength() {
                return reqBodyBytes.length;
            }

            @Override
            public long getContentLengthLong() {
                return reqBodyBytes.length;
            }
        });
        ctx.setSendZuulResponse(true);
        ctx.setResponseStatusCode(200);
        ctx.set("isSuccess", true);
    }

    //设置过滤器返回内容
    private void setBadResponse(RequestContext context) {
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(401);
        context.setResponseBody(JsonUtil.toJSONString(ResultUtil.error(401, "Unauthorized access")));
    }

}
