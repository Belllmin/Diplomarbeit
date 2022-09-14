package com.htlleonding.ac.at.backend.util;

import java.util.*;

import com.htlleonding.ac.at.backend.security_service.JwtUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

@Component
public class JwtUtils {

    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${backend.app.jwtSecret}")
    private String jwtSecret;

    @Value("${backend.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${backend.app.jwtRefreshExpirationDateInMs}")
    private int jwtRefreshExpirationDateInMs;
    //endregion

    //region Helper methods
    public String doGenerateJwtToken(Map<String, Object> claims, JwtUser userPrincipal) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }
    //endregion

    //region Main methods
    public String generateJwtToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
        if (roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) claims.put("isAdmin", true);
        if (roles.contains(new SimpleGrantedAuthority("ROLE_MODERATOR"))) claims.put("isModerator", true);
        if (roles.contains(new SimpleGrantedAuthority("ROLE_USER"))) claims.put("isUser", true);
        JwtUser userPrincipal = (JwtUser) authentication.getPrincipal();
        return doGenerateJwtToken(claims, userPrincipal);
    }

    public String doGenerateRefreshJwtToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpirationDateInMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
    //endregion
}