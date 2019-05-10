package com._4point.aem.docservices.rest_services.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AcceptHeadersTest {
	
	@Test
	public void testAcceptHeaders() throws NoSuchFieldException {
		testAcceptHeaders(Arrays.asList(ContentType.TEXT_HTML));
		testAcceptHeaders(Arrays.asList(ContentType.APPLICATION_PDF, ContentType.MULTIPART_FORMDATA, ContentType.TEXT_PLAIN));
		testAcceptHeaders(Collections.emptyList());
		
		assertEquals(0, getAcceptHeadersInternalList(new AcceptHeaders("")).size());	// Empty string should create an empty list.
		
		assertThrows(IllegalArgumentException.class, ()->new AcceptHeaders("foo/bar/test"));
	}

	private void testAcceptHeaders(List<ContentType> list) throws NoSuchFieldException {
		AcceptHeaders acceptHeaders = createAcceptHeadersObject(list);
		List<ContentType> contentTypes = getAcceptHeadersInternalList(acceptHeaders);
		assertEquals(list, contentTypes, "Expected the list of contentTypes to match the list provided.");
	}

	@Test
	public void testNegotiateResponseContentType() {
		List<ContentType> oneEntry = Arrays.asList(ContentType.TEXT_HTML);
		testNegotiateResponseContentType(oneEntry, oneEntry, ContentType.TEXT_HTML);

		// Test various combinations when several specific content types are specified.
		List<ContentType> severalEntries = Arrays.asList(ContentType.APPLICATION_PDF, ContentType.MULTIPART_FORMDATA, ContentType.TEXT_PLAIN);
		testNegotiateResponseContentType(severalEntries, severalEntries, ContentType.APPLICATION_PDF);
		testNegotiateResponseContentType(severalEntries, Arrays.asList(ContentType.MULTIPART_FORMDATA, ContentType.TEXT_PLAIN, ContentType.APPLICATION_PDF), ContentType.APPLICATION_PDF);
		testNegotiateResponseContentType(severalEntries, Arrays.asList(ContentType.TEXT_PLAIN, ContentType.MULTIPART_FORMDATA), ContentType.MULTIPART_FORMDATA);
		testNegotiateResponseContentType(severalEntries, Arrays.asList(ContentType.TEXT_HTML, ContentType.TEXT_PLAIN), ContentType.TEXT_PLAIN);
		testNegotiateResponseContentType(severalEntries, Arrays.asList(ContentType.TEXT_PLAIN, ContentType.TEXT_HTML), ContentType.TEXT_PLAIN);
		
		// Test specifying wildcards
		List<ContentType> textWildcardEntry = Arrays.asList(ContentType.TEXT_WILDCARD);
		testNegotiateResponseContentType(textWildcardEntry, Arrays.asList(ContentType.TEXT_HTML, ContentType.TEXT_PLAIN), ContentType.TEXT_HTML);
		testNegotiateResponseContentType(textWildcardEntry, Arrays.asList(ContentType.TEXT_PLAIN, ContentType.TEXT_HTML), ContentType.TEXT_PLAIN);
		testNegotiateResponseContentType(Arrays.asList(ContentType.WILDCARD), Arrays.asList(ContentType.MULTIPART_FORMDATA, ContentType.TEXT_PLAIN, ContentType.APPLICATION_PDF), ContentType.MULTIPART_FORMDATA);

		// Test negative scenarios
		assertFalse(createAcceptHeadersObject(Arrays.asList(ContentType.TEXT_PLAIN, ContentType.TEXT_HTML))
					.negotiateResponseContentType(Arrays.asList(ContentType.APPLICATION_PDF, ContentType.MULTIPART_FORMDATA))
					.isPresent(), "Expected to get no results back from the negotiation.");
		assertFalse(createAcceptHeadersObject(Arrays.asList(ContentType.TEXT_WILDCARD))
					.negotiateResponseContentType(Arrays.asList(ContentType.APPLICATION_PDF, ContentType.MULTIPART_FORMDATA))
					.isPresent(), "Expected to get no results back from the negotiation.");

		assertFalse(createAcceptHeadersObject(Collections.emptyList())
					.negotiateResponseContentType(Arrays.asList(ContentType.APPLICATION_PDF, ContentType.MULTIPART_FORMDATA))
					.isPresent(), "Expected to get no results back from the negotiation when no accept headers.");
		assertFalse(createAcceptHeadersObject(Arrays.asList(ContentType.TEXT_PLAIN, ContentType.TEXT_HTML))
					.negotiateResponseContentType(Collections.emptyList())
					.isPresent(), "Expected to get no results back from the negotiation when no supported content types.");
	}

	private void testNegotiateResponseContentType(List<ContentType> acceptHeaderList, List<ContentType> supportedHeaderList, ContentType expectedResponse) {
		AcceptHeaders acceptHeadersTextHtml = createAcceptHeadersObject(acceptHeaderList);
		Optional<ContentType> responseContentType = acceptHeadersTextHtml.negotiateResponseContentType(supportedHeaderList);	// Should be compatible with itself
		assertTrue(responseContentType.isPresent(), "Expected to get a response type back.");
		assertEquals(expectedResponse, responseContentType.get(), "Expected to get expected content type back.");
	}

	private AcceptHeaders createAcceptHeadersObject(List<ContentType> list) {
		String acceptHeader = createAcceptHeaderString(list);
		AcceptHeaders acceptHeaders = new AcceptHeaders(acceptHeader);
		return acceptHeaders;
	}

	private String createAcceptHeaderString(List<ContentType> list) {
		return list.stream().map(ContentType::getContentTypeStr).collect(Collectors.joining(","));
	}

	private List<ContentType> getAcceptHeadersInternalList(AcceptHeaders acceptHeaders) throws NoSuchFieldException {
		@SuppressWarnings("unchecked")
		List<ContentType> contentTypes = ((List<ContentType>) junitx.util.PrivateAccessor.getField(acceptHeaders, "acceptTypes"));
		return contentTypes;
	}
}
