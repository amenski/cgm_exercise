package com.docomodigital.exerciseapi.services.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
import com.docomodigital.exerciseapi.swagger.dtos.RefundRequestDTO;

@Service
public class PaymentTransactionImpl implements IPaymentTransaction {

    Logger logger = LoggerFactory.getLogger(PaymentTransactionImpl.class);
    
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;
    
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
            if(Objects.isNull(body) || StringUtils.isBlank(body.getProductId())) {
                throw ExceptionEnums.VALIDATION_EXCEPTION.get().message("Invalid purchase request data.");
            }
            
            if(isNegative(body.getAmount())) {
                throw ExceptionEnums.NEGATIVE_AMOUNT_EXCEPTION.get();
            }
            
            List<ConstantEnum> currencyTypeList = enumRepository.findEnumByTypeAndStatus(ApiConstants.CONSTANT_ENUM_TYPES.CURRENCY_TYPE.name());
            Map<Integer, ConstantEnum> currencyTypeMap = currencyTypeList.stream().collect(Collectors.toMap(ConstantEnum::getEnumCode, Function.identity()));
            if(!validCurrency(body, currencyTypeMap)) {
                throw ExceptionEnums.INVALID_CURRENCY_EXCEPTION.get();
            }
            
            ModelPaymentTransactionDTO response = externalApiService.executePurchase(body.getPhoneNumber(), body.getAmount(), currencyTypeMap.get(body.getCurrency().getId()).getEnumName());
            
            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setPhoneNumber(body.getPhoneNumber());
            transaction.setProductId(body.getProductId());
            transaction.setCreatedAt(now);
            transaction.setUpdatedAt(now);
            transaction.setAmount(body.getAmount());
            transaction.setKind(ApiConstants.KIND.PURCHASE.name());
            transaction.setTransactionId(MDC.get(ApiConstants.UUID_KEY));
            transaction.setStatus(ApiConstants.TX_STATUS.UNSURE.name());
            transaction.setConstantEnum(new ConstantEnum(body.getCurrency().getId()));
            
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
            if(!isValidPhoneNumber(phoneNumber)) {
                throw ExceptionEnums.VALIDATION_EXCEPTION.get().message("Invalid phone-number provided.");
            }
            
            ModelPaymentTransactionListDTO listDto = new ModelPaymentTransactionListDTO();
            List<PaymentTransaction> txList = paymentTransactionRepository.findByPhoneNumber(phoneNumber);
            txList.stream().forEach(tx -> {
                ModelPaymentTransactionDTO row = mapper.map(tx, ModelPaymentTransactionDTO.class);
                listDto.addTransactionsItem(row);
            });
            
            return listDto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    @Loggable
    public boolean refund(RefundRequestDTO body) throws ApiException {
        try{
            OffsetDateTime now = OffsetDateTime.now();
            if(Objects.isNull(body) || StringUtils.isBlank(body.getTransactionId())) {
                throw ExceptionEnums.VALIDATION_EXCEPTION.get().message("Invalid TRANSACTOIN_ID, please try again.");
            }
            PaymentTransaction paymentTransaction = paymentTransactionRepository.findByTransactionId(body.getTransactionId().trim()).orElseThrow(ExceptionEnums.TRANSACTION_NOT_FOUND);
            ModelPaymentTransactionDTO response = externalApiService.executeRefund(paymentTransaction.getOrderId());
            
            paymentTransaction.setUpdatedAt(now);
            paymentTransaction.setKind(ApiConstants.KIND.REFUND.name());
            paymentTransaction.setStatus(ApiConstants.TX_STATUS.UNSURE.name());
            paymentTransaction.setRefundReason(body.getRefundReason());
            
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
     * @param currencyTypeMap 
     * @return
     * @throws ApiException 
     */
    private boolean validCurrency(PurchaseSaveRequestDTO body, Map<Integer, ConstantEnum> currencyTypeMap) throws ApiException {
        String methodName = "validCurrency()";
        if(!isValidPhoneNumber(body.getPhoneNumber())) 
            return false;
        
        // request contains enumId of the currency type, so should be an integer
        Optional<Integer> currencyEnumId = Optional.ofNullable(body.getCurrency()).map(ModelEnumIdValueDTO::getId);
        if(!currencyEnumId.isPresent()) {
            logger.error(ApiConstants.PARAMETER_2, methodName, "Currency can not be empty.");
            return false;
        }

        if(Integer.signum(currencyEnumId.get()) != 1 || !currencyTypeMap.containsKey(currencyEnumId.get())) {
            logger.error(ApiConstants.PARAMETER_2, methodName, "Invalid currency type found.");
            return false;
        }
        
        int end = body.getPhoneNumber().length() - 10;
        String areaCode = body.getPhoneNumber().substring(0, end);
        InternationalPhoneCode row = internationalPhoneCodeRepository.findByAreaCode(areaCode).orElseThrow(ExceptionEnums.AREA_CODE_NOT_FOUND);
        
        return Objects.nonNull(row) && row.getConstantEnum().getEnumCode().equals(currencyEnumId.get());
    }
    
    /**
     * Assuming that a phone number is of an international format, having `+` sign as prefix
     * and with no spaces in between the numbers, validates if a string is a valid phone number.
     * 
     * @param phone
     * @return resulting boolean
     */
    private boolean isValidPhoneNumber(String phone) {
        if(StringUtils.isBlank(phone)) {
            logger.error(ApiConstants.PARAMETER_2, "isValidPhoneNumber()", "Invalid phone number.");
            return false;
        }
        
        return Pattern.matches("\\+\\d{11,15}", phone);
    }

    private boolean isNegative(double value) {
        return Double.doubleToRawLongBits(value) < 0;
    }
}
