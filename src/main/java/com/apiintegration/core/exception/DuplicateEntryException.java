package com.apiintegration.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED)
public class DuplicateEntryException extends RuntimeException {

	private static final long serialVersionUID = 8330252806059654993L;

	public DuplicateEntryException(String message) {
		super(message);
	}
}