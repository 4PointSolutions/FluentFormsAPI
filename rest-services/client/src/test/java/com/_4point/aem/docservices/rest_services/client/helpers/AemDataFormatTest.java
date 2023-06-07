package com._4point.aem.docservices.rest_services.client.helpers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AemDataFormatTest {

	@Test
	void testSniff_XML() {
		assertEquals(AemDataFormat.XML, AemDataFormat.sniff("\n    <root/>".getBytes()).get());
	}

	@Test
	void testSniff_JSON() {
		assertEquals(AemDataFormat.JSON, AemDataFormat.sniff("\n  { } ".getBytes()).get());
	}

	@ParameterizedTest
	@ValueSource(strings = {"\n  12345556677788899999", "\n", "   "})
	@EmptySource
	void testSniff_UnknownFormat(String testString) {
		assertEquals(Optional.empty(), AemDataFormat.sniff(testString.getBytes()));
	}
}
