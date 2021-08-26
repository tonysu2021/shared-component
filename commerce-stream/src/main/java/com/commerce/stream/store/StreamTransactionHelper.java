package com.commerce.stream.store;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.commerce.stream.constant.StatusCode;
import com.commerce.stream.domain.StreamActionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StreamTransactionHelper<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StreamTransactionHelper.class);

	private StreamStores<StreamRecord> streamStores;

	private ObjectMapper mapper;
	
	public StreamTransactionHelper(StreamStores<StreamRecord> streamStores, ObjectMapper mapper) {
		super();
		this.streamStores = streamStores;
		this.mapper = mapper;
	}
	
	public void doInTransaction(String target,UUID messageId, StreamActionType type, int attempt, T payload,
			Consumer<T> consumer) {

		LOGGER.info("Received Message id :{} , type : {} , body: {} from Client.", messageId, type,
				payload);

		String msgId = messageId.toString();
		String cacheKey = generateKey(target, msgId, type.getCode());
		StreamRecord oldRecord = streamStores.get(cacheKey, StreamRecord.class);

		if (oldRecord != null && StatusCode.SUCCESS == oldRecord.getStatus()) {
			LOGGER.info("Repeated messages were found. ");
			return;
		}
		if (oldRecord != null && 3 == oldRecord.getRetryTime()) {
			LOGGER.info("Message has exceeded the number of times it was processed. ");
			return;
		}

		StreamRecord newRecord = oldRecord != null ? updateOldRecord(oldRecord, attempt)
				: createNewRecord(messageId, attempt, payload);
		try {
			consumer.accept(payload);	
			newRecord.setStatus(StatusCode.SUCCESS);
		} catch (Exception ex) {
			newRecord.setStatus(StatusCode.FAILURE);
			throw ex;
		} finally {
			streamStores.save(cacheKey, newRecord);
		}
	}

	private String generateKey(String target, String msgId, String type) {
		return String.format("%s-%s-%s",target, msgId, type);
	}

	private StreamRecord updateOldRecord(StreamRecord oldRecord, int attempt) {
		oldRecord.setRetryTime(attempt);
		oldRecord.setModifyTime(Instant.now());
		return oldRecord;
	}

	private StreamRecord createNewRecord(UUID messageId, int attempt, T payload) {
		StreamRecord newRecord = new StreamRecord();
		newRecord.setMessageId(messageId);
		try {
			newRecord.setMessage(mapper.writeValueAsString(payload));
		} catch (JsonProcessingException e) {
			LOGGER.error("Message id :{} payload cannot be converted ", messageId);
		}
		newRecord.setCreateTime(Instant.now());
		newRecord.setRetryTime(attempt);
		return newRecord;
	}
}
