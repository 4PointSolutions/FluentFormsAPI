package com._4point.aem.fluentforms.sampleapp.domain;

public interface DataService {
	byte[] load(String key);
	void save(String key, byte[] data);
	
	@SuppressWarnings("serial")
	public static final class DataServiceException extends RuntimeException {

		public DataServiceException() {
		}

		public DataServiceException(String message, Throwable cause) {
			super(message, cause);
		}

		public DataServiceException(String message) {
			super(message);
		}

		public DataServiceException(Throwable cause) {
			super(cause);
		}
	}
}
