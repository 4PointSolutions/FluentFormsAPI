package com._4point.aem.docservices.rest_services.server.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.server.data.DataCache.Entry;

class DataCacheTest {

	@Test
	void testAddDataToCacheGetDataFromCache() {
		String expectedContentType = "text/plain";
		byte[] expectedDataBytes = "testAddDataToCacheGetDataFromCache Result".getBytes();

		Entry dataFromCache = DataCache.getDataFromCache(DataCache.addDataToCache(expectedDataBytes, expectedContentType));
		
		assertEquals(expectedContentType,dataFromCache.contentType());
		assertArrayEquals(expectedDataBytes, dataFromCache.data());
	}

	@Test
	void testManyAddDataToCacheGetDataFromCache() {
		int numEntries = 100;
		String expectedContentType = "text/plain";
		String[] keys = new String[numEntries];
		byte[][] expectedDataBytes = new byte[numEntries][];
		// Populate the DataCache
		for(int i = 0; i < numEntries; i++) {
			expectedDataBytes[i] = ("testManyAddDataToCacheGetDataFromCache Result " + Integer.toString(i)).getBytes();
			keys[i] = DataCache.addDataToCache(expectedDataBytes[i], expectedContentType);
		}

		// Check the DataCache (in reverse order)
		for(int i = numEntries - 1; i > -1; i--) {
			Entry dataFromCache = DataCache.getDataFromCache(keys[i]);
			assertEquals(expectedContentType,dataFromCache.contentType());
			assertArrayEquals(expectedDataBytes[i], dataFromCache.data());
		}
	}

}
