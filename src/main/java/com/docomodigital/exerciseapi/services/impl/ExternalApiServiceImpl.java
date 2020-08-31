package com.docomodigital.exerciseapi.services.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.docomodigital.exerciseapi.common.annotations.Loggable;
import com.docomodigital.exerciseapi.common.exception.ApiException;
import com.docomodigital.exerciseapi.common.exception.ExceptionEnums;
import com.docomodigital.exerciseapi.mock.RequestModelMock;
import com.docomodigital.exerciseapi.mock.ResponseModelMock;
import com.docomodigital.exerciseapi.services.IExternalApiService;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionDTO;

@Service
public class ExternalApiServiceImpl implements IExternalApiService {

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${app.telecom-api.url:")
    private String url;
    
    @Override
    @Loggable
    public ModelPaymentTransactionDTO executePurchase(String phoneNumber, Double amount, String currency) throws ApiException {
        ModelPaymentTransactionDTO txDto = new ModelPaymentTransactionDTO();
        try {
            RequestModelMock request = new RequestModelMock();
            request.setAmount(amount);
            request.setCurrency(currency);
            request.setPhoneNumber(phoneNumber);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<RequestModelMock> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<ResponseModelMock> mockResponse = restTemplate.exchange(url, HttpMethod.POST, entity, ResponseModelMock.class);
            if(mockResponse.hasBody()) {
                txDto.setMsisdn(mockResponse.getBody().getMsisdn());
                txDto.setOrderId(mockResponse.getBody().getOrderId());
            }
            return txDto;
        } catch(RestClientException ex) {
            throw ExceptionEnums.EXTERNAL_API_EXCEPTION.get();
        } catch(Exception ex) {
            throw ex;
        }
    }

    @Override
    @Loggable
    public boolean executeRefund(String orderId) throws ApiException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<ResponseModelMock> mockResponse = restTemplate.exchange(url, HttpMethod.POST, entity, ResponseModelMock.class);
            return "SUCCESS".equals(mockResponse.getBody().getResult());
        } catch(RestClientException ex) {
            throw ExceptionEnums.EXTERNAL_API_EXCEPTION.get();
        } catch(Exception ex) {
            throw ex;
        }
    }
}
