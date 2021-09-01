package com.commerce.stream.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum StreamActionType {
	BROADCAST("broadcast","廣播Client"),
	TEST("test", "測試Biz"),
	TEST_REPLY("test_reply", "測試Biz回復"),
	NORMAL("normal", "正常Biz"),
	DEFAULT("default", "預設");

	private String code;
	private String desc;
	private static final Map<String, StreamActionType> MAP_STORE = new HashMap<>();

	private StreamActionType(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	static {
		Arrays.stream(values()).forEach(item -> MAP_STORE.put(item.getCode(), item));
	}

	public static StreamActionType getInstanceOf(String code) {
		return MAP_STORE.get(code);
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
}
