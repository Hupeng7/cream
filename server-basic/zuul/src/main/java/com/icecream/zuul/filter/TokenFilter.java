package com.icecream.zuul.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icecream.common.model.model.TokenInfo;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.util.constant.SysConstants;
import com.icecream.common.util.json.JsonUtil;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.zuul.feign.UserTokenFeignClient;
import com.icecream.zuul.jwt.TokenParser;
import com.icecream.zuul.redis.RedisHandler;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.icecream.common.util.constant.SysConstants.USER_HASH_PREFIX;
import static com.icecream.common.util.constant.SysConstants.USER_STAR_HASH_PREFIX;
import static com.netflix.zuul.context.RequestContext.getCurrentContext;
import static java.time.OffsetDateTime.now;

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

    private static final String[] whiteList = {"login", "sendauthcode", "register", "loginByOldOpenid", "loginByOldOpenid", "superLogin"};

    @Autowired
    private UserTokenFeignClient userTokenFeignClient;

    @Autowired
    private TokenParser tokenParser;

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
        if (!checkToken(ctx, token)) setBadAuthResponse(ctx);
        return null;
    }

    private boolean checkToken(RequestContext ctx, String token) {
        //token为空直接返回
        boolean flag = false;
        if (token == null) return false;

        //解析token
        TokenInfo tokenInfo = tokenParser.parseToken(token);
        Integer uid = tokenInfo.getUid();

        if (uid > 0) {
            try {
                Object mapField = RedisHandler.getMapField(USER_HASH_PREFIX, uid.toString());
                User user = mapField != null ? JSON.parseObject(mapField.toString(), User.class)
                                              : userTokenFeignClient.checkConsumerByMysql(uid);
                if (user != null) {
                    flag = true;
                    setSuccessResponse(ctx, user.getId());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else if (uid < 0) {
            try {
                Object mapField = RedisHandler.getMapField(USER_STAR_HASH_PREFIX, uid.toString());
                UserStar star = mapField != null ? JSON.parseObject(mapField.toString(), UserStar.class)
                                                   : userTokenFeignClient.checkStarByMysql(uid);
                if (star != null) {
                    flag = true;
                    setSuccessResponse(ctx, star.getId());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return flag;
    }


    /**
     * 设置认证成功的路由跳转
     *
     * @param ctx
     * @param id
     * @throws IOException
     */
    private void setSuccessResponse(RequestContext ctx, Integer id) throws IOException {
        InputStream in = ctx.getRequest().getInputStream();
        Map<String, List<String>> requestQueryParams = ctx.getRequestQueryParams();
        requestQueryParams.remove("access_token");
        List<String> requestList = new ArrayList<>();
        requestList.add(id.toString());
        requestQueryParams.put("specialTokenId", requestList);
        String body = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
        log.info(body);
        JSONObject json = JSONObject.parseObject(body);
        if (json == null || json.isEmpty()) {
            json = new JSONObject();
        }
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
    private void setBadAuthResponse(RequestContext context) {
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(401);
        HttpServletResponse response = context.getResponse();
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        context.setResponse(response);
        context.setResponseBody(JsonUtil.toJSONString(ResultUtil.error(null, ResultEnum.NOT_AUTH)));
    }
}
