package com._4point.aem.fluentforms.spring;

import static org.junit.jupiter.api.Assertions.*;

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
//"fluentforms.aem.useSsl=true",
"jasypt.encryptor.algorithm=PBEWITHHMACSHA512ANDAES_256",
"jasypt.encryptor.password=4Point",
"jasypt.encryptor.iv-generator-classname=org.jasypt.iv.RandomIvGenerator",
"jasypt.encryptor.salt-generator-classname=org.jasypt.salt.RandomSaltGenerator"
})
class AemProxyConfigurationTest {

	@Autowired
	AemProxyConfiguration underTest;
	
	@Test
	void testGetAfBaseLocation() {
		assertEquals("", underTest.afBaseLocation());
	}

	@Test
	void testGetAppPrefix() {
		assertEquals("", underTest.aemPrefix());
	}

	@SpringBootApplication
	@EnableConfigurationProperties(AemConfiguration.class)
	public static class TestApplication {
		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}

	}
}
