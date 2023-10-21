package by.devtools.order.exception;

import by.devtools.domain.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException(String message) {
        super(HttpStatus.UNAUTHORIZED.value(), message);
    }
}
