package com._4point.aem.docservices.rest_services.server.data;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public  enum DataCache {
	INSTANCE;

	private final ConcurrentMap<String, Entry> dataMap = new ConcurrentHashMap<>();
	
	public static String addDataToCache(byte[] data, String contentType) {
		String key = UUID.randomUUID().toString();
		INSTANCE.dataMap.put(key, new Entry(data, contentType));
		return key;
	}
	
	public static Entry getDataFromCache(String key) {
		return INSTANCE.dataMap.remove(key);
	}
	
	public static class Entry {
		private final byte[] data;
		private final String contentType;
		public Entry(byte[] data, String contentType) {
			super();
			this.data = data;
			this.contentType = contentType;
		}
		public byte[] data() {
			return data;
		}
		public String contentType() {
			return contentType;
		}
	}
}

