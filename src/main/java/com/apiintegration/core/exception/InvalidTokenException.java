package com.apiintegration.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidTokenException extends RuntimeException {
	
	public InvalidTokenException(String msg) {
		super(msg);
	}
	
	public InvalidTokenException(String msg , Throwable error) {
		super(msg , error);
	}
	
	public InvalidTokenException() {
		super();
	}

}
