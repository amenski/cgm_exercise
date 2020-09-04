package com.docomodigital.exerciseapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.docomodigital.exerciseapi.dal.model.ConstantEnum;
import com.docomodigital.exerciseapi.dal.model.InternationalPhoneCode;
import com.docomodigital.exerciseapi.dal.model.PaymentTransaction;
import com.docomodigital.exerciseapi.swagger.dtos.ModelEnumIdValueDTO;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionDTO;
import com.docomodigital.exerciseapi.swagger.dtos.PurchaseSaveRequestDTO;
import com.docomodigital.exerciseapi.swagger.dtos.RefundRequestDTO;
import com.docomodigital.exerciseapi.swagger.models.ModelEnumIdValue;
import com.docomodigital.exerciseapi.swagger.models.PurchaseSaveRequest;
import com.docomodigital.exerciseapi.swagger.models.RefundRequest;

public class TestUtil {
    
    public static final String PHONE = "+391234567890";
    public static final String PHONE_2 = "+11234567890";
    public static final String INVALID_PHONE = "+39000000";
    public static final Double AMOUNT = 1.0;
    public static final Double INVALID_AMOUNT = -1.0;
    public static final String CURRENCY = "EUR";
    public static final String PRODUCT_ID = "XXDRI980234K";
    public static final String TX_ID = "XXDRI980234KXXDRI980234KXXDRI980234K";
    
    public static PurchaseSaveRequestDTO buildPurchaseRequestDTOBody() {
        ModelEnumIdValueDTO modelCurrencyEnum = new ModelEnumIdValueDTO();
        modelCurrencyEnum.setId(1000);
        modelCurrencyEnum.setValue(CURRENCY);
        
        PurchaseSaveRequestDTO body = new PurchaseSaveRequestDTO();
        body.setAmount(AMOUNT);
        body.setPhoneNumber(PHONE);
        body.setProductId(PRODUCT_ID);
        body.setCurrency(modelCurrencyEnum);
        
        return body;
    }
    
    public static PurchaseSaveRequest buildPurchaseRequestBody() {
        ModelEnumIdValue modelCurrencyEnum = new ModelEnumIdValue();
        modelCurrencyEnum.setId(1000);
        modelCurrencyEnum.setValue(CURRENCY);
        
        PurchaseSaveRequest body = new PurchaseSaveRequest();
        body.setAmount(AMOUNT);
        body.setPhoneNumber(PHONE);
        body.setProductId(PRODUCT_ID);
        body.setCurrency(modelCurrencyEnum);
        
        return body;
    }
    
    public static RefundRequestDTO buildRefundRequestDTOBody() {
        RefundRequestDTO body = new RefundRequestDTO();
        body.setTransactionId(TX_ID);
        body.setRefundReason("Refund reason.");
        
        return body;
    }
    
    public static RefundRequest buildRefundRequestBody() {
        RefundRequest body = new RefundRequest();
        body.setTransactionId(TX_ID);
        body.setRefundReason("Refund reason.");
        
        return body;
    }
    
    public static List<ConstantEnum> buildEnumList() {
        List<ConstantEnum> list = new ArrayList<>();
        ConstantEnum EURO = buildConstanEnum(1000, false, "Description text", "Euro", "EUR", "CURRENCY_TYPE");
        ConstantEnum USD = buildConstanEnum(1001, false, "Description text", "US Dollar", "USD", "CURRENCY_TYPE");
        
        list.add(EURO);
        list.add(USD);
        
        return list;
    }
    
    public static ConstantEnum buildConstanEnum(Integer enumCode, boolean disabled, String enumDesc, 
            String enumLabel, String enumName, String enumType) {
        ConstantEnum enumObj = new ConstantEnum();
        enumObj.setDisabled(disabled);
        enumObj.setEnumCode(enumCode);
        enumObj.setEnumDesc(enumDesc);
        enumObj.setEnumLabel(enumLabel);
        enumObj.setEnumName(enumName);
        enumObj.setEnumType(enumType);
        
        return enumObj;
    }
    
    public static Optional<InternationalPhoneCode> buildInternationalPhoneCode() {
        InternationalPhoneCode code = new InternationalPhoneCode();
        code.setId(1);
        code.setCountry("ITA");
        code.setAreaCode("+39");
        code.setConstantEnum(buildEnumList().get(0));
        return Optional.of(code);
    }
    
    public static ModelPaymentTransactionDTO buildResponseModelPaymentTransactionDTOMock() {
        return new ModelPaymentTransactionDTO()
                .orderId("MOCKORDERID")
                .msisdn("MOCKMSISDN")
                .status("SUCCESS");
    }
    
    public static List<PaymentTransaction> buildPaymentTransactionList() {
        List<PaymentTransaction> txGivenList = new ArrayList<>();
        PaymentTransaction tx = new PaymentTransaction();
        txGivenList.add(tx);
        
        return txGivenList;
    }
    
    private TestUtil() {
        throw new IllegalStateException();
    }
}
