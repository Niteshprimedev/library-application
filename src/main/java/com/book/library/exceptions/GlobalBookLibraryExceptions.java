package com.book.library.exceptions;

import com.book.library.dto.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalBookLibraryExceptions {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleMethodArgNotValidException(MethodArgumentNotValidException exception){
        String message = "Validation failed";

        FieldError fieldError = exception.getBindingResult().getFieldError();
        if(fieldError != null){
            message = fieldError.getDefaultMessage();
        }

        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleException(Exception exception){
        String message = exception.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);

        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> handleResourceNotFoundException(ResourceNotFoundException exception){
        String message = exception.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);

        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> handleAPIException(APIException exception){
        String message = exception.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
