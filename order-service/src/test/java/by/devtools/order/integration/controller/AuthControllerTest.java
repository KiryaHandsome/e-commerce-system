package by.devtools.order.integration.controller;

import by.devtools.order.dto.LoginRequest;
import by.devtools.order.dto.TokenResponse;
import by.devtools.order.exception.InvalidPasswordException;
import by.devtools.order.integration.BaseIntegrationTest;
import by.devtools.order.service.AuthService;
import by.devtools.order.util.JsonUtil;
import by.devtools.order.util.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private static Stream<LoginRequest> invalidLoginRequests() {
        return Stream.of(
                new LoginRequest(null, "password"),
                new LoginRequest("username", null),
                new LoginRequest(null, null),
                new LoginRequest("username", ""),
                new LoginRequest("", "password"),
                new LoginRequest("", ""),
                new LoginRequest("username", "      "),
                new LoginRequest("              ", "password"),
                new LoginRequest("      ", "       ")
        );
    }

    @Test
    void check_login_should_return_accessToken() throws Exception {
        String url = "/api/v1/auth/login";
        var request = TestData.getLoginRequest();
        var tokenResponse = new TokenResponse(TestData.ACCESS_TOKEN);

        doReturn(tokenResponse)
                .when(authService)
                .login(request);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(tokenResponse.accessToken()));

        verify(authService).login(eq(request));
    }

    @Test
    void check_login_should_return_unauthorizedStatus() throws Exception {
        String url = "/api/v1/auth/login";
        var request = TestData.getLoginRequest();

        doThrow(new InvalidPasswordException("exception message"))
                .when(authService)
                .login(request);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(request)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(eq(request));
    }

    @ParameterizedTest
    @MethodSource("invalidLoginRequests")
    void check_login_withInvalidCredentials_should_return_badRequestStatus(LoginRequest request) throws Exception {
        String url = "/api/v1/auth/login";

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(request)))
                .andExpect(status().isBadRequest());
    }
}

