package by.devtools.order.exception;

import by.devtools.domain.BaseException;
import by.devtools.order.dto.ErrorEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorEntity> handleRuntime(RuntimeException ex) {
        ErrorEntity response = new ErrorEntity(ex.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorEntity> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ErrorEntity response = new ErrorEntity(ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorEntity> handleBaseException(BaseException ex) {
        ErrorEntity response = new ErrorEntity(ex.getMessage());
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(response);
    }

}
