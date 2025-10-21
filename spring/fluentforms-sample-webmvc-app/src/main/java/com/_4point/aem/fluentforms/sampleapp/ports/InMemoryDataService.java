package com._4point.aem.fluentforms.sampleapp.ports;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com._4point.aem.fluentforms.sampleapp.domain.DataService;

public class InMemoryDataService implements DataService {
	
	private final Map<String, byte[]> dataStore = new HashMap<>();

	@Override
	public byte[] load(String key) {
		byte[] data = dataStore.get(Objects.requireNonNull(key));
		if (data == null) { throw new DataServiceException("Error retrieving data, key does not exist (%s).".formatted(key)); }
		return Arrays.copyOf(data, data.length);
	}

	@Override
	public void save(String key, byte[] data) {
		if (dataStore.containsKey(key)) { throw new DataServiceException("Error saving data, key already exists (%s).".formatted(key)); }
		dataStore.put(Objects.requireNonNull(key), Arrays.copyOf(Objects.requireNonNull(data), data.length));
	}

	@Override
	public boolean exists(String key) throws DataServiceException {
		return dataStore.containsKey(key);
	}

}
