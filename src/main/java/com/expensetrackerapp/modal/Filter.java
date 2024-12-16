package com.expensetrackerapp.modal;

public class Filter {
    private String fieldName;
    private Object value;
    private String operation;

    public Filter(String fieldName, Object value, String operation) {
        this.fieldName = fieldName;
        this.value = value;
        this.operation = operation;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
