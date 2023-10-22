package by.devtools.order.service.impl;

import by.devtools.order.util.TestData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final String secretKey = "1123456789023456789dfhsaogufioadsfljhsdfjkasdlkfhjdfosdhflkasjdfhahfpuh14iuh1p23121424124";
    private final String issuer = "devtools";
    private final long expirationTime = 1800000;

    private JwtService tokenService;

    @BeforeAll
    static void beforeAll() {

    }

    @BeforeEach
    void setUp() {
        tokenService = new JwtService(secretKey, issuer, expirationTime);
    }

    @Test
    void check_generateToken_should_return_correctToken() {
        long deltaMilliseconds = 2000;

        String actual = tokenService.generateToken(TestData.USERNAME);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(tokenService.getSignInKey())  // Set your secret key
                .build()
                .parseClaimsJws(actual)
                .getBody();

        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(TestData.USERNAME);
        assertThat(claims.getIssuer()).isEqualTo(issuer);
        assertThat(claims.getExpiration()).isCloseTo(new Date(System.currentTimeMillis() + expirationTime), deltaMilliseconds);
        assertThat(claims.getIssuedAt()).isCloseTo(new Date(System.currentTimeMillis()), deltaMilliseconds);
    }

    @Test
    void check_validateToken_should_return_true() {
        String token = tokenService.generateToken(TestData.USERNAME);

        boolean actual = tokenService.validateToken(token);

        assertThat(actual).isTrue();
    }

    @Test
    void check_validateToken_should_return_false() {
        boolean actual = tokenService.validateToken(TestData.EXPIRED_TOKEN);

        assertThat(actual).isFalse();
    }

    @Test
    void check_extractUsername_should_return_expectedUsername() {
        String token = tokenService.generateToken(TestData.USERNAME);

        String actual = tokenService.extractUsername(token);

        assertThat(actual).isEqualTo(TestData.USERNAME);
    }
}