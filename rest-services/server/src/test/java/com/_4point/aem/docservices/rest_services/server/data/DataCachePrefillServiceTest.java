package com._4point.aem.docservices.rest_services.server.data;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.adobe.forms.common.service.DataOptions;
import com.adobe.forms.common.service.DataXMLOptions;
import com.adobe.forms.common.service.FormsException;
import com.adobe.forms.common.service.PrefillData;

class DataCachePrefillServiceTest {

	private static final byte[] EXPECTED_XML_DATA_BYTES = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
			+ "<form1>\r\n"
			+ "	<TextField1>Text Field1 Data</TextField1>\r\n"
			+ "	<TextField2>Text Field2 Data</TextField2>\r\n"
			+ "</form1>").getBytes(StandardCharsets.UTF_8);
	
	private static final byte[] EXPECTED_JSON_DATA_BYTES = ("{"
			+ "\"form1\": {"
			+ "    \"TextField1\" : \"Text Field1 Data\",\r\n"
			+ "    \"TextField2\" : \"Text Field2 Data\"\r\n"
			+ "    }"
			+ "}").getBytes(StandardCharsets.UTF_8);
	
	private DataCachePrefillService underTest = new DataCachePrefillService();
	
	@Test
	void testGetPrefillData_XML() throws Exception {
		
		// Store data in the cache and then call the preFill service with mock options object that contains the UUID from the cache.
		String uuid = DataCache.addDataToCache(EXPECTED_XML_DATA_BYTES, "application/xml");
		PrefillData result = underTest.getPrefillData(mockDataOptions(uuid));
		
		// Verify the result
		assertNotNull(result);
		assertAll(
				()->assertArrayEquals(EXPECTED_XML_DATA_BYTES, IOUtils.toByteArray(result.getInputStream()), "Expected result to match the data placed in the cache."),
				()->assertEquals(com.adobe.forms.common.service.ContentType.XML, result.getContentType(), "Expected result to match the data placed in the cache.")
				);
	}

	@Test
	void testGetPrefillData_JSON() throws Exception {
		
		// Store data in the cache and then call the preFill service with mock options object that contains the UUID from the cache.
		String uuid = DataCache.addDataToCache(EXPECTED_JSON_DATA_BYTES, "application/json");
		PrefillData result = underTest.getPrefillData(mockDataOptions(uuid));
		
		// Verify the result
		assertNotNull(result);
		assertAll(
				()->assertArrayEquals(EXPECTED_JSON_DATA_BYTES, IOUtils.toByteArray(result.getInputStream()), "Expected result to match the data placed in the cache."),
				()->assertEquals(com.adobe.forms.common.service.ContentType.JSON, result.getContentType(), "Expected result to match the data placed in the cache.")
				);
	}

	@Test
	void testGetPrefillData_Unknown() throws Exception {
		
		// Store data in the cache and then call the preFill service with mock options object that contains the UUID from the cache.
		String invalidContentType = "application/octet-stream";
		String uuid = DataCache.addDataToCache(EXPECTED_XML_DATA_BYTES, invalidContentType);
		FormsException ex = assertThrows(FormsException.class, ()->underTest.getPrefillData(mockDataOptions(uuid)));
		String msg = ex.getMessage();

		// Verify the result
		assertNotNull(msg);
		assertThat(msg, allOf(containsString(invalidContentType), containsStringIgnoringCase("No support for data with content type")));
	}


	@Test
	void testGetDataXMLForDataRef_InvalidUuid() throws Exception {
		
		// Send an invalid uuid
		String uuid = "foobar";
		PrefillData result = underTest.getPrefillData(mockDataOptions(uuid));
		
		// Verify the result
		assertNull(result, "Expect to get null back if an invalid cache id is provided.");
	}

	@Test
	void testGetDataXMLForDataRef_EmptyIdentifier() throws Exception {
		
		// Send an empty UUID
		PrefillData result = underTest.getPrefillData(mockDataOptions(""));
		
		// Verify the result
		assertNull(result, "Expect to get null back if an invalid cache id is provided.");
	}

	@Test
	void testGetDataXMLForDataRef_EmptyIdentifier2() throws Exception {
		
		// send service name only
		PrefillData result = underTest.getPrefillData(mockDataOptions(null));
		
		// Verify the result
		assertNull(result, "Expect to get null back if an invalid cache id is provided.");
	}

	@Test
	void testGetDataXMLForDataRef_EmptyOptions() throws Exception {
		
		// Send empty DataXmlOptions structure (which is what happens when called from editor).
		PrefillData result = underTest.getPrefillData(new DataOptions());
		
		// Verify the result
		assertNull(result, "Expect to get null back if an invalid cache id is provided.");
	}

	@Test
	void testGetDataXMLForDataRef_Null() {
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.getPrefillData(null));
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

	private DataOptions mockDataOptions(String identifier) {
		DataOptions options = new DataOptions();
		options.setDataRef("service://FFPrefillService" + (identifier != null ? "/" + identifier : ""));
		return options;
	}
}
