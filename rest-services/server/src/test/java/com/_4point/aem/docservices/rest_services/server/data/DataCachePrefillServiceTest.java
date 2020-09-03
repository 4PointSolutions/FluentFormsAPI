package com._4point.aem.docservices.rest_services.server.data;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.adobe.forms.common.service.DataXMLOptions;

class DataCachePrefillServiceTest {

	private static final byte[] EXPECTED_DATA_BYTES = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
			+ "<form1>\r\n"
			+ "	<TextField1>Text Field1 Data</TextField1>\r\n"
			+ "	<TextField2>Text Field2 Data</TextField2>\r\n"
			+ "</form1>").getBytes(StandardCharsets.UTF_8);
	
	private DataCachePrefillService underTest = new DataCachePrefillService();
	
	@Test
	void testGetDataXMLForDataRef() throws Exception {
		
		// Store data in the cache and then call the preFill service with mock options object that contains the UUID from the cache.
		String uuid = addDataToCache(EXPECTED_DATA_BYTES);
		InputStream result = underTest.getDataXMLForDataRef(mockDataXmlOptions(uuid));
		
		// Verify the result
		assertNotNull(result);
		assertArrayEquals(EXPECTED_DATA_BYTES, IOUtils.toByteArray(result), "Expected result to match the data placed in the cache.");
	}

	@Test
	void testGetDataXMLForDataRef_InvalidUuid() throws Exception {
		
		// Store data in the cache and then call the preFill service with mock options object that contains the UUID from the cache.
		String uuid = "foobar";
		InputStream result = underTest.getDataXMLForDataRef(mockDataXmlOptions(uuid));
		
		// Verify the result
		assertNull(result, "Expect to get null back if an invalid cache id is provided.");
	}

	@Test
	void testGetDataXMLForDataRef_Null() {
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.getDataXMLForDataRef(null));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertAll(
				()->assertThat(msg, containsString("DataXMLOptions")),
				()->assertThat(msg, containsString("com._4point.aem.docservices.rest_services.server.data.DataCachePrefillService.getDataXMLForDataRef()."))
				);
	}

	@Test
	void testGetServiceName() {
		assertEquals("FFPrefillService", underTest.getServiceName());
	}

	@Test
	void testGetServiceDescription() {
		assertEquals("Fluent Forms REST Services Prefill Service for Adaptive Forms", underTest.getServiceDescription());
	}

	private String addDataToCache(byte[] bytes) {
		return DataCache.addDataToCache(bytes, "application/xml");
	}
	
	private DataXMLOptions mockDataXmlOptions(String identifier) {
		DataXMLOptions options = new DataXMLOptions();
		options.setDataRef("service://FFPrefillService/" + identifier);
		return options;
	}
}
