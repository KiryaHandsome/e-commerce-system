package by.devtools.order.service.impl;

import by.devtools.order.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Slf4j
@Service
public class JwtService implements TokenService {

    @Value("${secret.key}")
    private final String secretKey;

    @Value("${secret.issuer}")
    private final String issuer;

    @Value("${secret.expiration}")
    private final long expirationTime;

    public JwtService(@Value("${secret.key}") String secretKey,
                      @Value("${secret.issuer}") String issuer,
                      @Value("${secret.expiration}") long expirationTime) {
        this.secretKey = secretKey;
        this.issuer = issuer;
        this.expirationTime = expirationTime;
    }

    @Override
    public String generateToken(String username) {
        long nowMilliseconds = System.currentTimeMillis();
        return Jwts.builder()
                .setIssuedAt(new Date(nowMilliseconds))
                .setExpiration(new Date(nowMilliseconds + expirationTime))
                .setIssuer(issuer)
                .setSubject(username)
                .signWith(getSignInKey())
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        Claims claims;
        try {
            claims = extractClaims(token);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
        Date expirationDate = claims.getExpiration();
        Date now = new Date(System.currentTimeMillis());
        return expirationDate.after(now);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    protected Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    protected Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
