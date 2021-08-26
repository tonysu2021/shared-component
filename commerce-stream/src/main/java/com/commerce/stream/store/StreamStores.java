package com.commerce.stream.store;

public interface StreamStores<T> {
	
	public T get(String key, Class<T> clz);
		
	public Boolean save(String key, T data);
}
