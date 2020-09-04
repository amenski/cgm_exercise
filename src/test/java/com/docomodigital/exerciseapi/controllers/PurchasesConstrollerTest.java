package com.docomodigital.exerciseapi.controllers;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.docomodigital.exerciseapi.TestUtil;
import com.docomodigital.exerciseapi.common.exception.ExceptionEnums;
import com.docomodigital.exerciseapi.services.impl.PaymentTransactionImpl;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionListDTO;
import com.docomodigital.exerciseapi.swagger.dtos.PurchaseSaveRequestDTO;
import com.docomodigital.exerciseapi.swagger.dtos.RefundRequestDTO;
import com.docomodigital.exerciseapi.swagger.models.PurchaseSaveRequest;
import com.docomodigital.exerciseapi.swagger.models.RefundRequest;
import com.docomodigital.exerciseapi.swagger.models.ResponseBase;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(PowerMockRunner.class)
public class PurchasesConstrollerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MockMvc mockMvc;

    @Spy
    private ModelMapper mapper = new ModelMapper();

    @Mock
    private HttpServletResponse httpResponse;

    private ObjectMapper objMapper;

    @Mock
    private PaymentTransactionImpl paymentTransactionServiceImpl;
    
    @InjectMocks
    PurchasesConstroller purchasesConstroller;

    private JacksonTester<PurchaseSaveRequest> purchaseSaveRequest;
    private JacksonTester<RefundRequest> refundRequest;

    private static final String PHONE = "+39000000000";
    private static final String CURRENCY = "EUR";
    private static final String ORDER_ID = "XXDRI980234K";
    private static final String PATH_PURCHASE = "/v1/purchase";
    private static final String PATH_PURCHASES = "/v1/purchases/%s";

    @Before
    public void setup() {
        objMapper = new ObjectMapper();
        objMapper.findAndRegisterModules();
        JacksonTester.initFields(this, objMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(purchasesConstroller).build();
    }

    @Test
    public void getAllTransactionsThenThrowGenericException() throws Exception {
        // MOCKITO
        Mockito.when(paymentTransactionServiceImpl.getTransactionsForCustomer(anyString())).thenThrow(new RuntimeException(""));
        
        MockHttpServletResponse response = mockMvc.perform(get(String.format(PATH_PURCHASES, PHONE))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ResponseBase responseContent = objMapper.readValue(response.getContentAsString(), ResponseBase.class);

        // ASSERTS
        assertNotNull(response);
        AssertTestHelper.checkForGenericException(responseContent);
    }
    
    @Test
    public void getAllTransactionsThenThrowApiException() throws Exception {
        // MOCKITO
        Mockito.when(paymentTransactionServiceImpl.getTransactionsForCustomer(anyString())).thenThrow( ExceptionEnums.UNHANDLED_EXCEPTION.get());
        
        MockHttpServletResponse response = mockMvc.perform(get(String.format(PATH_PURCHASES, PHONE))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        
        ResponseBase responseContent = objMapper.readValue(response.getContentAsString(), ResponseBase.class);
        
        // ASSERTS
        assertNotNull(response);
        AssertTestHelper.checkForApiException(responseContent, ExceptionEnums.UNHANDLED_EXCEPTION);
    }
    
    @Test
    public void getAllTransactionsThenSuccess() throws Exception {
        // MOCKITO
        Mockito.when(paymentTransactionServiceImpl.getTransactionsForCustomer(anyString())).thenReturn(new ModelPaymentTransactionListDTO());
        
        MockHttpServletResponse response = mockMvc.perform(get(String.format(PATH_PURCHASES, PHONE))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        
        ResponseBase responseContent = objMapper.readValue(response.getContentAsString(), ResponseBase.class);
        
        // ASSERTS
        AssertTestHelper.checkForSucces(responseContent);
    }
    
    @Test
    public void purchaseProductThenThrowGenericException() throws Exception {
        // MOCKITO
        Mockito.when(paymentTransactionServiceImpl.purchase(any(PurchaseSaveRequestDTO.class))).thenThrow(new RuntimeException(""));
        
        MockHttpServletResponse response = mockMvc.perform(post(PATH_PURCHASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(purchaseSaveRequest.write(TestUtil.buildPurchaseRequestBody()).getJson()))
                .andReturn().getResponse();

        ResponseBase responseContent = objMapper.readValue(response.getContentAsString(), ResponseBase.class);

        // ASSERTS
        assertNotNull(response);
        AssertTestHelper.checkForGenericException(responseContent);
    }
    
    @Test
    public void purchaseProductThenThrowApiException() throws Exception {
        // MOCKITO
        Mockito.when(paymentTransactionServiceImpl.purchase(any(PurchaseSaveRequestDTO.class))).thenThrow( ExceptionEnums.UNHANDLED_EXCEPTION.get());
        
        MockHttpServletResponse response = mockMvc.perform(post(PATH_PURCHASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(purchaseSaveRequest.write(TestUtil.buildPurchaseRequestBody()).getJson()))
                .andReturn().getResponse();
        
        ResponseBase responseContent = objMapper.readValue(response.getContentAsString(), ResponseBase.class);
        
        // ASSERTS
        assertNotNull(response);
        AssertTestHelper.checkForApiException(responseContent, ExceptionEnums.UNHANDLED_EXCEPTION);
    }
    
    @Test
    public void purchaseProductThenSuccess() throws Exception {
        // MOCKITO
        Mockito.when(paymentTransactionServiceImpl.purchase(any(PurchaseSaveRequestDTO.class))).thenReturn(true);
        
        MockHttpServletResponse response = mockMvc.perform(post(PATH_PURCHASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(purchaseSaveRequest.write(TestUtil.buildPurchaseRequestBody()).getJson()))
                .andReturn().getResponse();
        
        ResponseBase responseContent = objMapper.readValue(response.getContentAsString(), ResponseBase.class);
        
        // ASSERTS
        AssertTestHelper.checkForSucces(responseContent);
    }

     @Test
    public void refundPaymentThenThrowGenericException() throws Exception {
        // MOCKITO
        Mockito.when(paymentTransactionServiceImpl.refund(any(RefundRequestDTO.class))).thenThrow(new RuntimeException(""));
        
        MockHttpServletResponse response = mockMvc.perform(post(String.format(PATH_PURCHASES, "refund"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(refundRequest.write(TestUtil.buildRefundRequestBody()).getJson()))
                .andReturn().getResponse();

        ResponseBase responseContent = objMapper.readValue(response.getContentAsString(), ResponseBase.class);

        // ASSERTS
        assertNotNull(response);
        AssertTestHelper.checkForGenericException(responseContent);
    }
    
    @Test
    public void refundPaymentThenThrowApiException() throws Exception {
        // MOCKITO
        Mockito.when(paymentTransactionServiceImpl.refund(any(RefundRequestDTO.class))).thenThrow( ExceptionEnums.UNHANDLED_EXCEPTION.get());
        
        MockHttpServletResponse response = mockMvc.perform(post(String.format(PATH_PURCHASES, "refund"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(refundRequest.write(TestUtil.buildRefundRequestBody()).getJson()))
                .andReturn().getResponse();
        
        ResponseBase responseContent = objMapper.readValue(response.getContentAsString(), ResponseBase.class);
        
        // ASSERTS
        assertNotNull(response);
        AssertTestHelper.checkForApiException(responseContent, ExceptionEnums.UNHANDLED_EXCEPTION);
    }
    
    @Test
    public void refundPaymentThenSuccess() throws Exception {
        // MOCKITO
        Mockito.when(paymentTransactionServiceImpl.refund(any(RefundRequestDTO.class))).thenReturn(true);
        
        MockHttpServletResponse response = mockMvc.perform(post(String.format(PATH_PURCHASES, "refund"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(refundRequest.write(TestUtil.buildRefundRequestBody()).getJson()))
                .andReturn().getResponse();

        
        ResponseBase responseContent = objMapper.readValue(response.getContentAsString(), ResponseBase.class);
        
        // ASSERTS
        AssertTestHelper.checkForSucces(responseContent);
    }
}
