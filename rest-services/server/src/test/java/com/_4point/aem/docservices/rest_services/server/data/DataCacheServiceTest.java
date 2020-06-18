package com._4point.aem.docservices.rest_services.server.data;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;

import io.wcm.testing.mock.aem.junit5.AemContext;

class DataCacheServiceTest {
	private static final String DATA_KEY_PARAM = "DataKey";
	private static final String APPLICATION_XML = "application/xml";

	private final AemContext aemContext = new AemContext();
	
	private final DataCacheService underTest = new DataCacheService();

	@Test
	void test_HappyPath() throws Exception {
		String expectedResultData = "testDoPost Happy Path Result";
		String expectedContentType = APPLICATION_XML;
		byte[] resultDataBytes = expectedResultData.getBytes();
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(DATA_KEY_PARAM, DataCache.addDataToCache(resultDataBytes, expectedContentType));
		
		underTest.doGet(request, response);

		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(expectedContentType, response.getContentType());
		assertEquals(expectedResultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
	}

}
