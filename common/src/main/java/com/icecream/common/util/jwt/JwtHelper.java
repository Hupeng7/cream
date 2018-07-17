package com.icecream.common.util.jwt;

import com.icecream.common.model.requstbody.TokenInfo;
import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserStar;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * @author Mr_h
 * @version 1.0
 * description: jwt工具类
 * create by Mr_h on 2018/6/12 0012
 * 目前逻辑 版主与用户分为两张表，且无字段区分
 * 粉丝下载的是粉丝端APP,版主下载的是版主APP
 * 无权限逻辑，只需要验证token即可
 */
public class JwtHelper {



    //解密
    public static TokenInfo parseJWT(String jsonWebToken, String[] cell){
        for(String c:cell) {
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(DatatypeConverter.parseBase64Binary(c))
                        .parseClaimsJws(jsonWebToken).getBody();
                if (claims.get("id") != null) {
                    TokenInfo tokenInfo = new TokenInfo();
                    tokenInfo.setIsToken(1);
                    tokenInfo.setId(Integer.parseInt(claims.get("id").toString()));
                    tokenInfo.setRole(Integer.parseInt(claims.get("role").toString()));
                    return tokenInfo;
                } else {
                    continue;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }


    //客户端的创建token方式
    public static String createJWT(long TTLMillis, String secret, User user,Integer type) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "jwt")
                .claim("type",type)
                .claim("id", user.getId())
                .signWith(signatureAlgorithm, signingKey);
        //添加Token过期时间
        if (TTLMillis >= 0) {
            long expMillis = nowMillis + TTLMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        //生成JWT
        return "customer" + builder.compact();
    }

    //版主端的创建token方式
    public static String createJWTForStar(long TTLMillis, String secret, UserStar star,Integer role) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "jwt")
                .claim("id", star.getId())
                .claim("role",role)
                .signWith(signatureAlgorithm, signingKey);
        //添加Token过期时间
        if (TTLMillis >= 0) {
            long expMillis = nowMillis + TTLMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        //生成JWT
        return builder.compact();
    }
}  