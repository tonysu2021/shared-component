package com.commerce.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;

import com.commerce.cache.client.CacheManager;
import com.commerce.cache.client.TransactionHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

@Configuration
public class RedisConfig {

	@Autowired
	RedisConnectionFactory factory;

	@Bean("jacksonSerializer")
	public Jackson2JsonRedisSerializer<Object> jacksonSerializer() {
		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new ParameterNamesModule());
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.WRAPPER_ARRAY);

		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		serializer.setObjectMapper(objectMapper);

		return serializer;
	}

	/**
	 * Support Any type
	 * 
	 */
	@Bean("objectTemplate")
	public <T> ReactiveRedisTemplate<String, T> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory,
			Jackson2JsonRedisSerializer<T> jacksonSerializer) {
		RedisSerializationContext.RedisSerializationContextBuilder<String, T> builder = RedisSerializationContext
				.newSerializationContext(new StringRedisSerializer());
		RedisSerializationContext<String, T> context = builder.value(jacksonSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}

	@Bean
	public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
		return new RedisLockRegistry(redisConnectionFactory, "commerce-lock", 10000);
	}

	@Bean
	public TransactionHelper transactionHelper(RedisLockRegistry redisLockRegistry) {
		return new TransactionHelper(redisLockRegistry);
	}

	@Bean("cacheManager")
	public <T> CacheManager<T> cacheManager(@Qualifier("objectTemplate") ReactiveRedisTemplate<String, T> template,
			TransactionHelper helper) {
		return new CacheManager<>(template, helper);
	}

	/**
	 * Clear database before shut down.
	 * 
	 */
//	@PreDestroy
//	public void cleanRedis() {
//		factory.getConnection().flushDb();
//	}
}
