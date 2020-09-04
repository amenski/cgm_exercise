package com.docomodigital.exerciseapi.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.function.Supplier;

import com.docomodigital.exerciseapi.common.exception.ApiException;
import com.docomodigital.exerciseapi.swagger.models.ResponseBase;

public class AssertTestHelper {

    public static <T extends ResponseBase> void checkForSucces(T response) {
        assertTrue(response.isSuccess());
        assertEquals(200, response.getResultCode().intValue());
        assertThat(response.getErrors()).isNull();
        assertThat(response.getMessage()).isNull();
    }

    public static <T extends ResponseBase> void checkForGenericException(T response) {
        assertFalse(response.isSuccess());
        assertNotEquals(200, response.getResultCode().intValue());
        assertThat(response.getMessage()).isNotNull();
    }

    public static <T extends ResponseBase> void checkForApiException(T response, Supplier<ApiException> ex) {
        assertFalse(response.isSuccess());
        assertEquals(ex.get().getErrorCode(), response.getResultCode().intValue());
        assertThat(response.getMessage()).isNotNull();
    }

    private AssertTestHelper() {
        throw new IllegalStateException("Utility Class");
    }
}
