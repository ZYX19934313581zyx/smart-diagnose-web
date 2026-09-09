package com.cnu.zyx.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    //密钥，长度不能太短
    private static final String SECRET = "abcdefghijklmnopabcdefghijklmnop123456";
    //token有效期：2小时（毫秒）
    private static final long EXPIRE_TIME = 2 * 60 * 60 * 1000L;

    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    //生成token，存入用户id
    public static String createToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expireDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //解析token，获取用户id
    public static Long getUserId(String token){
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
        return Long.valueOf(jws.getBody().get("userId").toString());
    }

    //校验token是否合法有效
    public static boolean verifyToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}