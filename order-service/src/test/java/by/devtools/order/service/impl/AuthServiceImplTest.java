package by.devtools.order.service.impl;

import by.devtools.order.dto.TokenResponse;
import by.devtools.order.exception.InvalidPasswordException;
import by.devtools.order.exception.UserNotFoundException;
import by.devtools.order.repository.UserRepository;
import by.devtools.order.service.TokenService;
import by.devtools.order.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthServiceImpl authService;


    @BeforeEach
    void setUp() {
    }

    @Test
    void check_AuthService_should_be_NotNull() {
        assertThat(authService).isNotNull();
    }

    @Test
    void check_login_should_return_CorrectToken() {
        var loginRequest = TestData.getLoginRequest();
        var user = TestData.getUser();

        doReturn(Optional.of(user))
                .when(userRepository)
                .findByUsername(loginRequest.username());
        doReturn(true)
                .when(passwordEncoder)
                .matches(loginRequest.password(), user.getPassword());
        doReturn(TestData.ACCESS_TOKEN)
                .when(tokenService)
                .generateToken(loginRequest.username());

        TokenResponse actual = authService.login(loginRequest);

        verify(userRepository).findByUsername(loginRequest.username());
        verify(passwordEncoder).matches(loginRequest.password(), user.getPassword());
        verify(tokenService).generateToken(loginRequest.username());

        assertThat(actual).isEqualTo(new TokenResponse(TestData.ACCESS_TOKEN));
    }

    @Test
    void check_login_should_throw_UserNotFoundException() {
        doReturn(Optional.empty())
                .when(userRepository)
                .findByUsername(TestData.USERNAME);

        assertThrows(UserNotFoundException.class,
                () -> authService.login(TestData.getLoginRequest()));

        verify(userRepository).findByUsername(TestData.USERNAME);
    }

    @Test
    void check_login_should_throw_InvalidPasswordException() {
        var loginRequest = TestData.getLoginRequest();
        var user = TestData.getUser();

        doReturn(Optional.of(user))
                .when(userRepository)
                .findByUsername(TestData.USERNAME);
        doReturn(false)
                .when(passwordEncoder)
                .matches(loginRequest.password(), user.getPassword());

        assertThrows(InvalidPasswordException.class,
                () -> authService.login(loginRequest));

        verify(userRepository).findByUsername(TestData.USERNAME);
        verify(passwordEncoder).matches(loginRequest.password(), user.getPassword());
    }
}