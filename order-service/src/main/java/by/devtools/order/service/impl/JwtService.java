package by.devtools.order.service.impl;

import by.devtools.order.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService implements TokenService {

    @Value("${secret.key}")
    private String secretKey;

    @Value("${secret.issuer}")
    private String issuer;

    @Value("${secret.expiration}")
    private long expirationTime;

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
        Date expirationDate = extractClaims(token).getExpiration();
        Date now = new Date(System.currentTimeMillis());
        return expirationDate.after(now);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
