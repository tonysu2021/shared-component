package com.commerce.stream.exception;

public enum ExceptionCodeEnum {
	DOMAIN_ERROR("Domain is error");

	ExceptionCodeEnum(final String message) {
		this.message = message;
	}

	private String message;

	public String getMsg() {
		return message;
	}
}
