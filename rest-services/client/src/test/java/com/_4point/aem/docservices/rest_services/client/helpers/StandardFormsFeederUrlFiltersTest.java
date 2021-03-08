package com._4point.aem.docservices.rest_services.client.helpers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class StandardFormsFeederUrlFiltersTest {
	private static final int BUFFER_SIZE = 2048;
	private static final String FF_PREFIX = "/aem";

	private static final String testInput = 
			  "--</etc.clientlibs/>--\n"
			+ "--</libs/wcm/>--\n"
			+ "--</etc/clientlibs/>--\n"
			+ "--</libs/fd/>--\n"
			+ "--</content/forms/>--\n"
			+ "--</content/xfaforms/>--\n"
			+ "--</libs/granite/>--\n"
			+ "--</apps/>--\n"
			;
			
	private static final String expectedOutput = 
			  "--<" + FF_PREFIX + "/etc.clientlibs/>--\n"
			+ "--<" + FF_PREFIX + "/libs/wcm/>--\n"
			+ "--<" + FF_PREFIX + "/etc/clientlibs/>--\n"
			+ "--<" + FF_PREFIX + "/libs/fd/>--\n"
			+ "--<" + FF_PREFIX + "/content/forms/>--\n"
			+ "--<" + FF_PREFIX + "/content/xfaforms/>--\n"
			+ "--<" + FF_PREFIX + "/libs/granite/>--\n"
			+ "--<" + FF_PREFIX + "/apps/>--\n"
			;

	@Test
	void testReplaceAemUrlsOutputStream() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = StandardFormsFeederUrlFilters.replaceAemUrls(os);
		underTest.write(testInput.getBytes(StandardCharsets.UTF_8));
		assertEquals(expectedOutput, new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	void testReplaceAemUrlsInputStream() throws Exception {
		final InputStream is = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  StandardFormsFeederUrlFilters.replaceAemUrls(is);
		assertEquals(expectedOutput, new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@Test
	void testReplaceAemUrlsOutputStreamOsgi() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = StandardFormsFeederUrlFilters.replaceAemUrls(os, AemServerType.StandardType.OSGI);
		underTest.write(testInput.getBytes(StandardCharsets.UTF_8));
		assertEquals(expectedOutput, new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	void testReplaceAemUrlsInputStreamOsgi() throws Exception {
		final InputStream is = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  StandardFormsFeederUrlFilters.replaceAemUrls(is, AemServerType.StandardType.OSGI);
		assertEquals(expectedOutput, new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	private static final String testPrefix = "/lc";
	private static final String testPrefixInput = 
			  "--<" + testPrefix + "/etc.clientlibs/>--\n"
			+ "--<" + testPrefix + "/libs/wcm/>--\n"
			+ "--<" + testPrefix + "/etc/clientlibs/>--\n"
			+ "--<" + testPrefix + "/libs/fd/>--\n"
			+ "--<" + testPrefix + "/content/forms/>--\n"
			+ "--<" + testPrefix + "/content/xfaforms/>--\n"
			+ "--<" + testPrefix + "/libs/granite/>--\n"
			+ "--<" + testPrefix + "/apps/>--\n"
			;
			
	private static final String expectedPrefixOutput = 
			  "--<" + FF_PREFIX + testPrefix + "/etc.clientlibs/>--\n"
			+ "--<" + FF_PREFIX + testPrefix + "/libs/wcm/>--\n"
			+ "--<" + FF_PREFIX + testPrefix + "/etc/clientlibs/>--\n"
			+ "--<" + FF_PREFIX + testPrefix + "/libs/fd/>--\n"
			+ "--<" + FF_PREFIX + testPrefix + "/content/forms/>--\n"
			+ "--<" + FF_PREFIX + testPrefix + "/content/xfaforms/>--\n"
			+ "--<" + FF_PREFIX + testPrefix + "/libs/granite/>--\n"
			+ "--<" + FF_PREFIX + testPrefix + "/apps/>--\n"
			;

	@Test
	void testReplaceAemUrlsOutputStreamWithPrefix() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = StandardFormsFeederUrlFilters.replaceAemUrls(os, testPrefix);
		underTest.write(testPrefixInput.getBytes(StandardCharsets.UTF_8));
		assertEquals(expectedPrefixOutput, new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	void testReplaceAemUrlsInputStreamWithPrefix() throws Exception {
		final InputStream is = new ByteArrayInputStream(testPrefixInput.getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  StandardFormsFeederUrlFilters.replaceAemUrls(is, testPrefix);
		assertEquals(expectedPrefixOutput, new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@Test
	void testReplaceAemUrlsOutputStreamJee() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = StandardFormsFeederUrlFilters.replaceAemUrls(os, AemServerType.StandardType.JEE);
		underTest.write(testPrefixInput.getBytes(StandardCharsets.UTF_8));
		assertEquals(expectedPrefixOutput, new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	void testReplaceAemUrlsInputStreamJee() throws Exception {
		final InputStream is = new ByteArrayInputStream(testPrefixInput.getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  StandardFormsFeederUrlFilters.replaceAemUrls(is, AemServerType.StandardType.JEE);
		assertEquals(expectedPrefixOutput, new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}


	
	private static void transfer(InputStream is, OutputStream out) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = is.read(buffer, 0, BUFFER_SIZE)) >= 0) {
            out.write(buffer, 0, read);
        }
	}

	private static byte[] readAllBytes(InputStream is) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        transfer(is, out);
        return out.toByteArray();
	}

}
