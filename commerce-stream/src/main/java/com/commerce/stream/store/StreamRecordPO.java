package com.commerce.stream.store;

import java.time.Instant;
import java.util.UUID;

import com.commerce.stream.constant.StatusCode;

public class StreamRecordPO {

	private UUID messageId;

	private String message;

	private Integer retryTime;

	private StatusCode status;

	private Instant createTime;

	private Instant modifyTime;

	public UUID getMessageId() {
		return messageId;
	}

	public void setMessageId(UUID messageId) {
		this.messageId = messageId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getRetryTime() {
		return retryTime;
	}

	public void setRetryTime(Integer retryTime) {
		this.retryTime = retryTime;
	}

	public StatusCode getStatus() {
		return status;
	}

	public void setStatus(StatusCode status) {
		this.status = status;
	}

	public Instant getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Instant createTime) {
		this.createTime = createTime;
	}

	public Instant getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Instant modifyTime) {
		this.modifyTime = modifyTime;
	}

}
