package com.docomodigital.exerciseapi.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jboss.logging.MDC;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.client.RestTemplate;

import com.docomodigital.exerciseapi.ApiExceptionMatcher;
import com.docomodigital.exerciseapi.common.exception.ExceptionEnums;
import com.docomodigital.exerciseapi.dal.model.ConstantEnum;
import com.docomodigital.exerciseapi.dal.model.InternationalPhoneCode;
import com.docomodigital.exerciseapi.dal.model.PaymentTransaction;
import com.docomodigital.exerciseapi.dal.repository.ConstantsEnumRepository;
import com.docomodigital.exerciseapi.dal.repository.InternationalPhoneCodeRepository;
import com.docomodigital.exerciseapi.dal.repository.PaymentTransactionRepository;
import com.docomodigital.exerciseapi.services.impl.PaymentTransactionImpl;
import com.docomodigital.exerciseapi.swagger.dtos.ModelEnumIdValueDTO;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionDTO;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionListDTO;
import com.docomodigital.exerciseapi.swagger.dtos.PurchaseSaveRequestDTO;
import com.docomodigital.exerciseapi.swagger.dtos.RefundRequestDTO;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MDC.class)
@SuppressWarnings("unchecked")
public class PaymentTransactionImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    RestTemplate restTemplate;
    
    @Mock
    PaymentTransactionRepository paymentTransactionRepository;
    
    @Mock
    ConstantsEnumRepository enumRepository;
    
    @Mock
    InternationalPhoneCodeRepository internationalPhoneCodeRepository;
    
    @Mock
    IExternalApiService externalApiService;
    
    @Spy
    ModelMapper mapper = new ModelMapper();
    
    @InjectMocks
    PaymentTransactionImpl paymentTransactionImpl;
    
    private static final String PHONE = "+391234567890";
    private static final String PHONE_2 = "+11234567890";
    private static final String INVALID_PHONE = "+39000000";
    private static final Double AMOUNT = 1.0;
    private static final Double INVALID_AMOUNT = -1.0;
    private static final String CURRENCY = "EUR";
    private static final String PRODUCT_ID = "XXDRI980234K";
    private static final String TX_ID = "XXDRI980234KXXDRI980234KXXDRI980234K";
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void executePurchaseThenInvalidDataException() throws Exception {
        //GIVEN
        PurchaseSaveRequestDTO body = new PurchaseSaveRequestDTO();
        
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.VALIDATION_EXCEPTION));
        
        paymentTransactionImpl.purchase(body);
    }
    
    @Test
    public void executePurchaseThenInvalidAmountException() throws Exception {
        //GIVEN
        PurchaseSaveRequestDTO body = buildPurchaseRequestBody();
        body.setAmount(INVALID_AMOUNT);
        
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.NEGATIVE_AMOUNT_EXCEPTION));
        
        paymentTransactionImpl.purchase(body);
    }
    
    @Test
    public void executePurchaseThenInvalidCurrencyTypeException() throws Exception {
        //GIVEN
        PurchaseSaveRequestDTO body = buildPurchaseRequestBody();
        
        //MOCKITO
        Mockito.when(enumRepository.findEnumByTypeAndStatus(anyString())).thenReturn(new ArrayList<>());
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.INVALID_CURRENCY_EXCEPTION));
        
        paymentTransactionImpl.purchase(body);
    }
    
    @Test
    public void executePurchaseThenInvalidPhoneAreaCodeException() throws Exception {
      //GIVEN
        PurchaseSaveRequestDTO body = buildPurchaseRequestBody();
        body.setPhoneNumber(PHONE_2);
        
        //MOCKITO
        Mockito.when(enumRepository.findEnumByTypeAndStatus(anyString())).thenReturn(buildEnumList());
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.AREA_CODE_NOT_FOUND));
        
        paymentTransactionImpl.purchase(body);
    }
    
    @Test
    public void executePurchaseThenExternalApiException() throws Exception {
        //GIVEN
        PurchaseSaveRequestDTO body = buildPurchaseRequestBody();
        
        //MOCKITO
        Mockito.when(enumRepository.findEnumByTypeAndStatus(anyString())).thenReturn(buildEnumList());
        Mockito.when(internationalPhoneCodeRepository.findByAreaCode(anyString())).thenReturn(buildInternationalPhoneCode());
        Mockito.when(externalApiService.executePurchase(anyString(), any(Double.class), anyString())).thenThrow(ExceptionEnums.EXTERNAL_API_EXCEPTION.get());

        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.EXTERNAL_API_EXCEPTION));
        
        paymentTransactionImpl.purchase(body);
    }
    
    @Test
    public void executePurchaseThenSuccess() throws Exception {
        //GIVEN
        PurchaseSaveRequestDTO body = buildPurchaseRequestBody();
        
        //MOCKITO
        Mockito.when(enumRepository.findEnumByTypeAndStatus(anyString())).thenReturn(buildEnumList());
        Mockito.when(internationalPhoneCodeRepository.findByAreaCode(anyString())).thenReturn(buildInternationalPhoneCode());
        Mockito.when(externalApiService.executePurchase(anyString(), any(Double.class), anyString())).thenReturn(buildResponseModelPaymentTransactionDTOMock());
        
        //VALIDATE
        boolean result = paymentTransactionImpl.purchase(body);
        Assert.assertTrue(result);
    }
    
    @Test
    public void getTransactionsForCustomerThenThrowException() throws Exception {
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.VALIDATION_EXCEPTION));
        paymentTransactionImpl.getTransactionsForCustomer(INVALID_PHONE);
    }
    
    @Test
    public void getTransactionsForCustomerThenSuccess() throws Exception {
        //MOCKITO
        Mockito.when(paymentTransactionRepository.findByPhoneNumber(anyString())).thenReturn(new ArrayList<>());
        
        //ASSERT
        ModelPaymentTransactionListDTO txList = paymentTransactionImpl.getTransactionsForCustomer(PHONE_2);
        Assert.assertNotNull(txList);
        Assert.assertNull(txList.getTransactions());
    }
    
    @Test
    public void getTransactionsForCustomerThenSuccess1() throws Exception {
        //MOCKITO
        Mockito.when(paymentTransactionRepository.findByPhoneNumber(anyString())).thenReturn(buildPaymentTransactionList());
        
        //ASSERT
        ModelPaymentTransactionListDTO txList = paymentTransactionImpl.getTransactionsForCustomer(PHONE_2);
        Assert.assertNotNull(txList);
        Assert.assertFalse(txList.getTransactions().isEmpty());
    }
    
    @Test
    public void refundThenValidationException() throws Exception {
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.VALIDATION_EXCEPTION));
        paymentTransactionImpl.refund(null);
    }
    
    @Test
    public void refundThenTransactionNotFoundException() throws Exception {
        //MOCKITO
        Mockito.when(paymentTransactionRepository.findByTransactionId(anyString())).thenReturn(Optional.empty());
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.TRANSACTION_NOT_FOUND));
        
        paymentTransactionImpl.refund(buildRefundRequestBody());
    }
    
//    @Test
//    public void refundThenExternalApiException() throws Exception {
//        //MOCKITO
//        Mockito.when(paymentTransactionRepository.findByTransactionId(anyString())).thenReturn(Optional.of(buildPaymentTransactionList().get(0)));
//        Mockito.when(externalApiService.executeRefund(anyString())).thenThrow(ExceptionEnums.EXTERNAL_API_EXCEPTION.get());
//        
//        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.EXTERNAL_API_EXCEPTION));
//        
//        paymentTransactionImpl.refund(buildRefundRequestBody());
//    }
    
    //======  mock objects ====== //
    private static PurchaseSaveRequestDTO buildPurchaseRequestBody() {
        ModelEnumIdValueDTO modelCurrencyEnum = new ModelEnumIdValueDTO();
        modelCurrencyEnum.setId(1000);
        modelCurrencyEnum.setValue("EUR");
        
        PurchaseSaveRequestDTO body = new PurchaseSaveRequestDTO();
        body.setAmount(AMOUNT);
        body.setPhoneNumber(PHONE);
        body.setProductId(PRODUCT_ID);
        body.setCurrency(modelCurrencyEnum);
        
        return body;
    }
    
    private static RefundRequestDTO buildRefundRequestBody() {
        RefundRequestDTO body = new RefundRequestDTO();
        body.setTransactionId(TX_ID);
        body.setRefundReason("Refund reason.");
        
        return body;
    }
    
    private static List<ConstantEnum> buildEnumList() {
        List<ConstantEnum> list = new ArrayList<>();
        ConstantEnum EURO = buildConstanEnum(1000, false, "Description text", "Euro", "EUR", "CURRENCY_TYPE");
        ConstantEnum USD = buildConstanEnum(1001, false, "Description text", "US Dollar", "USD", "CURRENCY_TYPE");
        
        list.add(EURO);
        list.add(USD);
        
        return list;
    }
    
    private static ConstantEnum buildConstanEnum(Integer enumCode, boolean disabled, String enumDesc, 
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
    
    private static Optional<InternationalPhoneCode> buildInternationalPhoneCode() {
        InternationalPhoneCode code = new InternationalPhoneCode();
        code.setId(1);
        code.setCountry("ITA");
        code.setAreaCode("+39");
        code.setConstantEnum(buildEnumList().get(0));
        return Optional.of(code);
    }
    
    private static ModelPaymentTransactionDTO buildResponseModelPaymentTransactionDTOMock() {
        return new ModelPaymentTransactionDTO()
                .orderId("MOCKORDERID")
                .msisdn("MOCKMSISDN")
                .status("SUCCESS");
    }
    
    private static List<PaymentTransaction> buildPaymentTransactionList() {
        List<PaymentTransaction> txGivenList = new ArrayList<>();
        PaymentTransaction tx = new PaymentTransaction();
        txGivenList.add(tx);
        
        return txGivenList;
    }
}
