package com._4point.aem.fluentforms.api;

import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com._4point.aem.fluentforms.impl.CrxUrlHandler;

class PathOrUrlTest {

	@BeforeAll
	static void setUpAll() throws Exception {
		CrxUrlHandler.enableCrxProtocol();
	}
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@ParameterizedTest
	@ValueSource(strings = { "C:/foo/bar", "foo", "foo/bar", "C:\\foo\\bar", "\\\\foo\\bar" })
	void testFromString_Path(String path) {
		PathOrUrl result = PathOrUrl.from(path);
		assertTrue(result.isPath(), "Expected that isPath() would be true");
		assertFalse(result.isUrl(), "Expected that isUrl() would be false");
		assertFalse(result.isCrxUrl(), "Expected that isCrxUrl() would be false");
		Path expected = Paths.get(path);
		assertEquals(expected, result.getPath());
		assertEquals(expected.toString(), result.toString());
	}

	@ParameterizedTest
	@ValueSource(strings = { "http://example.com", "https://example.com", "file:///~/calendar" })
	void testFromString_Url(String url) throws MalformedURLException {
		PathOrUrl result = PathOrUrl.from(url);
		assertFalse(result.isPath(), "Expected that isPath() would be false");
		assertTrue(result.isUrl(), "Expected that isUrl() would be true");
		assertFalse(result.isCrxUrl(), "Expected that isCrxUrl() would be false");
		URL expected = new URL(url);
		assertEquals(expected, result.getUrl());
		assertEquals(expected.toString(), result.toString());
	}

	@ParameterizedTest
	@ValueSource(strings = { "crx:/content/dam/formsanddocument", "crx://content/dam/formsanddocument", "crx://foo./?foo?#?#%foo/bar?foo" })
	void testFromString_CrxUrl(String url) throws MalformedURLException {
		PathOrUrl result = PathOrUrl.from(url);
		assertFalse(result.isPath(), "Expected that isPath() would be false");
		assertFalse(result.isUrl(), "Expected that isUrl() would be false");
		assertTrue(result.isCrxUrl(), "Expected that isCrxUrl() would be true");
		String crxUrl = result.getCrxUrl();
		assertEquals(url, crxUrl);
		assertEquals(url, result.toString());
	}

	@ParameterizedTest
	@ValueSource(strings = { "", " " })	// I'd like to find a crx: example that causes an invalid URL, but have been unable to find one.
	void testFromString_Invalid(String str) {
		IllegalArgumentException iaex = assertThrows(IllegalArgumentException.class, ()->PathOrUrl.from(str));
	}

	@ParameterizedTest
	@NullSource
	void testFromString_Null(String str) {
		NullPointerException iaex = assertThrows(NullPointerException.class, ()->PathOrUrl.from(str));
	}

	@EnabledOnOs(OS.WINDOWS)	// Only execute on Windows, because crap://more/crap is a valid unix path.
	@ParameterizedTest
	@ValueSource(strings = { "crap://more/crap" })
	void testFromString_InvalidWindows(String str) {
		IllegalArgumentException iaex = assertThrows(IllegalArgumentException.class, ()->PathOrUrl.from(str));
	}

	@Test
	void testFromString_Null() {
		assertThrows(NullPointerException.class, ()->PathOrUrl.from((String)null));
	}
	
	
}
