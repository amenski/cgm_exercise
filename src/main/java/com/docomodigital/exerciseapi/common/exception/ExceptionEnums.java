package com.docomodigital.exerciseapi.common.exception;

import java.util.function.Supplier;

import org.springframework.http.HttpStatus;

/**
 * a supplier of custom exceptions
 */
public enum ExceptionEnums implements Supplier<ApiException> {

    VALIDATION_EXCEPTION(new ApiException(HttpStatus.BAD_REQUEST, 400001, "The input data has a mistake, please try again.")),

    RECORD_NOT_FOUND(new ApiException(HttpStatus.NOT_FOUND, 404001, "Record not found.")),

    UNHANDLED_EXCEPTION(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 500001, "Unhandled exception has occured."));

    /// ====== ======///
    private ApiException e;

    private ExceptionEnums(ApiException ex) {
        this.e = ex;
    }

    public void message(String message) {
        e.setMessage(message);
    }

    /**
     * gets the ApiException instance triggered by the enums
     * 
     * @param e
     * @return ApiException of defined enums
     */
    @Override
    public ApiException get() {
        return e;
    }
}
