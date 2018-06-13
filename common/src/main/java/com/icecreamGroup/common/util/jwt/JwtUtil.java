package com.icecreamGroup.common.util.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr_h
 * @version 1.0
 * description: jwt工具类
 * create by Mr_h on 2018/6/12 0012
 * 目前逻辑 版主与用户分为两张表，且无字段区分
 * 粉丝下载的是粉丝端APP,版主下载的是版主APP
 * 无权限逻辑，只需要验证token即可
 */
public class JwtUtil {

    private static final String STAR_SECRET ="star";
    private static final String CUSTOMER_SECRET= "customer";

    //生成用户token
    public static String generateTokenForCustomer(Integer uid){
        Map<String, Object> map = new HashMap<>();
        map.put("uid",uid);
        String jwt = Jwts.builder().setClaims(map).setExpiration(new Date(System.currentTimeMillis() + 3600000000L))
                .signWith(SignatureAlgorithm.HS512, STAR_SECRET).compact();
        return "Bearer"+jwt;
    }

    //生成版主token
    public static String generateTokenForStar(Integer uid,String username){
        Map<String, Object> map = new HashMap<>();
        map.put("uid",uid);
        String jwt = Jwts.builder().setClaims(map).setExpiration(new Date(System.currentTimeMillis() + 3600000000L))
                .signWith(SignatureAlgorithm.HS512, STAR_SECRET).compact();
        return "Bearer"+jwt;
    }

    //验证用户token
    public static Boolean validateTokenForCustomer(String token) {
        try {
            // parse the token.
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(CUSTOMER_SECRET)
                    .parseClaimsJws(token.replace("Bearer ",""))
                    .getBody();
            if(body.get("uid")!=null||body.get("username")!=null){
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            throw new IllegalStateException("Invalid Token. "+e.getMessage());
        }
    }

    //验证版主token
    public static Boolean validateTokenForStar(String token) {
        try {
            // parse the token.
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(STAR_SECRET)
                    .parseClaimsJws(token.replace("Bearer ",""))
                    .getBody();
            if(body.get("uid")!=null){
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            throw new IllegalStateException("Invalid Token. "+e.getMessage());
        }
    }
}
