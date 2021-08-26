package com.commerce.stream.domain;

import java.time.Instant;

public class StreamMessage<T> {
	
	private final TrackInfo trackInfo;

	private T message;
	
	public StreamMessage() {
		super();
		this.trackInfo = new TrackInfo(Instant.now());
	}
	
	public StreamMessage(TrackInfo trackInfo) {
		super();
		this.trackInfo = trackInfo;
	}

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}

	public TrackInfo getTrackInfo() {
		return trackInfo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StreamMessage [message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}
}
