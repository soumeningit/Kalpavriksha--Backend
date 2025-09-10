package com.soumen.kalpavriksha.Auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService
{
    // private final String secret = "secretsecretsecretsecretsecretsecret";
    private final String secret = "jnd-*/+w859dkbkjshksv%$@!(){}}[wjsu]5856sdfnklj";

    private final SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    private String generateRefreshToken(Object payload, long REFRESH_TOKEN_EXPIRATION_TIME)
    {
        try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.convertValue(payload, Map.class);
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createRefreshToken(Object payload, long REFRESH_TOKEN_EXPIRATION_TIME)
    {
        return generateRefreshToken(payload, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private String generateToken(Object payload, long EXPIRATION_TIME)
    {
        try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.convertValue(payload, Map.class);
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String generateAccessToken(Object payload, long EXPIRATION_TIME)
    {
        return generateToken(payload, EXPIRATION_TIME);
    }

    public String getUserIdFromRefreshToken(String token)
    {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return String.valueOf(claims.get("userId"));
    }

    public String getUserIdFromToken(String token)
    {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return String.valueOf(claims.get("userId"));
    }

    private boolean validateToken(String token)
    {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValid(String token)
    {
        return validateToken(token);
    }

    private boolean validateToken(String token, String userId)
    {
        String userIdFromToken = getUserIdFromToken(token);
        System.out.println("userIdFromToken : " + userIdFromToken + " userId : " + userId);
        if(userIdFromToken.equals(userId))
        {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public boolean checkIsTokenValid(String token, String userId)
    {
        return validateToken(token, userId);
    }


    private Object getPayload(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Object getPayloadFromToken(String token)
    {
        return getPayload(token);
    }
}
