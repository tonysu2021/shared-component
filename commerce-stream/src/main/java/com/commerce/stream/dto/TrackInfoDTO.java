package com.commerce.stream.dto;

import java.time.Instant;

public class TrackInfoDTO {
	protected final Instant source;

	protected Instant destination;
	
	public TrackInfoDTO() {
		super();
		this.source = Instant.now();
	}

	public TrackInfoDTO(Instant source) {
		super();
		this.source = source;
	}

	public Instant getSource() {
		return source;
	}

	public Instant getDestination() {
		return destination;
	}

	public void setDestination(Instant destination) {
		this.destination = destination;
	}
}
