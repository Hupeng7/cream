package com.icecreamGroup.common.util.jwt;

import java.security.Key;
import java.util.Date;  
  
import javax.crypto.spec.SecretKeySpec;  
import javax.xml.bind.DatatypeConverter;

import com.icecreamGroup.common.model.User;
import com.icecreamGroup.common.model.UserNameAndPasswordLogin;
import com.icecreamGroup.common.model.UserStar;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;  
import io.jsonwebtoken.Jwts;  
import io.jsonwebtoken.SignatureAlgorithm;

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

    //客户端的解密方式
    public static Boolean parseJWT(String jsonWebToken, String base64Security) throws NullPointerException{
        try  
        {  
            Claims claims = Jwts.parser()  
                       .setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))  
                       .parseClaimsJws(jsonWebToken).getBody();
            if(claims.get("uid")!=null||claims.get("name")!=null){
                return true;
            }else {
                return false;
            }
        }
        catch(Exception ex)  
        {  
            return null;  
        }  
    }

    //客户端的创建token方式
    public static String createJWT(long TTLMillis, String secret,User user)
    {  
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;  
           
        long nowMillis = System.currentTimeMillis();  
        Date now = new Date(nowMillis);  
           
        //生成签名密钥  
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());  
           
          //添加构成JWT的参数  
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                                        .claim("name", user.getNickname())
                                        .claim("uid", user.getId())
                                        .setIssuer(user.getNickname())
                                        .setAudience(user.getId().toString())
                                        .signWith(signatureAlgorithm, signingKey);  
         //添加Token过期时间  
        if (TTLMillis >= 0) {  
            long expMillis = nowMillis + TTLMillis;  
            Date exp = new Date(expMillis);  
            builder.setExpiration(exp).setNotBefore(now);  
        }  
           
         //生成JWT  
        return "customer"+builder.compact();
    }

    //版主端的创建token方式
    public static String createJWTForStar(long TTLMillis, String secret,UserStar star)
    {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                .claim("name", star.getName())
                .claim("uid", star.getUid())
                .setIssuer(star.getName())
                .setAudience(star.getPassword())
                .signWith(signatureAlgorithm, signingKey);
        //添加Token过期时间
        if (TTLMillis >= 0) {
            long expMillis = nowMillis + TTLMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        //生成JWT
        return "star"+builder.compact();
    }
}  