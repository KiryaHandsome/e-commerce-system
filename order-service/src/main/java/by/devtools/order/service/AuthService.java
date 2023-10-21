package by.devtools.order.service;


import by.devtools.order.dto.LoginRequest;
import by.devtools.order.dto.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginRequest request);
}
