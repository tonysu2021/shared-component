package com.commerce.stream.exception;

import org.apache.commons.lang3.exception.ContextedRuntimeException;

public class StreamException extends ContextedRuntimeException {
	private static final long serialVersionUID = 1L;

	public StreamException(ExceptionCodeEnum exceptionCode) {
		super(exceptionCode.getMsg());
	}
}
