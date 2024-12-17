package com.expensetrackerapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpenseIdMissMatchException.class)
    public ResponseEntity<HttpApiResponse> expenseMisMatchException(ExpenseIdMissMatchException ex){
        return new ResponseEntity<>(buildApiResponse(ex.getMessage(),HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpApiResponse> userNotFoundException(UserNotFoundException ex){
        return new ResponseEntity<>(buildApiResponse(ex.getMessage(),HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<HttpApiResponse> expenseNotFoundException(ExpenseNotFoundException ex){
        return new ResponseEntity<>(buildApiResponse(ex.getMessage(),HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpApiResponse<Object>> badCredentialException(BadCredentialsException ex){
        return new ResponseEntity<>(buildApiResponse(ex.getMessage()+ " - Enter valid username/password",HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpApiResponse> handleValidationError(MethodArgumentNotValidException ex) {
        List<com.expensetrackerapp.exception.FieldError> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new com.expensetrackerapp.exception.FieldError(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        HttpApiResponse httpApiResponse = buildApiResponse("Validation Error occurred for following fields",HttpStatus.BAD_REQUEST);
        httpApiResponse.setErrorList(fieldErrors);
        return new ResponseEntity<>(httpApiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpApiResponse> exception(Exception ex){
        ex.printStackTrace();
        return new ResponseEntity<>(buildApiResponse("An Server Exception occurred!",HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static HttpApiResponse buildApiResponse(String message, HttpStatus status) {
        HttpApiResponse apiResponse = new HttpApiResponse();
        apiResponse.setStatusCode(status.value());
        apiResponse.setMessage(message);
        apiResponse.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return apiResponse;
    }

    public static HttpApiResponse buildApiResponse(String message) {
        HttpApiResponse apiResponse = new HttpApiResponse();
        apiResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        apiResponse.setMessage(message);
        apiResponse.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return apiResponse;
    }
}
