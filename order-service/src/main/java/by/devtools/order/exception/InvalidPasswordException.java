package by.devtools.order.exception;

import by.devtools.domain.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends BaseException {

    public InvalidPasswordException(String message) {
        super(HttpStatus.UNAUTHORIZED.value(), message);
    }
}
