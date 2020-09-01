package com.docomodigital.exerciseapi.services;

import com.docomodigital.exerciseapi.common.exception.ApiException;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionDTO;

public interface IExternalApiService {

    public ModelPaymentTransactionDTO executePurchase(String phoneNumber, Double amount, String currency) throws ApiException;

    public ModelPaymentTransactionDTO executeRefund(String orderId) throws ApiException;
}
