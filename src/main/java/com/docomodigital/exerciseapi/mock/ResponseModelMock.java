package com.docomodigital.exerciseapi.mock;

public class ResponseModelMock {

    private String orderId;
    private String msisdn;
    private String result;

    public ResponseModelMock() {
        //
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
