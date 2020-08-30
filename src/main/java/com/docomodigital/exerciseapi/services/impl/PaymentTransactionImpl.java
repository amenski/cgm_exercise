package com.docomodigital.exerciseapi.services.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.docomodigital.exerciseapi.common.annotations.Loggable;
import com.docomodigital.exerciseapi.common.exception.ApiException;
import com.docomodigital.exerciseapi.common.exception.ExceptionEnums;
import com.docomodigital.exerciseapi.common.utils.ApiConstants;
import com.docomodigital.exerciseapi.dal.model.PaymentTransaction;
import com.docomodigital.exerciseapi.dal.repository.PaymentTransactionRepository;
import com.docomodigital.exerciseapi.services.IPaymentTransaction;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionDTO;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionListDTO;
import com.docomodigital.exerciseapi.swagger.dtos.PurchaseSaveRequestDTO;

@Service
public class PaymentTransactionImpl implements IPaymentTransaction {

    Logger logger = LoggerFactory.getLogger(PaymentTransactionImpl.class);
    
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;
    
    @Autowired
    private HttpServletRequest servletRequest;
    
    @Autowired
    private ModelMapper mapper;
    
    @Override
    @Loggable
    public boolean purchase(PurchaseSaveRequestDTO body) throws ApiException {
        try{
            OffsetDateTime txBeginTime = OffsetDateTime.now();
            if(Objects.isNull(body)) {
                throw ExceptionEnums.VALIDATION_EXCEPTION.get().message("Invalid purchase request body.");
            }
            
            if(isNegative(body.getAmount())) {
                throw ExceptionEnums.NEGATIVE_AMOUNT_EXCEPTION.get();
            }
            
            // TODO call external api to do the transaction and the save localy
            
            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setPhoneNumber(body.getPhoneNumber());
            transaction.setProductId(body.getProductId());
            transaction.setTransactionBegin(txBeginTime);
            transaction.setTransactionEnd(OffsetDateTime.now());
            transaction.setAmount(body.getAmount());
            transaction.setTransactionId(servletRequest.getHeader(ApiConstants.TRANSACTION_ID_KEY)); 
            
            paymentTransactionRepository.save(transaction);
            return true;
        } catch(ConstraintViolationException | DataIntegrityViolationException e) {
            throw ExceptionEnums.VALIDATION_EXCEPTION.get();
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    @Loggable
    public ModelPaymentTransactionListDTO getTransactionsForCustomer(String phoneNumber) throws ApiException {
        try{
            if("".equals(phoneNumber.trim())) {
                throw ExceptionEnums.VALIDATION_EXCEPTION.get();
            }
            
            ModelPaymentTransactionListDTO listDto = new ModelPaymentTransactionListDTO();
            List<PaymentTransaction> txList = paymentTransactionRepository.findByPhoneNumber(phoneNumber);
            txList.stream().forEach(tx -> {
                ModelPaymentTransactionDTO row = mapper.map(tx, ModelPaymentTransactionDTO.class);
                listDto.addListItem(row);
            });
            
            return listDto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    @Loggable
    public boolean refund(String transactionId) throws ApiException {
        // TODO Auto-generated method stub
        return false;
    }
    
    
    private boolean isNegative(double value) {
        return Double.doubleToRawLongBits(value) < 0;
    }
}
