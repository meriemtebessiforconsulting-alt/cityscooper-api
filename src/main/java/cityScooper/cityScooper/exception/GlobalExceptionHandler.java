package cityScooper.cityScooper.exception;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<?> handleInvalidRequest(
            InvalidRequestException ex) {

        Map<String, Object> error = Map.of(
            "errorCode", 400,
            "type", "Invalid_request",
            "code", ex.getCode(),
            "message", ex.getMessage()
        );

        Map<String, Object> body = Map.of("errors", List.of(error));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(body);
    }
    
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<?> handleExternalService(
    		ExternalServiceException ex) {

        Map<String, Object> error = Map.of(
            "errorCode", 500,
            "type", "external_service_error",
            "code", ex.getCode(),
            "message", ex.getMessage()
        );

        Map<String, Object> body = Map.of(
            "errors", List.of(error)
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(body);
    }
}