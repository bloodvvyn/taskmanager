package ru.zaikin.taskmanager.taskmanager.util;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTCore {

    private Key key;

    public JWTCore(@Value("${testing.app.secret}") String token) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(token));
    }

    /*Метод генерации JWT токена, указываем имя, время выдачи, дату окончания и подписываем токен секретным ключом*/

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userDetails.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /*Метод извлекает имя пользователя из токена
    * Принимает токен, */

    public String getNameFromJwt(String jwt) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}
