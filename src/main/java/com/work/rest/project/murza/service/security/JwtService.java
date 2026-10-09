package com.work.rest.project.murza.service.security;

import io.jsonwebtoken.Claims;
import com.work.rest.project.murza.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecretKey;


    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;


    @Value("${jwt.refresh-expiration}")
    private int refreshExpirationInMs;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpirationInMs, "access");
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration, String purpose
    ) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("tokenUse", purpose);
        claims.put("credentialsVersion", userDetails instanceof User user ? user.getCredentialsVersion() : 0);
        return Jwts
                .builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return validFor(token, userDetails, "access");
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpirationInMs, "refresh");
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        return validFor(token, userDetails, "refresh");
    }

    private boolean validFor(String token, UserDetails userDetails, String purpose) {
        Claims claims = extractAllClaims(token);
        Number version = claims.get("credentialsVersion", Number.class);
        int tokenVersion = version == null ? 0 : version.intValue();
        int currentVersion = userDetails instanceof User user ? user.getCredentialsVersion() : 0;
        String use = claims.get("tokenUse", String.class);
        return userDetails.getUsername().equals(claims.getSubject()) && !claims.getExpiration().before(new Date())
                && tokenVersion == currentVersion && (use == null || purpose.equals(use));
    }

}
