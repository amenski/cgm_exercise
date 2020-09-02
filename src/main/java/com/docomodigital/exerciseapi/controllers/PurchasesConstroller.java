package com.docomodigital.exerciseapi.controllers;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.docomodigital.exerciseapi.common.annotations.Loggable;
import com.docomodigital.exerciseapi.common.exception.ApiException;
import com.docomodigital.exerciseapi.services.IPaymentTransaction;
import com.docomodigital.exerciseapi.swagger.apis.PurchasesApi;
import com.docomodigital.exerciseapi.swagger.dtos.ModelPaymentTransactionListDTO;
import com.docomodigital.exerciseapi.swagger.dtos.PurchaseSaveRequestDTO;
import com.docomodigital.exerciseapi.swagger.models.ModelPaymentTransactionList;
import com.docomodigital.exerciseapi.swagger.models.PurchaseSaveRequest;
import com.docomodigital.exerciseapi.swagger.models.ResponseBase;
import com.docomodigital.exerciseapi.swagger.models.ResponseModelPaymentTransactionList;

import io.swagger.annotations.ApiParam;

@RestController
public class PurchasesConstroller extends AbstractController implements PurchasesApi {

    @Autowired
    private IPaymentTransaction paymentTransactionService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Loggable
    public ResponseEntity<ResponseModelPaymentTransactionList> getAllTransactions(
            @ApiParam(value = "",required=true) @PathVariable("phone-number") String phoneNumber) 
    {
        Class<ResponseModelPaymentTransactionList> responseClass = ResponseModelPaymentTransactionList.class;
        ResponseModelPaymentTransactionList response = null;
        HttpStatus status = HttpStatus.OK;
        try {
            ModelPaymentTransactionListDTO txDtoList = paymentTransactionService.getTransactionsForCustomer(phoneNumber);
            ModelPaymentTransactionList txList = mapper.map(txDtoList, ModelPaymentTransactionList.class);
            response = fillSuccessResponse(new ResponseModelPaymentTransactionList().returnValue(txList));
        } catch (ApiException ex) {
            status = HttpStatus.valueOf(ex.getHttpCode());
            response = fillFailResponseApiException(responseClass, ex);
        } catch (Exception ex) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response = fillFailResponseGeneric(responseClass);
        }

        return new ResponseEntity<>(response, status);
    }

    @Override
    @Loggable
    public ResponseEntity<ResponseBase> purchaseProduct(
            @ApiParam(value = "") @Valid @RequestBody PurchaseSaveRequest body) {
        Class<ResponseBase> responseClass = ResponseBase.class;
        ResponseBase response = null;
        HttpStatus status = HttpStatus.OK;
        try {
            PurchaseSaveRequestDTO dtoBody = mapper.map(body, PurchaseSaveRequestDTO.class);
            paymentTransactionService.purchase(dtoBody);
            response = fillSuccessResponse(new ResponseBase());
        } catch (ApiException ex) {
            status = HttpStatus.valueOf(ex.getHttpCode());
            response = fillFailResponseApiException(responseClass, ex);
        } catch (Exception ex) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response = fillFailResponseGeneric(responseClass);
        }

        return new ResponseEntity<>(response, status);
    }

    @Override
    public ResponseEntity<ResponseBase> refundPayment(
            @ApiParam(value = "" ,required=true )  @Valid @RequestBody String transactionId) {
        Class<ResponseBase> responseClass = ResponseBase.class;
        ResponseBase response = null;
        HttpStatus status = HttpStatus.OK;
        try {
            paymentTransactionService.refund(transactionId);
            response = fillSuccessResponse(new ResponseBase());
        } catch (ApiException ex) {
            status = HttpStatus.valueOf(ex.getHttpCode());
            response = fillFailResponseApiException(responseClass, ex);
        } catch (Exception ex) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response = fillFailResponseGeneric(responseClass);
        }

        return new ResponseEntity<>(response, status);
    }

}
