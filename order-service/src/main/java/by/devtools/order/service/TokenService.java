package by.devtools.order.service;

public interface TokenService {

    String generateToken(String username);

    boolean validateToken(String token);

    String extractUsername(String token);
}
