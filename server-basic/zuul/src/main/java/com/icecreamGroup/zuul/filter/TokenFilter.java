package com.icecreamGroup.zuul.filter;

import com.icecreamGroup.common.model.TokenInfo;
import com.icecreamGroup.common.util.json.JsonUtil;
import com.icecreamGroup.common.util.jwt.JwtHelper;
import com.icecreamGroup.common.util.res.ResultEnum;
import com.icecreamGroup.common.util.res.ResultUtil;
import com.icecreamGroup.common.util.res.ResultVO;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Mr_h
 * @version 1.0
 * description:token的创建与验证的过滤器
 * create by Mr_h on 2018/6/12 0012
 */
@Slf4j
@Component
@ResponseBody
public class TokenFilter extends ZuulFilter{

    private static final String[] whiteList = {"login","sendauthcode","register","loginByOldOpenid", "loginByOldOpenid"};

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
                .noneMatch(s -> RequestContext.getCurrentContext()
                        .getRequest().getRequestURL().toString().contains(s));
    }

    /**
     * @return
     * 过滤器核心逻辑
     * 过滤白名单外外所有请求
     * 验证是否有token或者token是否正确或者token是否将要过期
     * 验证失败，返回401
     * 验证成功，放行
     */
    @Override
    public ResultVO run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String token =  ctx.getRequest().getParameter("token");
        if(token==null){
            //如果token没有,不允许访问api
            log.error("token is null ...");
            setBadResponse(ctx);
        }else {
            log.info("token:"+token);
            parseJwt(ctx, token);
        }
        return null;
    }

    private void parseJwt(RequestContext ctx, String token) {

        //有token，则验证token
        TokenInfo tokenInfo;
        try {
            if (token.startsWith("customer")) {
                tokenInfo= JwtHelper.parseJWT(token.replace("customer", ""), "customer");
            } else {
                tokenInfo = JwtHelper.parseJWT(token.replace("star", ""), "star");
            }
            log.info("parseJwt result--{}", tokenInfo);
            if (tokenInfo!=null) {
                if(tokenInfo.getUid()!=null) {
                    setSuccessResponse(ctx);
                }
            } else {
                setBadResponse(ctx);
            }
        }catch (RuntimeException e){
            setBadResponse(ctx);
        }
    }

    private void setSuccessResponse(RequestContext ctx) {
        ctx.setSendZuulResponse(true);
        ctx.setResponseStatusCode(200);
        ctx.set("isSuccess",true);
    }

    //设置过滤器返回内容
    private void setBadResponse(RequestContext context){
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(401);
        context.setResponseBody(JsonUtil.toJSONString(ResultUtil.error(401,"Unauthorized access")));
    }

}
