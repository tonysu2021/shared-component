package com.commerce.stream.protocol;

public class LiveChannel {

	private LiveChannel() {
		// Do nothing
	}

	/** 直播-websocket */
	public static final String WS_BROADCAST_INPUT = "ws-broadcast-in";

	public static final String WS_BROADCAST_OUTPUT = "ws-broadcast-out";
	
	/** 直播-Event事件對列 */
	public static final String EVENT_BROADCAST_INPUT = "event-broadcast-in";

	public static final String EVENT_BROADCAST_OUTPUT = "event-broadcast-out";

}
