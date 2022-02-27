package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com._4point.aem.docservices.rest_services.cli.restservices.cli.AemConfig.AemServerType;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.AemConfig.Protocol;


class AemConfigTest {

	@ParameterizedTest
	@ValueSource(strings = {"HTTP", "https"})
	void testUrl(final String protocol) {
		final String expectedHost = "TestHost";
		final int expectedPort = 23;
		final AemConfig underTest = new AemConfig() {

			@Override
			public String host() {
				return expectedHost;
			}

			@Override
			public int port() {
				return expectedPort;
			}

			@Override
			public String username() {
				return null;
			}

			@Override
			public String secret() {
				return null;
			}

			@Override
			public Protocol protocol() {
				return Protocol.from(protocol).get();
			}

			@Override
			public AemServerType serverType() {
				return null;
			}
			
		};
		
		assertEquals(protocol.toLowerCase() + "://" + expectedHost + ":" + Integer.toString(expectedPort) + "/", underTest.url());
	}

	@ParameterizedTest
	@ValueSource(strings = {"HTTP", "http", "HttP"})
	void testProtocol_Http(String testString) {
		assertEquals(Protocol.HTTP, Protocol.from(testString).get());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"HTTPS", "https", "HttPs"})
	void testProtocol_Https(String testString) {
		assertEquals(Protocol.HTTPS, Protocol.from(testString).get());
	}

	@ParameterizedTest
	@ValueSource(strings = {"ftp", "foobar", "Httpf"})
	@NullAndEmptySource
	void testProtocol_Invalid(String testString) {
		assertFalse(Protocol.from(testString).isPresent());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"OSGI", "osgi", "OSGi"})
	void testServerType_Osgi(String testString) {
		assertEquals(AemServerType.OSGI, AemServerType.from(testString).get());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"JEE", "jee", "Jee"})
	void testServerType_Jee(String testString) {
		assertEquals(AemServerType.JEE, AemServerType.from(testString).get());
	}

	@ParameterizedTest
	@ValueSource(strings = {"JEEOSGI", "foobar"})
	void testServerType_Invalid(String testString) {
		assertFalse(AemServerType.from(testString).isPresent());
	}
}
