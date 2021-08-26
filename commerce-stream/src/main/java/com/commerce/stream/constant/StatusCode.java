package com.commerce.stream.constant;

import java.util.HashMap;
import java.util.Map;

public enum StatusCode implements Labeled<Integer> {
	FAILURE(0, "失敗"), SUCCESS(1, "成功");

	private static final Map<Integer, StatusCode> BY_LABEL = new HashMap<>();

	static {
		for (StatusCode element : values()) {
			BY_LABEL.put(element.label(), element);
		}
	}

	StatusCode(final int code, final String message) {
		this.code = code;
		this.message = message;
	}

	private int code;

	private String message;

	public static StatusCode valueOfCode(int number) {
		return BY_LABEL.get(number);
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return message;
	}

	@Override
	public Integer label() {
		return getCode();
	}
}
