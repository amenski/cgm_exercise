package com.docomodigital.exerciseapi.common.exception;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ApiException extends Exception {
	private static final long serialVersionUID = -5464526854597284442L;

	private int httpCode;
	private int errorCode;
	private String message;
	private List<String> errors;
	
	public ApiException(String message) {
		super();
		this.message = message;
	}

	public ApiException(int httpCode, int internalCode, String message) {
		super();
		this.httpCode = httpCode;
		this.errorCode = internalCode;
		this.message = message;
	}
	
	public ApiException(int httpCode, String message, List<String> errors) {
		super();
		this.httpCode = httpCode;
		this.message = message;
		this.errors = errors;
	}

	public ApiException(int httpCode, int internalCode, String message, List<String> errors) {
		super();
		this.httpCode = httpCode;
		this.errorCode = internalCode;
		this.message = message;
		this.errors = errors;
	}
	
	public int getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getErrors() {
		if(errors == null)
			return new ArrayList<>();
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public ApiException message(String newMessage) {
		if(!StringUtils.isBlank(newMessage)) {
			this.message = newMessage;
		}
		return this;
	}
}
