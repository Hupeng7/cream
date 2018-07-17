package com.icecream.user.utils.jwt;

import com.icecream.common.model.pojo.User;
import com.icecream.common.model.pojo.UserStar;
import com.icecream.common.model.requstbody.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;

import javax.crypto.spec.SecretKeySpec;
import javax.naming.ldap.PagedResultsControl;
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
    public static TokenInfo parseJWT(String jsonWebToken, String secret) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                    .parseClaimsJws(jsonWebToken).getBody();
            if (claims.get("uid") != null) {
                TokenInfo tokenInfo = new TokenInfo();
                tokenInfo.setIsToken(1);
                tokenInfo.setUid(Integer.parseInt(claims.get("uid").toString()));
                return tokenInfo;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    //客户端的创建token方式
    public static String createJWT(long TTLMillis, String secret, User user) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "jwt")
                .claim("uid", user.getId())
                .signWith(signatureAlgorithm, signingKey);
        //添加Token过期时间
        if (TTLMillis >= 0) {
            long expMillis = nowMillis + TTLMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        //生成JWT
        return "consumer" + builder.compact();
    }

    //版主端的创建token方式
    public static String createJWTForStar(long TTLMillis, String secret, UserStar star) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "jwt")
                .claim("uid", star.getId())
                .signWith(signatureAlgorithm, signingKey);
        //添加Token过期时间
        if (TTLMillis >= 0) {
            long expMillis = nowMillis + TTLMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        //生成JWT
        return "star" + builder.compact();
    }
}  