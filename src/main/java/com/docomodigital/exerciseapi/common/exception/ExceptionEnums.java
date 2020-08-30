package com.docomodigital.exerciseapi.common.exception;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

/**
 * a supplier of custom exceptions
 */
public enum ExceptionEnums implements Supplier<ApiException> {

    VALIDATION_EXCEPTION(new ApiException(HttpStatus.BAD_REQUEST, 400001, "The input data has a mistake, please try again.")),
    NEGATIVE_AMOUNT_EXCEPTION(new ApiException(HttpStatus.BAD_REQUEST, 400002, "Amount can not be less or equal to zero.")),

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
    
    // validate internal error codes
    static {
        final int errorCodeLength = 6;
        List<List<ExceptionEnums>> duplicateCodes = Arrays.stream(ExceptionEnums.values())
                .collect(Collectors.groupingBy(e -> e.get().getErrorCode())).values().stream().filter(e -> e.size() > 1)
                .collect(Collectors.toList());

        if (!duplicateCodes.isEmpty()) {
            throw new IllegalStateException("Duplicate error codes found: " + duplicateCodes);

        }

        for (ExceptionEnums e : ExceptionEnums.values()) {
            if (String.valueOf(e.get().getErrorCode()).length() != errorCodeLength) {
                throw new IllegalStateException(e + " -  Error code must have " + errorCodeLength + " digits");
            }
        }

        for (ExceptionEnums e : ExceptionEnums.values()) {
            if (!String.valueOf(e.get().getErrorCode()).substring(0, 3).equals(String.valueOf(e.get().getHttpCode()))) {
                throw new IllegalStateException(e + " - Error code prefix does not match http code");
            }
        }
    }
}
