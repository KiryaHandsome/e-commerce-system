package by.devtools.order.exception;

import by.devtools.domain.BaseException;

public class OrderNotFoundException extends BaseException {

    public OrderNotFoundException(String message) {
        super(404, message);
    }
}