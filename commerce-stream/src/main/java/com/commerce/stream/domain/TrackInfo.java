package com.commerce.stream.domain;

import java.time.Instant;

public class TrackInfo {
	protected final Instant source;

	protected Instant destination;
	
	public TrackInfo() {
		super();
		this.source = Instant.now();
	}

	public TrackInfo(Instant source) {
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
