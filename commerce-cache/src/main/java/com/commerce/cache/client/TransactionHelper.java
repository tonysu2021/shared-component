package com.commerce.cache.client;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

import org.springframework.integration.redis.util.RedisLockRegistry;

import reactor.core.publisher.Mono;

public class TransactionHelper {

	private RedisLockRegistry redisLockRegistry;

	public TransactionHelper(RedisLockRegistry redisLockRegistry) {
		this.redisLockRegistry = redisLockRegistry;
	}

	/**
	 * Do supplier in a transaction protected by a distributed lock, lock key is
	 * given by param key.
	 * 
	 * @param transactionKey 交易鎖的key name
	 * @param supplier       緩存邏輯
	 * 
	 * @return
	 * 
	 */
	public <T> Mono<T> doInTransaction(String transactionKey, Supplier<Mono<T>> supplier) {
		Lock lock = redisLockRegistry.obtain(transactionKey);
		return Mono.just(0)
				.doFirst(lock::lock)
				.doFinally(dummy -> lock.unlock())
				.doOnError(Exception.class, event -> lock.unlock())
				.flatMap(dummy -> supplier.get())
//                .subscribeOn(Schedulers.newSingle(transactionKey))
				.log("[Transaction]");
	}
}
