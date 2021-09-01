package com.commerce.stream.store;

public interface StreamStorage<T> {
	
	public T get(String key, Class<T> clz);
		
	public Boolean save(String key, T data);
}
