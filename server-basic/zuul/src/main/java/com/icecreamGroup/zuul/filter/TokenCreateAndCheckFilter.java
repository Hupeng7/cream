package com.icecreamGroup.zuul.filter;

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

/**
 * @author Mr_h
 * @version 1.0
 * description:token的创建与验证的过滤器
 * create by Mr_h on 2018/6/12 0012
 */
@Slf4j
@Component
@ResponseBody
public class TokenCreateAndCheckFilter extends ZuulFilter{

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
        RequestContext ctx = RequestContext.getCurrentContext();
        String url = ctx.getRequest().getRequestURL().toString();
        return !url.contains("login");
    }

    /**
     * @return
     * 过滤器核心逻辑
     * 除了login* 拦截所有请求
     * 验证是否有token或者token是否正确或者token是否将要过期
     * 验证失败，返回401
     * 验证成功，返回200
     */
    @Override
    public ResultVO run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info(String.format("%s request to %s",request.getMethod(), request.getRequestURL().toString()));
        String token = request.getParameter("token");
        if(token==null){
            //如果token没有,不允许访问api
            log.error("token is null ...");
            setResponse(ctx);
        }else {
            //用户有token，则验证token，是否存在这个token或者是否将要过期
            log.info("token:"+token);
            Boolean flag;
            if(token.startsWith("customer")) {
                flag = JwtHelper.parseJWT(token.replace("customer",""), "customer");
            }else {
                flag = JwtHelper.parseJWT(token.replace("star",""), "star");
            }
            log.info("parseJwt result--{}",flag);
            if(flag){
                return ResultUtil.success(ResultEnum.SUCCESS);
            }else{
                setResponse(ctx);
            }
        }
        return null;
    }


    //设置过滤器返回内容
    public void setResponse(RequestContext context){
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(401);
        context.setResponseBody(JsonUtil.toJSONString(ResultUtil.error(401,"Unauthorized access")));
    }

}
