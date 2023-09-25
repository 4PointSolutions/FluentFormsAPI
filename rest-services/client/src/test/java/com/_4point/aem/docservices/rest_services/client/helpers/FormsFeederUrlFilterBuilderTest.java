package com._4point.aem.docservices.rest_services.client.helpers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.helpers.FormsFeederUrlFilterBuilder.Location;
import com._4point.aem.docservices.rest_services.client.helpers.FormsFeederUrlFilterBuilder.Protocol;

class FormsFeederUrlFilterBuilderTest {
	private static final int BUFFER_SIZE = 2048;
	private static final String FF_PREFIX = "/aem";
	private static final String MACHINE_NAME = "somemachine";
	private static final int PORT_NO = 9987;

	@DisplayName("No parameters should create an identity function. (InputStream)")
	@Test
	void testBuildInputStreamFn_NoParameters() throws Exception {
		tester()
				.runInputStreamTest()
				.shouldBe("")
				;
	}

	@DisplayName("No parameters should create an identity function. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_NoParameters() throws Exception {
		tester()
				.runOutputStreamTest()
				.shouldBe("")
				;
	}

	@DisplayName("No urls should create an identity function. (InputStream)")
	@Test
	void testBuildInputStreamFn_NoUrls() throws Exception {
		tester()
				.aemPrefix(FF_PREFIX)
				.absoluteLocation(Protocol.HTTP, MACHINE_NAME, PORT_NO)
				.runInputStreamTest()
				.shouldBe("")
				;
	}

	@DisplayName("No urls should create an identity function. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_NoUrls() throws Exception {
		tester()
				.aemPrefix(FF_PREFIX)
				.absoluteLocation(Protocol.HTTP, MACHINE_NAME, PORT_NO)
				.runOutputStreamTest()
				.shouldBe("")
				;
	}

	@DisplayName("Urls array and prefix parameter should add aem prefix. (InputStream)")
	@Test
	void testBuildInputStreamFn_UrlArrayAndPrefixParameters() throws Exception {
		tester(replacedUrls)
				.runInputStreamTest()
				.shouldBe(FF_PREFIX)
				;
	}

	@DisplayName("Urls array and prefix parameter should add aem prefix. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_UrlArrayAndPrefixParameters() throws Exception {
		tester(replacedUrls)
				.runOutputStreamTest()
				.shouldBe(FF_PREFIX)
				;
	}

	@DisplayName("Urls List and prefix parameter should add aem prefix. (InputStream)")
	@Test
	void testBuildInputStreamFn_UrlListAndPrefixParameters() throws Exception {
		String expectedPrefix = "/foo";
		tester()
				.addReplacementUrls(Arrays.asList(replacedUrls))
				.aemPrefix(expectedPrefix)
				.runInputStreamTest(expectedPrefix)
				.shouldBe(FF_PREFIX + expectedPrefix)
				;
	}

	@DisplayName("Urls List and prefix parameter should add aem prefix. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_UrlListAndPrefixParameters() throws Exception {
		String expectedPrefix = "/foo";
		tester()
				.addReplacementUrls(Arrays.asList(replacedUrls))
				.aemPrefix(expectedPrefix)
				.runOutputStreamTest(expectedPrefix)
				.shouldBe(FF_PREFIX + expectedPrefix)
				;
	}

	@DisplayName("Mmost parameters and should add aem prefix. (InputStream)")
	@Test
	void testBuildInputStreamFn_MostParameters() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;
		tester()
				.addReplacementUrls(replacedUrls)
				.aemPrefix(expectedPrefix)
				.absoluteLocation(Protocol.HTTPS, MACHINE_NAME, PORT_NO)
				.runInputStreamTest(expectedPrefix)
				.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix)
				;
	}

	@DisplayName("Most parameters and should add aem prefix. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_MostParameters() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;
		tester()
				.addReplacementUrls(replacedUrls)
				.aemPrefix(expectedPrefix)
				.absoluteLocation(Protocol.HTTPS, MACHINE_NAME, PORT_NO)
				.runOutputStreamTest(expectedPrefix)
				.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix)
				;
	}

	@DisplayName("All parameters should add aem prefix and client prefix. (InputStream)")
	@Test
	void testBuildInputStreamFn_AllParameters() throws Exception {
		String expectedAemPrefix = "/foo";
		String expectedClientPrefix = "/client";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;
		tester()
				.addReplacementUrls(replacedUrls)
				.aemPrefix(expectedAemPrefix)
				.clientPrefix(expectedClientPrefix)
				.absoluteLocation(Protocol.HTTPS, MACHINE_NAME, PORT_NO)
				.runInputStreamTest(expectedAemPrefix)
				.shouldBe(expectedLocation + expectedClientPrefix + FF_PREFIX + expectedAemPrefix)
				;
	}

	@DisplayName("All parameters should add aem prefix and client prefix. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_AllParameters() throws Exception {
		String expectedAemPrefix = "/foo";
		String expectedClientPrefix = "/client";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;
		tester()
				.addReplacementUrls(replacedUrls)
				.aemPrefix(expectedAemPrefix)
				.clientPrefix(expectedClientPrefix)
				.absoluteLocation(Protocol.HTTPS, MACHINE_NAME, PORT_NO)
				.runOutputStreamTest(expectedAemPrefix)
				.shouldBe(expectedLocation + expectedClientPrefix + FF_PREFIX + expectedAemPrefix)
				;
	}

	@DisplayName("All parameters should add aem prefix. AbsolouteLocation without port. (InputStream)")
	@Test
	void testBuildInputStreamFn_AllParameters_NoPort() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME;
		tester()
			.addReplacementUrls(replacedUrls)
			.aemPrefix(expectedPrefix)
			.absoluteLocation(Protocol.HTTPS, MACHINE_NAME)
			.runInputStreamTest(expectedPrefix)
			.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix)
			;
	}

	@DisplayName("All parameters should add aem prefix. AbsolouteLocation without port. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_AllParameters_NoPort() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME;
		tester()
			.addReplacementUrls(replacedUrls)
			.aemPrefix(expectedPrefix)
			.absoluteLocation(Protocol.HTTPS, MACHINE_NAME)
			.runOutputStreamTest(expectedPrefix)
			.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix)
			;
	}

	@DisplayName("All parameters should add aem prefix. AbsoluteLocation using String. (InputStream)")
	@Test
	void testBuildInputStreamFn_AllParameters_LocationString() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "http://" + MACHINE_NAME + ":" + PORT_NO;
		tester()
			.addReplacementUrls(replacedUrls)
			.aemPrefix(expectedPrefix)
			.absoluteLocation(new URL(expectedLocation + "/"))	// Make sure trailing slash is removed.
			.runInputStreamTest(expectedPrefix)
			.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix)
			;
	}

	@DisplayName("All parameters should add aem prefix. AbsoluteLocation using String. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_AllParameters_LocationString() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;
		tester()
			.addReplacementUrls(replacedUrls)
			.aemPrefix(expectedPrefix)
			.absoluteLocation(new URL(expectedLocation))
			.runOutputStreamTest(expectedPrefix)
			.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix)
			;
	}

	@DisplayName("All parameters should add aem prefix. AbsoluteLocation using String with no port. (InputStream)")
	@Test
	void testBuildInputStreamFn_AllParameters_LocationStringNoPort() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "http://" + MACHINE_NAME;
		tester()
			.addReplacementUrls(replacedUrls)
			.aemPrefix(expectedPrefix)
			.absoluteLocation(new URL(expectedLocation + "/"))	// Make sure trailing slash is removed.
			.runInputStreamTest(expectedPrefix)
			.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix)
			;
	}

	@DisplayName("All parameters should add aem prefix. AbsoluteLocation using String with no port. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_AllParameters_LocationStringNoPort() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME;
		tester()
			.addReplacementUrls(replacedUrls)
			.aemPrefix(expectedPrefix)
			.absoluteLocation(new URL(expectedLocation))
			.runOutputStreamTest(expectedPrefix)
			.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix)
			;
	}

	@DisplayName("All parameters should add aem prefix. AbsoluteLocation using Location object. (InputStream)")
	@Test
	void testBuildInputStreamFn_AllParameters_LocationObject() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;
		tester()
			.addReplacementUrls(replacedUrls)
			.aemPrefix(expectedPrefix)
			.absoluteLocation(Location.from(Protocol.HTTPS, MACHINE_NAME, PORT_NO))
			.runInputStreamTest(expectedPrefix)
			.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix)
			;
	}

	@DisplayName("All parameters should add aem prefix. AbsoluteLocation using Location object. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_AllParameters_LocationObject() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME + ":" + PORT_NO;

		tester()
			.addReplacementUrls(replacedUrls)
			.aemPrefix(expectedPrefix)
			.absoluteLocation(Location.from(Protocol.HTTPS, MACHINE_NAME, PORT_NO))
			.runOutputStreamTest(expectedPrefix)
			.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix)
			;
	}

	
	@DisplayName("All parameters should add aem prefix. AbsoluteLocation using Location object with no port. (InputStream)")
	@Test
	void testBuildInputStreamFn_AllParameters_LocationObjectNoPort() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME;
		tester()
			.addReplacementUrls(replacedUrls)
			.aemPrefix(expectedPrefix)
			.absoluteLocation(Location.from(Protocol.HTTPS, MACHINE_NAME))
			.runInputStreamTest(expectedPrefix)
			.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix);
			;
	}

	@DisplayName("All parameters should add aem prefix. AbsoluteLocation using Location object with no port. (OutputStream)")
	@Test
	void testBuildOutputStreamFn_AllParameters_LocationObjectNoPort() throws Exception {
		String expectedPrefix = "/foo";
		String expectedLocation = "https://" + MACHINE_NAME;
		
		tester()
			.addReplacementUrls(replacedUrls)
			.aemPrefix(expectedPrefix)
			.absoluteLocation(Location.from(Protocol.HTTPS, MACHINE_NAME))
			.runOutputStreamTest(expectedPrefix)
			.shouldBe(expectedLocation + FF_PREFIX + expectedPrefix);
			;
	}

	private static Tester tester() {
		return new Tester();
	}
	
	private static Tester tester(String[] urls) {
		return new Tester(urls);
	}
	
	private static class Tester {
		
		private FormsFeederUrlFilterBuilder underTest;
		
		private Tester() {
			this.underTest = new FormsFeederUrlFilterBuilder();
		}

		private Tester(String[] urls) {
			this.underTest = new FormsFeederUrlFilterBuilder(urls);
		}

		Tester addReplacementUrls(List<String> urls) {
			underTest = underTest.addReplacementUrls(urls);
			return this;
		}

		Tester addReplacementUrls(String[] urls) {
			underTest = underTest.addReplacementUrls(urls);
			return this;
		}
		
		Tester aemPrefix(String prefix) {
			underTest = underTest.aemPrefix(prefix);
			return this;
		}
		
		Tester clientPrefix(String prefix) {
			underTest = underTest.clientPrefix(prefix);
			return this;
		}
		
		Tester absoluteLocation(Location location) {
			underTest = underTest.absoluteLocation(location);
			return this;
		}
		
		Tester absoluteLocation(URL location) {
			underTest = underTest.absoluteLocation(location);
			return this;
		}
		
		public Tester absoluteLocation(Protocol https, String machineName, int portNo) {
			underTest = underTest.absoluteLocation(https, machineName, portNo);
			return this;
		}
		
		Tester absoluteLocation(Protocol https, String machineName) {
			underTest = underTest.absoluteLocation(https, machineName);
			return this;
		}

		Asserter runOutputStreamTest(String expectedPrefix) throws IOException {
			return new Asserter(OutputStreamTester.create(underTest).runTest(expectedPrefix));
		}
		
		Asserter runInputStreamTest(String expectedPrefix) throws IOException {
			return new Asserter(InputStreamTester.create(underTest, expectedPrefix).runTest());
		}
		
		Asserter runOutputStreamTest() throws IOException {
			return new Asserter(OutputStreamTester.create(underTest).runTest(""));
		}
		
		Asserter runInputStreamTest() throws IOException {
			return new Asserter(InputStreamTester.create(underTest, "").runTest());
		}
		
		private static class OutputStreamTester {
			private final OutputStream outputStream;
			private final ByteArrayOutputStream original;

			private OutputStreamTester(OutputStream outputStream, ByteArrayOutputStream original) {
				this.outputStream = outputStream;
				this.original = original;
			}
			
			private static OutputStreamTester create(FormsFeederUrlFilterBuilder bldr) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				return new OutputStreamTester(bldr.buildOutputStreamFn().apply(os), os);
			}
			
			private String runTest(String expectedPrefix) throws IOException {
				outputStream.write(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
				return new String(original.toByteArray(), StandardCharsets.UTF_8);
			}
		}

		private static class InputStreamTester {
			private final InputStream inputStream;

			private InputStreamTester(InputStream inputStream) {
				this.inputStream = inputStream;
			}
			
			private static InputStreamTester create(FormsFeederUrlFilterBuilder bldr, String expectedPrefix) {
				final InputStream is = new ByteArrayInputStream(createData(expectedPrefix).getBytes(StandardCharsets.UTF_8));
				return new InputStreamTester(bldr.buildInputStreamFn().apply(is));
			}

			private String runTest() throws IOException {
				return new String(readAllBytes(inputStream), StandardCharsets.UTF_8);
			}

		}
		
		private static class Asserter {
			private final String actual;

	        Asserter(String actual) {
	            this.actual = actual;
	        }

	        void shouldBe(String expectedPrefix) {
	        	String expected = createData(expectedPrefix);
	            assertEquals(expected, actual);
	        }			
		}
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
