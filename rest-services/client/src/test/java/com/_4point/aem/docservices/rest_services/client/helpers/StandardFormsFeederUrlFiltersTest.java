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


	private static final String createData(String expectedPrefix) {
		return 
				"--<" + expectedPrefix + "/etc.clientlibs/>--\n"
				+ "--<" + expectedPrefix + "/libs/wcm/>--\n"
				+ "--<" + expectedPrefix + "/etc/clientlibs/>--\n"
				+ "--<" + expectedPrefix + "/libs/fd/>--\n"
				+ "--<" + expectedPrefix + "/content/forms/>--\n"
				+ "--<" + expectedPrefix + "/content/xfaforms/>--\n"
				+ "--<" + expectedPrefix + "/libs/granite/>--\n"
				+ "--<" + expectedPrefix + "/apps/>--\n"
				;
	}

	private static final String testInput = createData(""); 
	private static final String expectedOutput = createData(FF_PREFIX);

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

	@Test
	void testGetStandardOutputStreamFilter() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = StandardFormsFeederUrlFilters.getStandardOutputStreamFilter().apply(os);
		underTest.write(testInput.getBytes(StandardCharsets.UTF_8));
		assertEquals(expectedOutput, new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	void testGetStandardInputStreamFilter() throws Exception {
		final InputStream is = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  StandardFormsFeederUrlFilters.getStandardInputStreamFilter().apply(is);
		assertEquals(expectedOutput, new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@Test
	void testGetStandardOutputStreamFilterOsgi() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = StandardFormsFeederUrlFilters.getStandardOutputStreamFilter(AemServerType.StandardType.OSGI).apply(os);
		underTest.write(testInput.getBytes(StandardCharsets.UTF_8));
		assertEquals(expectedOutput, new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	void testGetStandardInputStreamFilterOsgi() throws Exception {
		final InputStream is = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  StandardFormsFeederUrlFilters.getStandardInputStreamFilter(AemServerType.StandardType.OSGI).apply(is);
		assertEquals(expectedOutput, new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}


	private static final String testPrefix = "/lc";
	private static final String testPrefixInput = createData(testPrefix);
	private static final String expectedPrefixOutput = createData(FF_PREFIX + testPrefix); 

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

	//
	@Test
	void testGetStandardOutputStreamFilterWithPrefix() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = StandardFormsFeederUrlFilters.getStandardOutputStreamFilter(testPrefix).apply(os);
		underTest.write(testPrefixInput.getBytes(StandardCharsets.UTF_8));
		assertEquals(expectedPrefixOutput, new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	void testGetStandardInputStreamFilterWithPrefix() throws Exception {
		final InputStream is = new ByteArrayInputStream(testPrefixInput.getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  StandardFormsFeederUrlFilters.getStandardInputStreamFilter(testPrefix).apply(is);
		assertEquals(expectedPrefixOutput, new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@Test
	void testGetStandardOutputStreamFilterJee() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = StandardFormsFeederUrlFilters.getStandardOutputStreamFilter(AemServerType.StandardType.JEE).apply(os);
		underTest.write(testPrefixInput.getBytes(StandardCharsets.UTF_8));
		assertEquals(expectedPrefixOutput, new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	void testGetStandardInputStreamFilterJee() throws Exception {
		final InputStream is = new ByteArrayInputStream(testPrefixInput.getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  StandardFormsFeederUrlFilters.getStandardInputStreamFilter(AemServerType.StandardType.JEE).apply(is);
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
