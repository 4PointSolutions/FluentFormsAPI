package com._4point.aem.docservices.rest_services.client.helpers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ReplacingInputStreamTest {

	static Stream<Arguments> samples() {
		return Stream.of(
				// String input, String pattern, String replacement, String expected
				Arguments.of("abcdefghijk", "def", "fed", "abcfedghijk"),
				Arguments.of("abcdefghijkabcdefghijkabcdefghijk", "def", "fed", "abcfedghijkabcfedghijkabcfedghijk"),
				Arguments.of("abcdefghijk", "def", "def", "abcdefghijk"),	// same result
				Arguments.of("abcdefghijk", "def", "", "abcghijk"),
				Arguments.of("abcdefghijk", "def", null, "abcghijk"),
				Arguments.of("abcdefghijk", "d", "dd", "abcddefghijk"),
				Arguments.of("", "d", "dd", ""),							// Empty String
				Arguments.of("abcdefghijk", "ijk", "kij", "abcdefghkij"),	// At end of string
				Arguments.of("abcdefghij", "ijk", "kij", "abcdefghij")	// At end of string, incomplete
			);
	}

	static Stream<Arguments> newlineSamples() {
		return Stream.of(
				Arguments.of("foo\r\nbar\r\n", "foo\nbar\n"),
				Arguments.of("foo\rbar\r", "foo\nbar\n"));
	}

	@ParameterizedTest
	@MethodSource("newlineSamples")
	void shouldFixNewlines(String input, String expected) throws IOException {
		try (ByteArrayInputStream bos = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))) {
			try (InputStream ros = ReplacingInputStream.newLineNormalizingInputStream(bos)) {
				String result = IOUtils.toString(ros, StandardCharsets.UTF_8);
				assertEquals(expected, result);
			}
		}
	}

	@ParameterizedTest
	@MethodSource("samples")
	void shouldReplace(String input, String pattern, String replacement, String expected) throws IOException {
		byte[] replacementBytes = null;
		if (replacement != null) {
			replacementBytes = replacement.getBytes(StandardCharsets.UTF_8);
		}
		try (ByteArrayInputStream bos = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))) {
			try (InputStream ros = new ReplacingInputStream(bos, pattern.getBytes(StandardCharsets.UTF_8),
							replacementBytes)) {
				byte[] result = IOUtils.toByteArray(ros);
				assertArrayEquals(expected.getBytes(StandardCharsets.UTF_8), result);
			}
		}
	}

	@ParameterizedTest
	@MethodSource("samples")
	void shouldReplaceUsingStringConstructor(String input, String pattern, String replacement, String expected) throws IOException {
		try (ByteArrayInputStream bos = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))) {
			try(InputStream ros = new ReplacingInputStream(bos, pattern, replacement)) {
				String result = IOUtils.toString(ros, StandardCharsets.UTF_8);
				assertEquals(expected, result);
	        }
		}
	}
	
	private static final String MODIFICATION_TARGETS_FORMAT_STR = """
			'contextPath = %sresult[1];'
			'"%s/etc.clientlibs/toggles.json"'
			""";

	@Test
	void shouldWorkWithByteArrayInputStream() throws IOException {
		String aemResponseText = "Value should be modified. " + MODIFICATION_TARGETS_FORMAT_STR.formatted("", "");
		String expectedResult = "Value should be modified. " + MODIFICATION_TARGETS_FORMAT_STR.formatted("\" + /aem\" + ", "");
		
		byte[] aemResponseBytes = aemResponseText.getBytes();
		byte[] expectedResultBytes = expectedResult.getBytes();
   		String target = "contextPath = result[1];";

   		String replacement = "contextPath = \" + /aem\" + result[1];";
   		try (ReplacingInputStream replacingInputStream = new ReplacingInputStream(new ByteArrayInputStream(aemResponseBytes), target, replacement)) {
   			byte[] result = replacingInputStream.readAllBytes();
   			
   			assertArrayEquals(aemResponseBytes, new ByteArrayInputStream(aemResponseBytes).readAllBytes());
   			assertArrayEquals(expectedResultBytes, result);
   		}
	}

}
