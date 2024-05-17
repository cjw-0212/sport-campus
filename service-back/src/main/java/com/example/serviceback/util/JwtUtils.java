package com.example.serviceback.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CJW
 * @since 2024/3/22
 */
public class JwtUtils {
    /**
     * jwt的有效时间，单位为毫秒
     */
    private static final Long JWT_TTL = 60 * 60 * 1000L;
    /**
     * jwt加密密钥
     */
    private static final String JWT_SECRET_KEY = "chenjiaweichenjiawei";

    /**
     * 生成指定的过期时间和携带数据的JWT
     *
     * @param subject   携带的数据，可以是json字符串
     * @param ttlMillis 过期时间,不填会使用默认时间
     * @return 携带指定数据的jwt
     */
    public static String createJwt( Map<String, Object> claims) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        long expireMillis = nowMillis + JWT_TTL;
        Date expireDate = new Date(expireMillis);
        return Jwts.builder()
                .signWith(signatureAlgorithm, JWT_SECRET_KEY)
                .setExpiration(expireDate)
                .setClaims(claims)
                .compact();
    }
    /**
     * @return 解析jwt出来的数据
     */
    public static Claims parseJwt(String jwt) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET_KEY)
                .parseClaimsJws(jwt)
                .getBody();
    }
}

