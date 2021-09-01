package com.commerce.stream.dto;

import java.time.Instant;

public class StreamMessageDTO<T> {
	
	private final TrackInfoDTO trackInfo;

	private T message;
	
	public StreamMessageDTO() {
		super();
		this.trackInfo = new TrackInfoDTO(Instant.now());
	}
	
	public StreamMessageDTO(TrackInfoDTO trackInfo) {
		super();
		this.trackInfo = trackInfo;
	}

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}

	public TrackInfoDTO getTrackInfo() {
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
