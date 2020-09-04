package com.docomodigital.exerciseapi.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.docomodigital.exerciseapi.common.exception.ApiException;
import com.docomodigital.exerciseapi.common.exception.ExceptionEnums;
import com.docomodigital.exerciseapi.swagger.models.ResponseBase;

public abstract class AbstractController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings("unchecked")
	public <T extends ResponseBase> T fillSuccessResponse(T response) {
		 if(response == null){
			 response = (T) getNewInstance(response.getClass());
		 }
		response.success(true);
		response.resultCode(HttpStatus.OK.value());
		response.message(response.getMessage());
		response.errors(null);

		return response;
	}

	public <T extends ResponseBase> T fillFailResponseApiException(Class<T> response, ApiException e){
		T res = getNewInstance(response);
		res.success(false);
		res.resultCode(e.getErrorCode());
		res.message(e.getMessage());
		res.errors(e.getErrors());
		
		return res;
	}
	
	public <T extends ResponseBase> T fillFailResponseGeneric(Class<T> responseClass){
		T response = getNewInstance(responseClass);
		response.success(false);
		response.resultCode(ExceptionEnums.UNHANDLED_EXCEPTION.get().getErrorCode());
		response.message(ExceptionEnums.UNHANDLED_EXCEPTION.get().getMessage());
		response.errors(null);
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getNewInstance(Class<T> clazz){
		T newInstance;
		try {
			newInstance = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			newInstance = (T) new ResponseBase();
		}
		return newInstance;
	}
}
