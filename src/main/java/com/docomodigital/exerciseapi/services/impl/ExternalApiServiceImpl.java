package com.docomodigital.exerciseapi.services.impl;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
import com.docomodigital.exerciseapi.common.utils.ApiConstants;
import com.docomodigital.exerciseapi.mock.RequestModelMock;
import com.docomodigital.exerciseapi.mock.ResponseModelMock;
import com.docomodigital.exerciseapi.mock.ResponseWrapper;
import com.docomodigital.exerciseapi.services.IExternalApiService;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionDTO;

@Service
public class ExternalApiServiceImpl implements IExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.telecom-api.url:}")
    private String url;

    @Override
    @Loggable
    public ModelPaymentTransactionDTO executePurchase(String phoneNumber, Double amount, String currency)
            throws ApiException {
        ModelPaymentTransactionDTO txDto = new ModelPaymentTransactionDTO();
        try {
            RequestModelMock request = new RequestModelMock();
            request.setAmount(amount);
            request.setCurrency(currency);
            request.setPhoneNumber(phoneNumber);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RequestModelMock> entity = new HttpEntity<>(request, headers);

            ResponseEntity<ResponseWrapper<ResponseModelMock>> mockResponse = restTemplate.exchange(String.join("/", url, "purchase"),
                    HttpMethod.POST, 
                    entity, 
                    new ParameterizedTypeReference<ResponseWrapper<ResponseModelMock>>() {});
            ResponseWrapper<ResponseModelMock> mockResponseBody = mockResponse.getBody();
            if (mockResponseBody != null) {
                if (mockResponseBody.isSuccess()) {
                    txDto.setMsisdn(mockResponseBody.getEntityResult().getMsisdn());
                    txDto.setOrderId(mockResponseBody.getEntityResult().getOrderId());
                    txDto.setStatus(ApiConstants.TX_STATUS.SUCCESS.name());
                } else if (!mockResponseBody.getErrors().isEmpty()) {
                    txDto.setStatus(ApiConstants.TX_STATUS.FAIL.name());
                    txDto.setFailureMessage(mockResponseBody.getErrors().stream().map(Object::toString).collect(Collectors.joining("  ||  ")));
                }
            }
            return txDto;
        } catch (RestClientException ex) {
            throw ExceptionEnums.EXTERNAL_API_EXCEPTION.get();
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    @Loggable
    public ModelPaymentTransactionDTO executeRefund(String orderId) throws ApiException {
        try {
            ModelPaymentTransactionDTO response = new ModelPaymentTransactionDTO();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<>(orderId, headers);

            ResponseEntity<ResponseWrapper<ResponseModelMock>> mockResponse = restTemplate.exchange(String.join("/", url, "refund"),
                    HttpMethod.POST, 
                    entity, 
                    new ParameterizedTypeReference<ResponseWrapper<ResponseModelMock>>() {});
            ResponseWrapper<ResponseModelMock> mockResponseBody = mockResponse.getBody();
            response.setStatus(ApiConstants.TX_STATUS.UNSURE.name());
            if (mockResponseBody != null) {
                    response.setStatus(mockResponseBody.isSuccess() ? ApiConstants.TX_STATUS.SUCCESS.name() : ApiConstants.TX_STATUS.FAIL.name());
                    response.setFailureMessage(mockResponseBody.getErrors().stream().map(Object::toString).collect(Collectors.joining("  ||  ")));
            }
            return response;
        } catch (RestClientException ex) {
            throw ExceptionEnums.EXTERNAL_API_EXCEPTION.get();
        } catch (Exception ex) {
            throw ex;
        }
    }
}
