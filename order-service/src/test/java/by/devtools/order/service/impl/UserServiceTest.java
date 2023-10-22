package by.devtools.order.service.impl;

import by.devtools.order.exception.UserNotFoundException;
import by.devtools.order.repository.UserRepository;
import by.devtools.order.util.TestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void check_loadUserByUsername_should_throw_UserNotFoundException() {
        doReturn(Optional.empty())
                .when(userRepository)
                .findByUsername(TestData.USERNAME);

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.loadUserByUsername(TestData.USERNAME));

        verify(userRepository)
                .findByUsername(TestData.USERNAME);
    }

    @Test
    void check_loadUserByUsername_should_return_expectedUser() {
        var user = TestData.getUser();
        var expected = new User(user.getUsername(), user.getUsername(), Collections.emptyList());

        doReturn(Optional.of(user))
                .when(userRepository)
                .findByUsername(TestData.USERNAME);

        UserDetails actual = userService.loadUserByUsername(TestData.USERNAME);

        assertThat(actual).isEqualTo(expected);

        verify(userRepository).findByUsername(TestData.USERNAME);
    }
}