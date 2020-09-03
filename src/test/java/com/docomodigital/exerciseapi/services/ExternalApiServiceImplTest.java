package com.docomodigital.exerciseapi.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Collections;

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
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.docomodigital.exerciseapi.ApiExceptionMatcher;
import com.docomodigital.exerciseapi.common.exception.ExceptionEnums;
import com.docomodigital.exerciseapi.mock.ResponseModelMock;
import com.docomodigital.exerciseapi.mock.ResponseWrapper;
import com.docomodigital.exerciseapi.services.impl.ExternalApiServiceImpl;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionDTO;

@RunWith(PowerMockRunner.class)
@SuppressWarnings("unchecked")
public class ExternalApiServiceImplTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    ExternalApiServiceImpl externalApiServiceImpl;

    private static final String PHONE = "+39000000000";
    private static final Double AMOUNT = 1.0;
    private static final String CURRENCY = "EUR";
    private static final String ORDER_ID = "XXDRI980234K";
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void executePurchaseThenThrowException() throws Exception {
        //MOCKITO
        Mockito.when(restTemplate.exchange(anyString(), 
                any(HttpMethod.class), 
                any(HttpEntity.class), 
                any(ParameterizedTypeReference.class)))
        .thenThrow(new RuntimeException());
        thrown.expect(RuntimeException.class);
        
        externalApiServiceImpl.executePurchase(PHONE, AMOUNT, CURRENCY);
    }
    
    @Test
    public void executePurchaseThenThrowRestClientException() throws Exception {
        //MOCKITO
        Mockito.when(restTemplate.exchange(anyString(), 
                any(HttpMethod.class), 
                any(HttpEntity.class), 
                any(ParameterizedTypeReference.class)))
        .thenThrow(new RestClientException(""));
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.EXTERNAL_API_EXCEPTION));
        
        externalApiServiceImpl.executePurchase(PHONE, AMOUNT, CURRENCY);
    }
    
    @Test
    public void executePurchaseThenSUCCESS() throws Exception {
        // GIVEN
        ResponseModelMock entity = new ResponseModelMock();
        entity.setResult("SUCCESS");
        ResponseWrapper<ResponseModelMock> mock = new ResponseWrapper<>(entity);
        
        //MOCKITO
        Mockito.when(restTemplate.exchange(anyString(), 
                any(HttpMethod.class), 
                any(HttpEntity.class), 
                any(ParameterizedTypeReference.class)))
        .thenReturn(new ResponseEntity<>(mock, HttpStatus.OK));
        
        //EVALUATE
        ModelPaymentTransactionDTO response = externalApiServiceImpl.executePurchase(PHONE, AMOUNT, CURRENCY);
        Assert.assertNotNull(response);
        Assert.assertEquals("SUCCESS", response.getStatus());
    }
    
    @Test
    public void executePurchaseThenSUCCESS1() throws Exception {
        // GIVEN
        ResponseWrapper<ResponseModelMock> mock = new ResponseWrapper<>();
        mock.setErrors(Collections.singletonList("Error text."));
        
        //MOCKITO
        Mockito.when(restTemplate.exchange(anyString(), 
                any(HttpMethod.class), 
                any(HttpEntity.class), 
                any(ParameterizedTypeReference.class)))
        .thenReturn(new ResponseEntity<>(mock, HttpStatus.OK));
        
        //EVALUATE
        ModelPaymentTransactionDTO response = externalApiServiceImpl.executePurchase(PHONE, AMOUNT, CURRENCY);
        Assert.assertNotNull(response);
        Assert.assertEquals("FAIL", response.getStatus());
    }
    
    @Test
    public void executePurchaseThenSUCCESS2() throws Exception {
        //MOCKITO
        Mockito.when(restTemplate.exchange(anyString(), 
                any(HttpMethod.class), 
                any(HttpEntity.class), 
                any(ParameterizedTypeReference.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        
        //EVALUATE
        ModelPaymentTransactionDTO response = externalApiServiceImpl.executePurchase(PHONE, AMOUNT, CURRENCY);
        Assert.assertNotNull(response);
        Assert.assertEquals("UNSURE", response.getStatus());
    }
    
    @Test
    public void executeRefundThenThrowException() throws Exception {
        //MOCKITO
        Mockito.when(restTemplate.exchange(anyString(), 
                any(HttpMethod.class), 
                any(HttpEntity.class), 
                any(ParameterizedTypeReference.class)))
        .thenThrow(new RuntimeException());
        thrown.expect(RuntimeException.class);
        
        externalApiServiceImpl.executeRefund(ORDER_ID);
    }
    
    @Test
    public void executeRefundThenThrowRestClientException() throws Exception {
        //MOCKITO
        Mockito.when(restTemplate.exchange(anyString(), 
                any(HttpMethod.class), 
                any(HttpEntity.class), 
                any(ParameterizedTypeReference.class)))
        .thenThrow(new RestClientException(""));
        thrown.expect(ApiExceptionMatcher.is(ExceptionEnums.EXTERNAL_API_EXCEPTION));
        
        externalApiServiceImpl.executeRefund(ORDER_ID);
    }
    
    @Test
    public void executeRefundThenSUCCESS() throws Exception {
        // GIVEN
        ResponseModelMock entity = new ResponseModelMock();
        entity.setResult("SUCCESS");
        ResponseWrapper<ResponseModelMock> mock = new ResponseWrapper<>(entity);
        
        //MOCKITO
        Mockito.when(restTemplate.exchange(anyString(), 
                any(HttpMethod.class), 
                any(HttpEntity.class), 
                any(ParameterizedTypeReference.class)))
        .thenReturn(new ResponseEntity<>(mock, HttpStatus.OK));
        
        //EVALUATE
        ModelPaymentTransactionDTO response = externalApiServiceImpl.executeRefund(ORDER_ID);
        Assert.assertNotNull(response);
    }
    
    @Test
    public void executeRefundThenSUCCESS1() throws Exception {
        // GIVEN
        ResponseWrapper<ResponseModelMock> mock = new ResponseWrapper<>();
        mock.setErrors(Collections.singletonList("Error text."));
        
        //MOCKITO
        Mockito.when(restTemplate.exchange(anyString(), 
                any(HttpMethod.class), 
                any(HttpEntity.class), 
                any(ParameterizedTypeReference.class)))
        .thenReturn(new ResponseEntity<>(mock, HttpStatus.OK));
        
        //EVALUATE
        ModelPaymentTransactionDTO response = externalApiServiceImpl.executeRefund(ORDER_ID);
        Assert.assertNotNull(response);
    }
    
    @Test
    public void executeRefundThenSUCCESS2() throws Exception {
        //MOCKITO
        Mockito.when(restTemplate.exchange(anyString(), 
                any(HttpMethod.class), 
                any(HttpEntity.class), 
                any(ParameterizedTypeReference.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        
        //EVALUATE
        ModelPaymentTransactionDTO response = externalApiServiceImpl.executeRefund(ORDER_ID);
        Assert.assertNotNull(response);
        Assert.assertEquals("UNSURE", response.getStatus());
    }
}
