package com._4point.aem.fluentforms.api;

import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AbsoluteOrRelativeUrlTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@ParameterizedTest
	@ValueSource(strings = { "http://example.com", "file:///~/calendar" })
	void testFromString_Absolute(String url) throws Exception {
		AbsoluteOrRelativeUrl underTest = AbsoluteOrRelativeUrl.fromString(url);
		assertTrue(underTest.isAbsolute());
		assertFalse(underTest.isRelative());
		URL expected = new URL(url);
		assertEquals(expected, underTest.getAbsolute());
		assertEquals(expected.toString(), underTest.toString());
	}

	@ParameterizedTest
	@ValueSource(strings = { "/foo/bar", "foo/bar" })
	void testFromString_Relative(String url) {
		AbsoluteOrRelativeUrl underTest = AbsoluteOrRelativeUrl.fromString(url);
		assertTrue(underTest.isRelative());
		assertFalse(underTest.isAbsolute());
		assertEquals(url, underTest.getRelative());
		assertEquals(url, underTest.toString());
	}

	@ParameterizedTest
	@ValueSource(strings = { "", " ", "crap://more/crap" })
	void testFromString_Invalid(String str) {
		IllegalArgumentException iaex = assertThrows(IllegalArgumentException.class, ()->AbsoluteOrRelativeUrl.fromString(str));
	}

	@Test
	void testFromString_Null() {
		assertThrows(NullPointerException.class, ()->AbsoluteOrRelativeUrl.fromString(null));
	}

}
