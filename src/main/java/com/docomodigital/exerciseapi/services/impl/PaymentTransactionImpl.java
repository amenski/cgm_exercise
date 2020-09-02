package com.docomodigital.exerciseapi.services.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.docomodigital.exerciseapi.common.annotations.Loggable;
import com.docomodigital.exerciseapi.common.exception.ApiException;
import com.docomodigital.exerciseapi.common.exception.ExceptionEnums;
import com.docomodigital.exerciseapi.common.utils.ApiConstants;
import com.docomodigital.exerciseapi.dal.model.ConstantEnum;
import com.docomodigital.exerciseapi.dal.model.InternationalPhoneCode;
import com.docomodigital.exerciseapi.dal.model.PaymentTransaction;
import com.docomodigital.exerciseapi.dal.repository.ConstantsEnumRepository;
import com.docomodigital.exerciseapi.dal.repository.InternationalPhoneCodeRepository;
import com.docomodigital.exerciseapi.dal.repository.PaymentTransactionRepository;
import com.docomodigital.exerciseapi.services.IExternalApiService;
import com.docomodigital.exerciseapi.services.IPaymentTransaction;
import com.docomodigital.exerciseapi.swagger.dtos.ModelEnumIdValueDTO;
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
    private ConstantsEnumRepository enumRepository;
    
    @Autowired
    private InternationalPhoneCodeRepository internationalPhoneCodeRepository;
    
    @Autowired
    private IExternalApiService externalApiService;
    
    @Autowired
    private ModelMapper mapper;
    
    @Override
    @Loggable
    public boolean purchase(PurchaseSaveRequestDTO body) throws ApiException {
        try{
            OffsetDateTime now = OffsetDateTime.now();
            if(Objects.isNull(body) || "".equals(body.getProductId())) {
                throw ExceptionEnums.VALIDATION_EXCEPTION.get().message("Invalid purchase request data.");
            }
            
            if(isNegative(body.getAmount())) {
                throw ExceptionEnums.NEGATIVE_AMOUNT_EXCEPTION.get();
            }
            
            if(!validCurrency(body)) {
                throw ExceptionEnums.INVALID_CURRENCY_EXCEPTION.get();
            }
            
            ModelPaymentTransactionDTO response = externalApiService.executePurchase(body.getPhoneNumber(), body.getAmount(), body.getCurrency().getValue());
            
            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setPhoneNumber(body.getPhoneNumber());
            transaction.setProductId(body.getProductId());
            transaction.setCreatedAt(now);
            transaction.setUpdatedAt(now);
            transaction.setAmount(body.getAmount());
            transaction.setKind(ApiConstants.KIND.PURCHASE.name());
            transaction.setTransactionId(servletRequest.getHeader(ApiConstants.TRANSACTION_ID_KEY));
            transaction.setStatus(ApiConstants.TX_STATUS.UNSURE.name());
            
            if(Objects.nonNull(response)) {
                transaction.setFailureMessage(response.getFailureMessage());
                transaction.setMsisdn(response.getMsisdn());
                transaction.setOrderId(response.getOrderId());
                transaction.setStatus(response.getStatus());
            }
            
            paymentTransactionRepository.save(transaction);
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    @Loggable
    public ModelPaymentTransactionListDTO getTransactionsForCustomer(String phoneNumber) throws ApiException {
        try{
            if("".equals(phoneNumber.trim())) {
                throw ExceptionEnums.VALIDATION_EXCEPTION.get().message("Invalid phone-number provided.");
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
        try{
            OffsetDateTime now = OffsetDateTime.now();
            if(transactionId == null || "".equals(transactionId.trim())) {
                throw ExceptionEnums.VALIDATION_EXCEPTION.get().message("Invalid TRANSACTOIN_ID, please try again.");
            }
            PaymentTransaction paymentTransaction = paymentTransactionRepository.findByTransactionId(transactionId).orElseThrow(ExceptionEnums.TRANSACTION_NOT_FOUND);
            ModelPaymentTransactionDTO response = externalApiService.executeRefund(paymentTransaction.getOrderId());
            
            paymentTransaction.setUpdatedAt(now);
            paymentTransaction.setKind(ApiConstants.KIND.REFUND.name());
            paymentTransaction.setStatus(ApiConstants.TX_STATUS.UNSURE.name());
            
            if(Objects.nonNull(response)) {
                paymentTransaction.setFailureMessage(response.getFailureMessage());
                paymentTransaction.setStatus(response.getStatus());
            }
            
            paymentTransactionRepository.save(paymentTransaction);
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    
    /**
     * Assuming that phoneNumber has variable prefix(e.g. +1, +39, +251 etc) and 10 numerals after the prefix,
     * validates if the currency is allowed for the current areaCode.
     * 
     * @param body
     * @return
     * @throws ApiException 
     */
    private boolean validCurrency(PurchaseSaveRequestDTO body) throws ApiException {
        if(!isValidPhoneNumber(body.getPhoneNumber())) 
            return false;
        
        ModelEnumIdValueDTO enumDto = body.getCurrency();
        List<ConstantEnum> currencyTypeList = enumRepository.findEnumByTypeAndStatus(ApiConstants.CONSTANT_ENUM_TYPES.CURRENCY_TYPE.name());
        boolean currencyIdFound = currencyTypeList.stream().anyMatch(row -> row.getEnumCode().equals(enumDto.getId()));
        if(Integer.signum(enumDto.getId()) != 1 || !currencyIdFound) {
            logger.error(ApiConstants.PARAMETER_2, "validCurrency()", "Invalid currency type found.");
            return false;
        }
        
        int start = body.getPhoneNumber().length() - 10;
        String areaCode = body.getPhoneNumber().substring(start);
        InternationalPhoneCode row = internationalPhoneCodeRepository.findByAreaCode(areaCode).orElseThrow(ExceptionEnums.AREA_CODE_NOT_FOUND);
        
        // request contains enumId of the currency type, so should be an integer
        Optional<Integer> currencyEnumId = Optional.ofNullable(body.getCurrency()).map(ModelEnumIdValueDTO::getId);
        if(currencyEnumId.isPresent() && row != null) {
            return row.getConstantEnum().getEnumCode().equals(currencyEnumId.get());
        }
        return false;
    }
    
    /**
     * Assuming that a phone number is of an international format, having `+` sign as prefix
     * and with no spaces in between the numbers, validates if a string is a valid phone number.
     * 
     * @param phone
     * @return resulting boolean
     */
    private boolean isValidPhoneNumber(String phone) {
        if(phone == null || "".equals(phone.trim())) {
            logger.error(ApiConstants.PARAMETER_2, "isValidPhoneNumber()", "Invalid phone number.");
            return false;
        }
        
        return Pattern.matches("\\+\\d{10,15}", phone);
    }

    private boolean isNegative(double value) {
        return Double.doubleToRawLongBits(value) < 0;
    }
}
