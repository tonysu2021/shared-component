package com.commerce.stream.store;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.commerce.stream.constant.StatusCode;
import com.commerce.stream.constant.StreamActionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StreamTransactionHelper<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StreamTransactionHelper.class);

	private StreamStorage<StreamRecordPO> streamStorage;

	private ObjectMapper mapper;
	
	public StreamTransactionHelper(StreamStorage<StreamRecordPO> streamStorage, ObjectMapper mapper) {
		super();
		this.streamStorage = streamStorage;
		this.mapper = mapper;
	}
	
	public void doInTransaction(String target,UUID messageId, StreamActionType type, int attempt, T payload,
			Consumer<T> consumer) {

		LOGGER.info("Received Message id :{} , type : {} , body: {} from Client.", messageId, type,
				payload);

		String msgId = messageId.toString();
		String cacheKey = generateKey(target, msgId, type.getCode());
		StreamRecordPO oldRecord = streamStorage.get(cacheKey, StreamRecordPO.class);

		if (oldRecord != null && StatusCode.SUCCESS == oldRecord.getStatus()) {
			LOGGER.info("Repeated messages were found. ");
			return;
		}
		if (oldRecord != null && 3 == oldRecord.getRetryTime()) {
			LOGGER.info("Message has exceeded the number of times it was processed. ");
			return;
		}

		StreamRecordPO newRecord = oldRecord != null ? updateOldRecord(oldRecord, attempt)
				: createNewRecord(messageId, attempt, payload);
		try {
			consumer.accept(payload);	
			newRecord.setStatus(StatusCode.SUCCESS);
		} catch (Exception ex) {
			newRecord.setStatus(StatusCode.FAILURE);
			throw ex;
		} finally {
			streamStorage.save(cacheKey, newRecord);
		}
	}

	private String generateKey(String target, String msgId, String type) {
		return String.format("%s-%s-%s",target, msgId, type);
	}

	private StreamRecordPO updateOldRecord(StreamRecordPO oldRecord, int attempt) {
		oldRecord.setRetryTime(attempt);
		oldRecord.setModifyTime(Instant.now());
		return oldRecord;
	}

	private StreamRecordPO createNewRecord(UUID messageId, int attempt, T payload) {
		StreamRecordPO newRecord = new StreamRecordPO();
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
