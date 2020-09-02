package com.docomodigital.exerciseapi.services;

import com.docomodigital.exerciseapi.common.exception.ApiException;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionListDTO;
import com.docomodigital.exerciseapi.swagger.dtos.PurchaseSaveRequestDTO;
import com.docomodigital.exerciseapi.swagger.dtos.RefundRequestDTO;

public interface IPaymentTransaction {

    public boolean purchase(PurchaseSaveRequestDTO body) throws ApiException;

    public ModelPaymentTransactionListDTO getTransactionsForCustomer(String phoneNumber) throws ApiException;

    public boolean refund(RefundRequestDTO body) throws ApiException;
}
