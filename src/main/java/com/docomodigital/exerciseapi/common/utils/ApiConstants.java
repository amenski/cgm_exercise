package com.docomodigital.exerciseapi.common.utils;

public class ApiConstants {
    
    // request type
    public enum KIND {
        PURCHASE, REFUND;
    }
    
    // request status
    public enum TX_STATUS {
        SUCCESS, FAILURE, UNSURE;
    }
    
    // log
    public static final String PARAMETER_2 = "{} {}";
    public static final String PARAMETER_3 = "{} {} {}";
    public static final String METHOD_START = " method start.";
    public static final String METHOD_END = " method end.";
    public static final String INPUT_PARAMETER = " input parameter ";

    // request enhacement
    public static final String UUID_KEY = "UUID";
    public static final String TRANSACTION_ID_KEY = "TRANSACTION_ID";

    private ApiConstants() throws Exception {
        throw new IllegalStateException("Utility class.");
    }
}
