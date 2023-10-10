package by.devtools.order.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {

    private HttpStatus statusCode;

    public BaseException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
