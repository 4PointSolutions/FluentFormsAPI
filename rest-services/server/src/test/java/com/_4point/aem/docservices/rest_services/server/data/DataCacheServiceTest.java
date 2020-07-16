package com._4point.aem.docservices.rest_services.server.data;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.server.data.DataCache.Entry;

import io.wcm.testing.mock.aem.junit5.AemContext;

class DataCacheServiceTest {
	private static final String DATA_KEY_PARAM = "DataKey";
	private static final String DATA_PARAM = "Data";
	private static final String APPLICATION_XML = "application/xml";

	private final AemContext aemContext = new AemContext();
	
	private final DataCacheService underTest = new DataCacheService();

	@Test
	void testDoGet_HappyPath() throws Exception {
		String expectedResultData = "testDoGet Happy Path Result";
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

	@Test
	void testDoGet_BadKey() throws Exception {
		String expectedResultData = "testDoGet Happy Path Result";
		String expectedContentType = APPLICATION_XML;
		byte[] resultDataBytes = expectedResultData.getBytes();
		String badRequestKey = "BadRequestKey";
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(DATA_KEY_PARAM, badRequestKey);
		
		underTest.doGet(request, response);

		assertEquals(SlingHttpServletResponse.SC_NOT_FOUND, response.getStatus());
		assertThat(response.getStatusMessage(), containsStringIgnoringCase("Unable to find"));
		assertThat(response.getStatusMessage(), containsStringIgnoringCase(badRequestKey));
	}

	@Test
	void testDoGet_NoKey() throws Exception {
		String expectedResultData = "testDoGet Happy Path Result";
		String expectedContentType = APPLICATION_XML;
		byte[] resultDataBytes = expectedResultData.getBytes();
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		underTest.doGet(request, response);

		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		assertThat(response.getStatusMessage(), containsStringIgnoringCase("Missing form parameter"));
		assertThat(response.getStatusMessage(), containsStringIgnoringCase(DATA_KEY_PARAM));
		assertEquals(0, response.getContentLength());
		assertNull(response.getContentType());
	}


	@Test
	void testDoPost_HappyPath() throws Exception {
		String expectedResultData = "testDoPost Happy Path Result";
		String expectedContentType = APPLICATION_XML;
		byte[] resultDataBytes = expectedResultData.getBytes();
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(DATA_PARAM, resultDataBytes, expectedContentType);
		
		underTest.doPost(request, response);

		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals("text/plain", response.getContentType());

		String returnedKey = response.getOutputAsString();
		Entry dataFromCache = DataCache.getDataFromCache(returnedKey).get();
		
		assertEquals(expectedContentType, dataFromCache.contentType());
		assertArrayEquals(resultDataBytes, dataFromCache.data());
	}

	@Test
	void testDoPost_BadRequest() throws Exception {
		String expectedResultData = "testDoPost Happy Path Result";
		String expectedContentType = APPLICATION_XML;
		byte[] resultDataBytes = expectedResultData.getBytes();
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(DATA_PARAM + "foobar", resultDataBytes, expectedContentType);
		
		underTest.doPost(request, response);

		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		assertThat(response.getStatusMessage(), containsStringIgnoringCase("Missing form parameter"));
		assertThat(response.getStatusMessage(), containsStringIgnoringCase(DATA_PARAM));
	}

}
