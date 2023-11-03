package com._4point.aem.fluentforms.spring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {com._4point.aem.fluentforms.spring.AemConfigurationTest.TestApplication.class}, 
				properties = {
		"fluentforms.aem.servername=" + AemConfigurationTest.EXPECTED_SERVERNAME, 
		"fluentforms.aem.port=" + AemConfigurationTest.EXPECTED_PORT, 
		"fluentforms.aem.user=ENC(7FgD3ZsSExfUGRYlXNc++6C1upPBURNKq6HouzagnNZW4FsBwFs5+crawv+djhw6)",		 
		"fluentforms.aem.password=ENC(QmQ6iTm/+TOO8U3dDuBzJWH129vReWgYNdgqQwWhjWaQy6j8sMnk2/Auhehmlh3v)",
//		"fluentforms.aem.useSsl=true",
		"jasypt.encryptor.algorithm=PBEWITHHMACSHA512ANDAES_256",
		"jasypt.encryptor.password=4Point",
		"jasypt.encryptor.iv-generator-classname=org.jasypt.iv.RandomIvGenerator",
		"jasypt.encryptor.salt-generator-classname=org.jasypt.salt.RandomSaltGenerator"
		})
class AemConfigurationTest {

	protected static final String EXPECTED_SERVERNAME = "localhost";
	protected static final int EXPECTED_PORT = 4502;
	protected static final String EXPECTED_USER = "admin";
	protected static final String EXPECTED_PASSWORD = "admin";

	@Autowired
	AemConfiguration underTest;

	
	@BeforeEach
	void setUp() throws Exception {
		assertNotNull(underTest);
	}

	@Test
	void testGetServername() {
		assertEquals(EXPECTED_SERVERNAME, underTest.servername());
	}

	@Test
	void testGetPort() {
		assertEquals(EXPECTED_PORT, underTest.port());
	}

	@Test
	void testGetUser() {
		assertEquals(EXPECTED_USER, underTest.user());
	}

	@Test
	void testGetPassword() {
		assertEquals(EXPECTED_PASSWORD, underTest.password());
	}

	@Test
	void testGetUseSsl() {
		assertEquals(false, underTest.useSsl());
	}

	@Test
	void testGetUrl() {
		assertEquals("http://" + EXPECTED_SERVERNAME + ":" + EXPECTED_PORT + "/", underTest.url());
	}

	@SpringBootTest(classes = {com._4point.aem.fluentforms.spring.AemConfigurationTest.TestApplication.class}, 
			properties = {
					"fluentforms.aem.servername=" + EXPECTED_SERVERNAME, 
					"fluentforms.aem.port=" + AemConfigurationTests2.EXPECTED_PORT, 
					"fluentforms.aem.user=" + EXPECTED_USER,		 
					"fluentforms.aem.password=" + EXPECTED_PASSWORD,
					"fluentforms.aem.useSsl=true",
	})
	static class AemConfigurationTests2 {
		protected static final int EXPECTED_PORT = 80;

		@Autowired
		AemConfiguration underTest;

		
		@BeforeEach
		void setUp() throws Exception {
			assertNotNull(underTest);
		}

		@Test
		void testGetUseSsl() {
			assertEquals(true, underTest.useSsl());
		}

		@Test
		void testGetUrl() {
			assertEquals("https://" + EXPECTED_SERVERNAME + "/", underTest.url());
		}

		
	}
	
	@SpringBootApplication
	@EnableConfigurationProperties(AemConfiguration.class)
	public static class TestApplication {
		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}

	}
}
