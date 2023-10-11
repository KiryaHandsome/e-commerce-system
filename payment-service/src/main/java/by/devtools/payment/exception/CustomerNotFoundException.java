package by.devtools.payment.exception;

import by.devtools.domain.BaseException;

public class CustomerNotFoundException extends BaseException {

    public CustomerNotFoundException(String message) {
        super(404, message);
    }
}
