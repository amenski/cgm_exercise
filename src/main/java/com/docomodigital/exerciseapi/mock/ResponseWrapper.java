package com.docomodigital.exerciseapi.mock;

import java.util.ArrayList;
import java.util.List;

public class ResponseWrapper<T> {
    private boolean success = false;
    private int resultCode;
    private String message;
    private List<String> errors;
    private T entityResult;

    public ResponseWrapper() {
        //
    }

    public ResponseWrapper(T entityResult) {
        this.entityResult = entityResult;
        this.resultCode = 0;
        this.errors = new ArrayList<>();
        this.message = null;
        this.success = true;
    }

    public T getEntityResult() {
        return entityResult;
    }

    public void setEntityResult(T entityResult) {
        this.entityResult = entityResult;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        if (errors == null)
            errors = new ArrayList<>();
        
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void addError(String error) {
        if (errors == null)
            errors = new ArrayList<>();

        errors.add(error);
    }

}
