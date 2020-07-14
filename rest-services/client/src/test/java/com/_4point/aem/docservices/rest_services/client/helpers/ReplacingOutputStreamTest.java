package com._4point.aem.docservices.rest_services.client.helpers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ReplacingOutputStreamTest {

	static Stream<Arguments> samples() {
		return Stream.of(
				Arguments.of("abcdefghijk", "def", "fed", "abcfedghijk"),
				Arguments.of("abcdefghijkabcdefghijkabcdefghijk", "def", "fed", "abcfedghijkabcfedghijkabcfedghijk"),
				Arguments.of("abcdefghijk", "def", "def", "abcdefghijk"),
				Arguments.of("abcdefghijk", "def", "", "abcghijk"),
				Arguments.of("abcdefghijk", "def", null, "abcghijk"),
				Arguments.of("abcdefghijk", "d", "dd", "abcddefghijk"),
				Arguments.of("", "d", "dd", ""),
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
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			try (OutputStream ros = ReplacingOutputStream.newLineNormalizingOutputStream(bos)) {
				IOUtils.write(input.getBytes(), ros);
			}
			assertEquals(expected, new String(bos.toByteArray()));
		}
	}

	@ParameterizedTest
	@MethodSource("samples")
	void shouldReplace(String input, String pattern, String replacement, String expected) throws IOException {
		byte[] replacementBytes = null;
		if (replacement != null) {
			replacementBytes = replacement.getBytes(StandardCharsets.UTF_8);
		}
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			try (OutputStream ros = new ReplacingOutputStream(bos, pattern.getBytes(StandardCharsets.UTF_8),
							replacementBytes)) {
				IOUtils.write(input.getBytes(), ros);
			}
			assertArrayEquals(expected.getBytes(), bos.toByteArray());
		}
	}

	@ParameterizedTest
	@MethodSource("samples")
	void shouldReplaceUsingStringConstructor(String input, String pattern, String replacement, String expected) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			try(OutputStream ros = new ReplacingOutputStream(bos, pattern, replacement)) {
				IOUtils.write(input.getBytes(), ros);
	        }
			assertEquals(expected, bos.toString());
		}
	}
}
