package com._4point.aem.docservices.rest_services.client.helpers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.helpers.FormsFeederUrlFilterBuilder.Location;
import com._4point.aem.docservices.rest_services.client.helpers.FormsFeederUrlFilterBuilder.Protocol;

class FormsFeederUrlFilterBuilderTest {
	private static final int BUFFER_SIZE = 2048;
	private static final String FF_PREFIX = "/aem";
	private static final String MACHINE_NAME = "somemachine";
	private static final int PORT_NO = 9987;

	private static final String testInput = createData(""); 
	private static final String expectedOutput = createData(FF_PREFIX);

	@DisplayName("No parameters should create an identity function. (InputStream)")
	@Test
	void testBuildInputStreamFn_NoParameters() throws Exception {
		final InputStream is = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  new FormsFeederUrlFilterBuilder()
										.buildInputStreamFn()
										.apply(is);
		assertEquals(createData(""), new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@DisplayName("No parameters should create an identity function. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_NoParameters() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = new FormsFeederUrlFilterBuilder()
										.buildOutputStreamFn()
										.apply(os);
		underTest.write(testInput.getBytes(StandardCharsets.UTF_8));
		assertEquals(createData(""), new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@DisplayName("No urls should create an identity function. (InputStream)")
	@Test
	void testBuildInputStreamFn_NoUrls() throws Exception {
		final InputStream is = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  new FormsFeederUrlFilterBuilder()
										.appPrefix(FF_PREFIX)
										.absoluteLocation(Protocol.HTTP, MACHINE_NAME, PORT_NO)
										.buildInputStreamFn()
										.apply(is);
		assertEquals(createData(""), new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@DisplayName("No urls should create an identity function. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_NoUrls() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = new FormsFeederUrlFilterBuilder()
										.appPrefix(FF_PREFIX)
										.absoluteLocation(Protocol.HTTP, MACHINE_NAME, PORT_NO)
										.buildOutputStreamFn()
										.apply(os);
		underTest.write(testInput.getBytes(StandardCharsets.UTF_8));
		assertEquals(createData(""), new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@DisplayName("Urls array and prefix parameter should add prefix. (InputStream)")
	@Test
	void testBuildInputStreamFn_UrlArrayAndPrefixParameters() throws Exception {
		final InputStream is = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  new FormsFeederUrlFilterBuilder(Arrays.asList(replacedUrls))
										.buildInputStreamFn()
										.apply(is);
		assertEquals(createData(FF_PREFIX), new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@DisplayName("Urls array and prefix parameter should add prefix. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_UrlArrayAndPrefixParameters() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = new FormsFeederUrlFilterBuilder(replacedUrls)
											.buildOutputStreamFn()
											.apply(os);

		underTest.write(testInput.getBytes(StandardCharsets.UTF_8));
		assertEquals(createData(FF_PREFIX), new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@DisplayName("Urls List and prefix parameter should add prefix. (InputStream)")
	@Test
	void testBuildInputStreamFn_UrlListAndPrefixParameters() throws Exception {
		String expectedPrefix = "/foo";
		final InputStream is = new ByteArrayInputStream(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  new FormsFeederUrlFilterBuilder()
										.addReplacementUrls(Arrays.asList(replacedUrls))
										.appPrefix(expectedPrefix)
										.buildInputStreamFn()
										.apply(is);
		assertEquals(createData(FF_PREFIX + expectedPrefix), new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@DisplayName("Urls List and prefix parameter should add prefix. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_UrlListAndPrefixParameters() throws Exception {
		String expectedPrefix = "/foo";
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = new FormsFeederUrlFilterBuilder()
											.addReplacementUrls(Arrays.asList(replacedUrls))
											.appPrefix(expectedPrefix)
											.buildOutputStreamFn()
											.apply(os);

		underTest.write(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		assertEquals(createData(FF_PREFIX + expectedPrefix), new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@DisplayName("All parameters should add prefix. (InputStream)")
	@Test
	void testBuildInputStreamFn_AllParameters() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;
		final InputStream is = new ByteArrayInputStream(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  new FormsFeederUrlFilterBuilder()
										.addReplacementUrls(replacedUrls)
										.appPrefix(expectedPrefix)
										.absoluteLocation(Protocol.HTTPS, MACHINE_NAME, PORT_NO)
										.buildInputStreamFn()
										.apply(is);
		assertEquals(createData(expectedLocation + FF_PREFIX + expectedPrefix), new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@DisplayName("All parameters should add prefix. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_AllParameters() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = new FormsFeederUrlFilterBuilder()
											.addReplacementUrls(replacedUrls)
											.appPrefix(expectedPrefix)
											.absoluteLocation(Protocol.HTTPS, MACHINE_NAME, PORT_NO)
											.buildOutputStreamFn()
											.apply(os);

		underTest.write(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		assertEquals(createData(expectedLocation + FF_PREFIX + expectedPrefix), new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@DisplayName("All parameters should add prefix. AbsolouteLocation without port. (InputStream)")
	@Test
	void testBuildInputStreamFn_AllParameters_NoPort() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME;
		final InputStream is = new ByteArrayInputStream(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  new FormsFeederUrlFilterBuilder()
										.addReplacementUrls(replacedUrls)
										.appPrefix(expectedPrefix)
										.absoluteLocation(Protocol.HTTPS, MACHINE_NAME)
										.buildInputStreamFn()
										.apply(is);
		assertEquals(createData(expectedLocation + FF_PREFIX + expectedPrefix), new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@DisplayName("All parameters should add prefix. AbsolouteLocation without port. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_AllParameters_NoPort() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = new FormsFeederUrlFilterBuilder()
											.addReplacementUrls(replacedUrls)
											.appPrefix(expectedPrefix)
											.absoluteLocation(Protocol.HTTPS, MACHINE_NAME)
											.buildOutputStreamFn()
											.apply(os);

		underTest.write(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		assertEquals(createData(expectedLocation + FF_PREFIX + expectedPrefix), new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@DisplayName("All parameters should add prefix. AbsoluteLocation using String. (InputStream)")
	@Test
	void testBuildInputStreamFn_AllParameters_LocationString() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "http://" + MACHINE_NAME + ":" + PORT_NO;
		final InputStream is = new ByteArrayInputStream(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  new FormsFeederUrlFilterBuilder()
										.addReplacementUrls(replacedUrls)
										.appPrefix(expectedPrefix)
										.absoluteLocation(expectedLocation + "/")	// Make sure trailing slash is removed.
										.buildInputStreamFn()
										.apply(is);
		assertEquals(createData(expectedLocation + FF_PREFIX + expectedPrefix), new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@DisplayName("All parameters should add prefix. AbsoluteLocation using String. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_AllParameters_LocationString() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = new FormsFeederUrlFilterBuilder()
											.addReplacementUrls(replacedUrls)
											.appPrefix(expectedPrefix)
											.absoluteLocation(expectedLocation)
											.buildOutputStreamFn()
											.apply(os);

		underTest.write(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		assertEquals(createData(expectedLocation + FF_PREFIX + expectedPrefix), new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@DisplayName("All parameters should add prefix. AbsoluteLocation using String with no port. (InputStream)")
	@Test
	void testBuildInputStreamFn_AllParameters_LocationStringNoPort() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "http://" + MACHINE_NAME;
		final InputStream is = new ByteArrayInputStream(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  new FormsFeederUrlFilterBuilder()
										.addReplacementUrls(replacedUrls)
										.appPrefix(expectedPrefix)
										.absoluteLocation(expectedLocation + "/")	// Make sure trailing slash is removed.
										.buildInputStreamFn()
										.apply(is);
		assertEquals(createData(expectedLocation + FF_PREFIX + expectedPrefix), new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@DisplayName("All parameters should add prefix. AbsoluteLocation using String with no port. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_AllParameters_LocationStringNoPort() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = new FormsFeederUrlFilterBuilder()
											.addReplacementUrls(replacedUrls)
											.appPrefix(expectedPrefix)
											.absoluteLocation(expectedLocation)
											.buildOutputStreamFn()
											.apply(os);

		underTest.write(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		assertEquals(createData(expectedLocation + FF_PREFIX + expectedPrefix), new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	@DisplayName("All parameters should add prefix. AbsoluteLocation using Location object. (InputStream)")
	@Test
	void testBuildInputStreamFn_AllParameters_LocationObject() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;
		final InputStream is = new ByteArrayInputStream(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		InputStream underTest =  new FormsFeederUrlFilterBuilder()
										.addReplacementUrls(replacedUrls)
										.appPrefix(expectedPrefix)
										.absoluteLocation(new Location(Protocol.HTTPS, MACHINE_NAME, PORT_NO))
										.buildInputStreamFn()
										.apply(is);
		assertEquals(createData(expectedLocation + FF_PREFIX + expectedPrefix), new String(readAllBytes(underTest), StandardCharsets.UTF_8));
	}

	@DisplayName("All parameters should add prefix. AbsoluteLocation using Location object. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_AllParameters_LocationObject() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream underTest = new FormsFeederUrlFilterBuilder()
											.addReplacementUrls(replacedUrls)
											.appPrefix(expectedPrefix)
											.absoluteLocation(new Location(Protocol.HTTPS, MACHINE_NAME, PORT_NO))
											.buildOutputStreamFn()
											.apply(os);

		underTest.write(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
		assertEquals(createData(expectedLocation + FF_PREFIX + expectedPrefix), new String(os.toByteArray(), StandardCharsets.UTF_8));
	}

	
	
	private static final String[] replacedUrls = {
			"/etc/clientlibs/",
			"/libs/fd/",
			"/content/forms/"
		};

	private static final String createData(String expectedPrefix) {
		return 
				"--</etc.clientlibs/>--\n"
				+ "--</libs/wcm/>--\n"
				+ "--<" + expectedPrefix + "/etc/clientlibs/>--\n"
				+ "--<" + expectedPrefix + "/libs/fd/>--\n"
				+ "--<" + expectedPrefix + "/content/forms/>--\n"
				+ "--</content/xfaforms/>--\n"
				+ "--</libs/granite/>--\n"
				+ "--</apps/>--\n"
				;
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
