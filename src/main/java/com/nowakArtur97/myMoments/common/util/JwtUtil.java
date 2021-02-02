package com.nowakArtur97.myMoments.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${my-moments.jwt.validity:36000000}")
    private long jwtTokenValidity;

    @Value("${my-moments.jwt.secretKey:secret}")
    private String secretKey;

    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        return createToken(userDetails.getUsername(), claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetail) {

        return (extractUserName(token).equals(userDetail.getUsername()) && !isTokenExpired(token));
    }

    public String extractUserName(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpirationDate(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        Claims claim = extractAllClaims(token);

        return claimsResolver.apply(claim);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private String createToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private boolean isTokenExpired(String token) {

        return extractExpirationDate(token).before(new Date(System.currentTimeMillis()));
    }
}
