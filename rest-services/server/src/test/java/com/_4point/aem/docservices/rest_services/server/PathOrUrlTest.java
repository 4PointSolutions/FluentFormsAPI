package com._4point.aem.docservices.rest_services.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PathOrUrlTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@ParameterizedTest
	@ValueSource(strings = { "C:/foo/bar", "foo", "foo/bar", "C:\\foo\\bar", "\\\\foo\\bar" })
	void testFromString_Path(String path) {
		PathOrUrl result = PathOrUrl.fromString(path);
		assertTrue(result.isPath(), "Expected that isPath() would be true");
		assertFalse(result.isUrl(), "Expected that isUrl() would be false");
	}

	@ParameterizedTest
	@ValueSource(strings = { "http://example.com", "file:///~/calendar" })
	void testFromString_Url(String url) {
		PathOrUrl result = PathOrUrl.fromString(url);
		assertFalse(result.isPath(), "Expected that isPath() would be false");
		assertTrue(result.isUrl(), "Expected that isUrl() would be true");
	}

	@ParameterizedTest
	@ValueSource(strings = { "", " ", "crap://more/crap" })
	void testFromString_Invalid(String str) {
		IllegalArgumentException iaex = assertThrows(IllegalArgumentException.class, ()->PathOrUrl.fromString(str));
	}

	@Test
	void testFromString_Null() {
		assertThrows(NullPointerException.class, ()->PathOrUrl.fromString(null));
	}
}
