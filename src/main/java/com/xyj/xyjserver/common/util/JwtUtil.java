package com.xyj.xyjserver.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // 默认密钥（实际开发中建议放入配置文件，长度需要至少256位）
    private static final String SECRET_STRING = "XYJ_Server_Secret_Key_Must_Be_Long_Enough_For_HS256_XYJ_Server_Secret_Key_Must_Be_Long_Enough_For_HS256";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // 默认过期时间，2小时 (7200秒)
    public static final long EXPIRATION_TIME = 2 * 60 * 60 * 1000L;

    // Refresh Token 过期时间，7天
    private static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 生成 token
     */
    public static String generateToken(Long userId, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 生成 refresh_token
     */
    public static String generateRefreshToken(Long userId, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 解析 token
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 token 获取用户 ID
     */
    public static Long getUserId(String token) {
        try {
            Claims claims = parseToken(token);
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 校验 token 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}