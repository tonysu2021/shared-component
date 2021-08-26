package com.commerce.cache.client;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class CacheManager<T> {
	
	private static Logger logger = LoggerFactory.getLogger(CacheManager.class);
	
	private ReactiveRedisTemplate<String, T> redisTemplate;
	
	private TransactionHelper helper;
	
	public CacheManager(ReactiveRedisTemplate<String, T> redisTemplate, TransactionHelper helper) {
		this.redisTemplate = redisTemplate;
		this.helper = helper;
	}

	/** 重試機制-使用背壓模式重試1次間隔500毫秒 */
	private Retry retry = Retry.backoff(1, Duration.ofMillis(500));

	public Mono<Boolean> save(String key, T data) {
		String cacheKey = generateKey(key, data.getClass());
		Mono<Boolean> processer = redisTemplate.opsForValue().set(cacheKey, data, Duration.ofMinutes(60))
				.retryWhen(retry).onErrorReturn(Boolean.FALSE)
				.log("[CacheManager][save]");
		return helper.doInTransaction(cacheKey, () -> processer);
	}

	public Mono<T> get(String key, Class<T> clz) {
		String cacheKey = generateKey(key, clz);
		Mono<T> processer = redisTemplate.opsForValue().get(cacheKey)
				.retryWhen(retry).onErrorResume(this::failback)
				.log("[CacheManager][get]");
		return helper.doInTransaction(cacheKey, () -> processer);
	}
	
	public Mono<Boolean> delete(String key, Class<T> clz) {
		String cacheKey = generateKey(key, clz);
		Mono<Boolean> processer = redisTemplate.opsForValue().delete(cacheKey)
				.retryWhen(retry).onErrorReturn(Boolean.FALSE)
				.log("[CacheManager][delete]");
		return helper.doInTransaction(cacheKey, () ->processer);
	}
	
	private Mono<T> failback(Throwable event) {
		logger.error(event.getMessage());
		return Mono.empty();
	}

	private String generateKey(String key, Class<?> clz) {
		return String.format("%s@-@%s", key, clz.getSimpleName());
	}
}
