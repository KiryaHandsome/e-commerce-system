package by.devtools.order.exception;

import by.devtools.order.dto.ErrorEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ErrorEntity response = new ErrorEntity(ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(response);
    }

}
