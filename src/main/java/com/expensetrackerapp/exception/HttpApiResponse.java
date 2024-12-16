package com.expensetrackerapp.exception;

import java.sql.Timestamp;
import java.util.List;

public class HttpApiResponse<T> {
    private int statusCode;
    private String message;
    private Timestamp timestamp;
    private List<FieldError> errorList;
    private T responseData;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<FieldError> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<FieldError> errorList) {
        this.errorList = errorList;
    }

    public T getResponseData() {
        return responseData;
    }

    public void setResponseData(T responseData) {
        this.responseData = responseData;
    }

    @Override
    public String toString() {
        return "HttpApiResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", errorList=" + errorList +
                ", responseData=" + responseData +
                '}';
    }
}
