package com.expensetrackerapp.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlobalExceptionHandlerTest {


    private GlobalExceptionHandler exceptionHandler;


    @BeforeEach
    public void setup() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleExpenseNotFoundException() {
        ExpenseNotFoundException exception = new ExpenseNotFoundException("Invalid expense id!!. Please provide valid id");
        ResponseEntity<HttpApiResponse> responseEntity = exceptionHandler.expenseNotFoundException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        HttpApiResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode());
        assertEquals("Invalid expense id!!. Please provide valid id", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    public void handleUserNotFoundExceptionTest() {
        UserNotFoundException exception = new UserNotFoundException("Invalid User id!!. Please provide valid id");
        ResponseEntity<HttpApiResponse> responseEntity = exceptionHandler.userNotFoundException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        HttpApiResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode());
        assertEquals("Invalid User id!!. Please provide valid id", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    public void handleExpenseIDMismatchExceptionTest() {
        ExpenseIdMissMatchException exception = new ExpenseIdMissMatchException("Expense id is not match with id present in Path ");

        ResponseEntity<HttpApiResponse> responseEntity = exceptionHandler.expenseMisMatchException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        HttpApiResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode());
        assertEquals("Expense id is not match with id present in Path ", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    public void handleBadCredentialExceptionTest() {
        BadCredentialsException exception = new BadCredentialsException("Bad Credentials");

        ResponseEntity<HttpApiResponse<Object>> responseEntity = exceptionHandler.badCredentialException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        HttpApiResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode());
        assertEquals("Bad Credentials - Enter valid username/password", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }


    @Test
    public void handleGenericExceptionTest() {
        Exception exception = new Exception("Generic error");
        ResponseEntity<HttpApiResponse> responseEntity = exceptionHandler.exception(exception);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        HttpApiResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode());
        assertEquals("An Server Exception occurred!", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    public void buildResponseMethodTest() {
        HttpApiResponse apiResponse = GlobalExceptionHandler.buildApiResponse("Test Message");
        assertNotNull(apiResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), apiResponse.getStatusCode());
        assertEquals("Test Message", apiResponse.getMessage());
        assertNotNull(apiResponse.getTimestamp());
    }


}
