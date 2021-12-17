package com.commerce.cache.client;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.CollectionUtils;

import reactor.core.publisher.Flux;
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
	
	public Flux<String> keys(String patten) {
		return redisTemplate.keys(patten);
	}
	
	public Mono<Long> delete(List<String> keys){
		if (CollectionUtils.isEmpty(keys)) {
			return Mono.just(0L);
		}
		String[] array = keys.toArray(new String[keys.size()]);
		return redisTemplate.delete(array);
	}
	
	/** 處理Redis資料結構-字串(String) */
	public Mono<Boolean> save(String key, T value) {
		String cacheKey = generateKey(key, value.getClass());
		Mono<Boolean> processer = redisTemplate.opsForValue().set(cacheKey, value, Duration.ofMinutes(60))
				.retryWhen(retry).onErrorReturn(Boolean.FALSE).log("[CacheManager][save]");
		return helper.doInTransaction(cacheKey, () -> processer);
	}

	public Mono<T> get(String key, Class<T> clz) {
		String cacheKey = generateKey(key, clz);
		Mono<T> processer = redisTemplate.opsForValue().get(cacheKey).retryWhen(retry).onErrorResume(this::failback)
				.log("[CacheManager][get]");
		return helper.doInTransaction(cacheKey, () -> processer);
	}

	public Mono<Boolean> delete(String key, Class<T> clz) {
		String cacheKey = generateKey(key, clz);
		Mono<Boolean> processer = redisTemplate.opsForValue().delete(cacheKey).retryWhen(retry)
				.onErrorReturn(Boolean.FALSE).log("[CacheManager][delete]");
		return helper.doInTransaction(cacheKey, () -> processer);
	}

	/** 處理Redis資料結構-列表(List) */
	public Mono<Long> rightPushByList(String key, T message) {
		String cacheKey = generateKey(key, message.getClass());
		Mono<Long> processer = redisTemplate.opsForList().rightPush(cacheKey, message);
		return helper.doInTransaction(cacheKey, () -> processer);
	}

	public Mono<List<T>> rangeByList(String key, Class<T> clz) {
		String cacheKey = generateKey(key, clz);
		Mono<List<T>> processer = redisTemplate.opsForList().range(cacheKey, 0, -1).collectList();
		return helper.doInTransaction(cacheKey, () -> processer);
	}

	/** 處理Redis資料結構-哈希(Hash) */
	public Mono<Boolean> putByHash(String key, String hashKey, T value) {
		return redisTemplate.<String, T>opsForHash().put(key, hashKey, value);
	}

	public Mono<T> getByHash(String key, String hashKey) {
		return redisTemplate.<String, T>opsForHash().get(key, hashKey);
	}

	public Mono<Long> sizeByHash(String key) {
		return redisTemplate.opsForHash().size(key);
	}

	public Mono<Boolean> deleteByHash(String key) {
		return redisTemplate.<String, T>opsForHash().delete(key);
	}

	public Mono<Long> removeByHash(String key, String hashKey) {
		return redisTemplate.<String, T>opsForHash().remove(key, hashKey);
	}

	public Flux<Map.Entry<String, T>> entriesByHash(String key) {
		return redisTemplate.<String, T>opsForHash().entries(key);
	}

	public Mono<Boolean> hasKey(String key){
		return 	redisTemplate.hasKey(key);
	}

	/** 處理Redis資料結構-集合(set) Note:去重 */
	@SuppressWarnings("unchecked") 
	public Mono<Long> addBySet(String key, T... values) {
		return redisTemplate.opsForSet().add(key, values);
	}
	
	public Mono<Long> addArrayBySet(String key, T[] values) {
		return redisTemplate.opsForSet().add(key, values);
	}
	
	public Mono<Boolean> deleteBySet(String key){
		return redisTemplate.opsForSet().delete(key);
	}
	
	public Flux<T> membersBySet(String key) {
		return redisTemplate.opsForSet().members(key);
	}
	
	public Mono<Boolean> isMemberBySet(String key,T value) {
		return redisTemplate.opsForSet().isMember(key, value);
	}
	
	public Flux<T> differenceBySet(String key,String otherKeys) {
		return redisTemplate.opsForSet().difference(key, otherKeys);
	}
	
	public Flux<T> differenceBySet(String key,Collection<String> otherKeys) {
		return redisTemplate.opsForSet().difference(key, otherKeys);
	}
	
	/** 處理Redis資料結構-有序集合(zset) Note:順序:由小到大 */
	public Mono<Boolean> addByZSet(String key, T value, double score) {
		return redisTemplate.opsForZSet().add(key, value, score);
	}

	public Mono<Double> incrementScoreByZSet(String key, T value, double score) {
		return redisTemplate.opsForZSet().incrementScore(key, value, score);
	}

	public Flux<ZSetOperations.TypedTuple<T>> reverseRanageByZSet(String key, Range<Long> range) {
		return redisTemplate.opsForZSet().reverseRangeWithScores(key, range);
	}

	public Mono<Set<ZSetOperations.TypedTuple<T>>> reverseRanageToSetByZSet(String key, Range<Long> range) {
		return this.reverseRanageByZSet(key, range).collect(Collectors.toSet());
	}

	/**
	 * 錯誤處理，目前單純只是印出錯誤訊息
	 * 
	 * @param event 錯誤事件
	 * 
	 */
	private Mono<T> failback(Throwable event) {
		logger.error(event.getMessage());
		return Mono.empty();
	}

	private String generateKey(String key, Class<?> clz) {
		return String.format("%s-%s", key, clz.getSimpleName());
	}
}
