package by.devtools.order.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BaseException {

    public OrderNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}