package com.book.library.exceptions;

import com.book.library.dto.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalBookLibraryExceptions {

    private static final Logger logger = LoggerFactory.getLogger(GlobalBookLibraryExceptions.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logger.error("Unexpected error", ex);
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));

        APIResponse response = new APIResponse("Please fix these errors!", false, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("Unexpected error", ex);
        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife) {
            String fieldName = ife.getPath().get(0).getFieldName();
            Object invalidValue = ife.getValue();
            String targetType = ife.getTargetType().getSimpleName();

            APIResponse response = new APIResponse(
                    "Invalid JSON input",
                    false,
                    Map.of(
                            fieldName,
                            "Value '" + invalidValue + "' is not valid for " + targetType
                    )
            );
            return ResponseEntity.badRequest().body(response);
        }

        // fallback case
        APIResponse fallback = new APIResponse("Malformed JSON input", false);
        return ResponseEntity.badRequest().body(fallback);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<APIResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        logger.error("Unexpected error", ex);
        String methodUsed = ex.getMethod();
        String supported = ex.getSupportedHttpMethods()
                .toString();

        APIResponse response = new APIResponse(
                "HTTP method not supported",
                false,
                Map.of(
                        "methodUsed", methodUsed,
                        "supportedMethods", supported
                )
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.error("Unexpected error", ex);
        APIResponse response = new APIResponse(
                "Invalid value for parameter: " + ex.getName(),
                false
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Unexpected error", ex);
        APIResponse response = new APIResponse(ex.getMessage(), false);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> handleAPIException(APIException ex) {
        logger.error("Unexpected error", ex);
        APIResponse response = new APIResponse(ex.getMessage(), false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleException(Exception ex) {
        logger.error("Unexpected error", ex);
        APIResponse response = new APIResponse("Internal server error", false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}