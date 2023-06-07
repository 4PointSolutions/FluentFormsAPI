package com._4point.aem.fluentforms.sampleapp.domain;

/**
 * Service that can store and retrieve data 
 *
 */
public interface DataService {
	/**
	 * Retrieves data from the Data Service.
	 * 
	 * throws an exception if no data has been previously saved with the key provided. 
	 * 
	 * @param key
	 *   the Key provided when the data was previously saved.
	 * @return the bytes of the data previously saved
	 * @throws DataServiceException
	 */
	byte[] load(String key) throws DataServiceException;
	
	/**
	 * Stores data to the Data Service for later retrieval.
	 * 
	 * throws an exception if the data has been previously saved with the key provided. 
	 * 
	 * @param key
	 *  	the Key that will be used later to retieve the data
	 * @param data 
	 * 		the bytes to be saved
	 * @throws DataServiceException
	 */
	void save(String key, byte[] data) throws DataServiceException;
	
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
