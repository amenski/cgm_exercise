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
import com.docomodigital.exerciseapi.TestUtil;
import com.docomodigital.exerciseapi.common.exception.ExceptionEnums;
import com.docomodigital.exerciseapi.dal.model.ConstantEnum;
import com.docomodigital.exerciseapi.dal.model.InternationalPhoneCode;
import com.docomodigital.exerciseapi.dal.model.PaymentTransaction;
import com.docomodigital.exerciseapi.dal.repository.ConstantsEnumRepository;
import com.docomodigital.exerciseapi.dal.repository.InternationalPhoneCodeRepository;
import com.docomodigital.exerciseapi.dal.repository.PaymentTransactionRepository;
import com.docomodigital.exerciseapi.services.impl.ExternalApiServiceImpl;
import com.docomodigital.exerciseapi.services.impl.PaymentTransactionImpl;
import com.docomodigital.exerciseapi.swagger.dtos.ModelEnumIdValueDTO;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionDTO;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionListDTO;
import com.docomodigital.exerciseapi.swagger.dtos.PurchaseSaveRequestDTO;
import com.docomodigital.exerciseapi.swagger.dtos.RefundRequestDTO;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MDC.class)
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
    ExternalApiServiceImpl externalApiService;
    
    @Spy
    ModelMapper mapper = new ModelMapper();
    
    @InjectMocks
    PaymentTransactionImpl paymentTransactionImpl;
    
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
        PurchaseSaveRequestDTO body = TestUtil.buildPurchaseRequestDTOBody();
        body.setAmount(TestUtil.INVALID_AMOUNT);
        
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.NEGATIVE_AMOUNT_EXCEPTION));
        
        paymentTransactionImpl.purchase(body);
    }
    
    @Test
    public void executePurchaseThenInvalidCurrencyTypeException() throws Exception {
        //GIVEN
        PurchaseSaveRequestDTO body = TestUtil.buildPurchaseRequestDTOBody();
        
        //MOCKITO
        Mockito.when(enumRepository.findEnumByTypeAndStatus(anyString())).thenReturn(new ArrayList<>());
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.INVALID_CURRENCY_EXCEPTION));
        
        paymentTransactionImpl.purchase(body);
    }
    
    @Test
    public void executePurchaseThenInvalidPhoneAreaCodeException() throws Exception {
      //GIVEN
        PurchaseSaveRequestDTO body = TestUtil.buildPurchaseRequestDTOBody();
        body.setPhoneNumber(TestUtil.PHONE_2);
        
        //MOCKITO
        Mockito.when(enumRepository.findEnumByTypeAndStatus(anyString())).thenReturn(TestUtil.buildEnumList());
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.AREA_CODE_NOT_FOUND));
        
        paymentTransactionImpl.purchase(body);
    }
    
    @Test
    public void executePurchaseThenExternalApiException() throws Exception {
        //GIVEN
        PurchaseSaveRequestDTO body = TestUtil.buildPurchaseRequestDTOBody();
        
        //MOCKITO
        Mockito.when(enumRepository.findEnumByTypeAndStatus(anyString())).thenReturn(TestUtil.buildEnumList());
        Mockito.when(internationalPhoneCodeRepository.findByAreaCode(anyString())).thenReturn(TestUtil.buildInternationalPhoneCode());
        Mockito.when(externalApiService.executePurchase(anyString(), any(Double.class), anyString())).thenThrow(ExceptionEnums.EXTERNAL_API_EXCEPTION.get());

        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.EXTERNAL_API_EXCEPTION));
        
        paymentTransactionImpl.purchase(body);
    }
    
    @Test
    public void executePurchaseThenSuccess() throws Exception {
        //GIVEN
        PurchaseSaveRequestDTO body = TestUtil.buildPurchaseRequestDTOBody();
        
        //MOCKITO
        Mockito.when(enumRepository.findEnumByTypeAndStatus(anyString())).thenReturn(TestUtil.buildEnumList());
        Mockito.when(internationalPhoneCodeRepository.findByAreaCode(anyString())).thenReturn(TestUtil.buildInternationalPhoneCode());
        Mockito.when(externalApiService.executePurchase(anyString(), any(Double.class), anyString())).thenReturn(TestUtil.buildResponseModelPaymentTransactionDTOMock());
        
        //VALIDATE
        boolean result = paymentTransactionImpl.purchase(body);
        Assert.assertTrue(result);
    }
    
    @Test
    public void getTransactionsForCustomerThenThrowException() throws Exception {
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.VALIDATION_EXCEPTION));
        paymentTransactionImpl.getTransactionsForCustomer(TestUtil.INVALID_PHONE);
    }
    
    @Test
    public void getTransactionsForCustomerThenSuccess() throws Exception {
        //MOCKITO
        Mockito.when(paymentTransactionRepository.findByPhoneNumber(anyString())).thenReturn(new ArrayList<>());
        
        //ASSERT
        ModelPaymentTransactionListDTO txList = paymentTransactionImpl.getTransactionsForCustomer(TestUtil.PHONE_2);
        Assert.assertNotNull(txList);
        Assert.assertNull(txList.getTransactions());
    }
    
    @Test
    public void getTransactionsForCustomerThenSuccess1() throws Exception {
        //MOCKITO
        Mockito.when(paymentTransactionRepository.findByPhoneNumber(anyString())).thenReturn(TestUtil.buildPaymentTransactionList());
        
        //ASSERT
        ModelPaymentTransactionListDTO txList = paymentTransactionImpl.getTransactionsForCustomer(TestUtil.PHONE_2);
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
        
        paymentTransactionImpl.refund(TestUtil.buildRefundRequestDTOBody());
    }
    
  @Test
  public void refundThenSuccess() throws Exception {
      //MOCKITO
      Mockito.when(paymentTransactionRepository.findByTransactionId(anyString())).thenReturn(Optional.of(TestUtil.buildPaymentTransactionList().get(0)));
      Mockito.when(externalApiService.executeRefund(anyString())).thenReturn(TestUtil.buildResponseModelPaymentTransactionDTOMock());
      
      boolean result = paymentTransactionImpl.refund(TestUtil.buildRefundRequestDTOBody());
      Assert.assertTrue(result);
  }
}
