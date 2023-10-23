package by.devtools.order.service.impl;

import by.devtools.order.dto.LoginRequest;
import by.devtools.order.dto.TokenResponse;
import by.devtools.order.exception.InvalidPasswordException;
import by.devtools.order.exception.UserNotFoundException;
import by.devtools.order.model.User;
import by.devtools.order.repository.UserRepository;
import by.devtools.order.service.AuthService;
import by.devtools.order.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(
                        () -> new UserNotFoundException("user with such username not found. username = " + request.username())
                );
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidPasswordException("Provided password is not valid.");
        }

        String token = tokenService.generateToken(user.getUsername());
        return new TokenResponse(token);
    }
}