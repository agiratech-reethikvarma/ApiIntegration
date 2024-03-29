package com.apiintegration.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
@NoArgsConstructor
public class InvalidUrlException extends RuntimeException {

	private static final long serialVersionUID = -5419641420488819789L;

	public InvalidUrlException(String msg) {
		super(msg);
	}
}