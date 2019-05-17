package com._4point.aem.docservices.rest_services.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class ContentTypeTest {

	// valid content types
	private static final String TEXT_HTML_STR = "text/html";
	private static final ContentType TEXT_HTML = ContentType.valueOf(TEXT_HTML_STR);
	private static final ContentType TEXT_HTML_UC = ContentType.valueOf("TEXT/HTML");
	private static final ContentType TEXT_PLAIN = ContentType.valueOf("text/plain");
	private static final ContentType APPLICATION_PDF = ContentType.valueOf("application/PDF");
	private static final ContentType APPLICATION_WILDCARD = ContentType.valueOf("application/*");
	private static final ContentType MULTIPART_FORMDATA = ContentType.valueOf("multipart/form-data");
	private static final ContentType WILDCARD = ContentType.valueOf("*/*");

	
	@Test
	public void testContentType_BadStrings() {
		testContentTypeConstructor_BadArg("*/plain");	// type wildcard not allowed if subtype is not wildcard.
		testContentTypeConstructor_BadArg("foo*");		// must have separator
		testContentTypeConstructor_BadArg("foo/bar/*");	// must have only one separator
		testContentTypeConstructor_BadArg("");			// empty string

	}

	private void testContentTypeConstructor_BadArg(String contentTypeStr) {
		
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->ContentType.valueOf(contentTypeStr));
		assertThat("Expected the error message to contain the offending string", e.getMessage(), containsString(contentTypeStr));
	}

	@Test
	public void testGetContentTypeStr() {
		assertEquals(TEXT_HTML_STR, TEXT_HTML.getContentTypeStr(), "Expected that getContentTypeStr would match the starting string for a lower case type.");
		assertEquals(TEXT_HTML_STR, TEXT_HTML_UC.getContentTypeStr(), "Expected that getContentTypeStr would return a lower case type.");
	}

	@Test
	public void testIsCompatibleWith() {
		assertTrue(TEXT_HTML.isCompatibleWith(TEXT_HTML), "Expected type to be compatible with itself");
		assertTrue(TEXT_HTML.isCompatibleWith(TEXT_HTML_UC), "Expected type to be compatible with itself");
		assertTrue(TEXT_HTML_UC.isCompatibleWith(TEXT_HTML), "Expected type to be compatible with itself");

		assertEquals(TEXT_HTML, TEXT_HTML, "Expected type to be equal to itself");
		assertEquals(TEXT_HTML, TEXT_HTML_UC, "Expected type to be equal to itself");
		assertEquals(TEXT_HTML_UC, TEXT_HTML, "Expected type to be equal to itself");

		assertFalse(TEXT_HTML_UC.isCompatibleWith(TEXT_PLAIN), "Expected type to not be compatible with a incompatible type");
		assertNotEquals(TEXT_HTML, TEXT_PLAIN, "Expected type to not be equal to a different type");

		assertTrue(APPLICATION_PDF.isCompatibleWith(APPLICATION_WILDCARD), "Expected type to be compatible with wildcard subtype");
		assertTrue(APPLICATION_WILDCARD.isCompatibleWith(APPLICATION_PDF), "Expected type to be compatible with wildcard subtype");

		assertTrue(MULTIPART_FORMDATA.isCompatibleWith(WILDCARD), "Expected all types to be compatible with wildcard");
		assertTrue(WILDCARD.isCompatibleWith(MULTIPART_FORMDATA), "Expected all types to be compatible with wildcard");
	}
}
