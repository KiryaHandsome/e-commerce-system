package by.devtools.domain;


public class BaseException extends RuntimeException {

    private final int statusCode;

    public BaseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
