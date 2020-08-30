package com.docomodigital.exerciseapi.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.docomodigital.exerciseapi.common.annotations.Loggable;
import com.docomodigital.exerciseapi.common.exception.ApiException;
import com.docomodigital.exerciseapi.dal.model.PaymentTransaction;
import com.docomodigital.exerciseapi.services.IPaymentTransaction;
import com.docomodigital.exerciseapi.swagger.dtos.PurchaseSaveRequestDTO;

@Service
public class PaymentTransactionImpl implements IPaymentTransaction {

    @Override
    @Loggable
    public boolean purchase(PurchaseSaveRequestDTO detail) throws ApiException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    @Loggable
    public List<PaymentTransaction> findAllTransactionsForCustomer(String phoneNumber) throws ApiException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Loggable
    public boolean refund(String transactionId) throws ApiException {
        // TODO Auto-generated method stub
        return false;
    }

}
