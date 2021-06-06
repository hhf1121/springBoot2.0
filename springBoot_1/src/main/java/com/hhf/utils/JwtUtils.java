package com.hhf.utils;

import com.alibaba.fastjson.util.TypeUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public abstract class JwtUtils {

    public static final String UID = "userId";
    private static final String SECRET = "mySecret";
    private static final long EXPIRE = 60L * 1000L;



    /**
     * 生成token
     *
     * @param userId
     * @return
     */
    public static String generateById(Integer userId) {
        Date nowDate = new Date();
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + EXPIRE * 60 * 24 * 15);
        Map<String, Object> claims = new HashMap<>(1);
        claims.put(UID, userId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 解析Claims
     *
     * @param token
     * @return
     */
    public static Claims getClaim(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            //解析失败
            log.info("无效的token.{}",e.getMessage());
        }
        return claims;
    }

    /**
     * 获取jwt发布时间
     */
    public static Date getIssuedAt(String token) {
        Claims claims = getClaim(token);
        if(claims==null){
            return null;
        }
        return claims.getIssuedAt();
    }


    public static Integer getUserId(String token) {
        Claims claims = getClaim(token);
        if(claims==null){
            return null;
        }
        return TypeUtils.castToInt(claims.get(UID));
    }

    /**
     * 获取jwt失效时间
     */
    public static Date getExpiration(String token) {
        Claims claims = getClaim(token);
        if(claims==null){
            return null;
        }
        return claims.getExpiration();
    }

}
