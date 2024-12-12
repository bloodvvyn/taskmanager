package ru.zaikin.taskmanager.taskmanager.util;


import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTCore {
    @Value("${testing.app.secret}")
    private String token;
    @Value("${testing.app.expriration}")
    private int lifetime;

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userDetails.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + lifetime))
                .compact();
    }

    public String getNameFromJwt(String jwt) {
        return Jwts.parser()
                .setSigningKey(token)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}
