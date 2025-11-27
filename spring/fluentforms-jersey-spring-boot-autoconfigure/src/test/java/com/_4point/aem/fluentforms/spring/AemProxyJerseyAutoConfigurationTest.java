package com._4point.aem.fluentforms.spring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {com._4point.aem.fluentforms.spring.FluentFormsJerseyAutoConfigurationTest.TestApplication.class, FluentFormsAutoConfiguration.class, AemProxyAutoConfiguration.class}, 
properties = {
		"fluentforms.aem.servername=localhost", 
		"fluentforms.aem.port=4502", 
		"fluentforms.aem.user=admin",		 
		"fluentforms.aem.password=admin)",
		})
class AemProxyJerseyAutoConfigurationTest {

	@Test
	void testDocumentFactory(@Autowired ResourceConfigCustomizer afProxyConfigurer) {
		assertNotNull(afProxyConfigurer);
	}

	@SpringBootApplication
	@EnableConfigurationProperties({AemConfiguration.class,AemProxyConfiguration.class})
	public static class TestApplication {
		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}

	}
}