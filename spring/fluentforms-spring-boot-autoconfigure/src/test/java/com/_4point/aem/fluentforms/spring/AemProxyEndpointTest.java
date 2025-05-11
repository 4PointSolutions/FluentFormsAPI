package com._4point.aem.fluentforms.spring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@WireMockTest
@SpringBootTest(classes = {com._4point.aem.fluentforms.spring.AemProxyEndpointTest.TestApplication.class}, 
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
class AemProxyEndpointTest {
	private static final String OSGI_CSRF_TOKEN_PATH = "/libs/granite/csrf/token.json";
	
	private RestClient restClient;
	@BeforeEach
	void setup(WireMockRuntimeInfo wmRuntimeInfo) {
		restClient = RestClient.builder()
//							   .baseUrl("http://localhost:%d/aem".formatted(wmRuntimeInfo.getHttpPort()))
							   .baseUrl("http://localhost:%d/aem".formatted(AemConfigurationTest.EXPECTED_PORT))
							   .build();
	}

	@Test
	void testProxyOsgiCsrfToken() {
		String result = restClient.get()
				  .uri(OSGI_CSRF_TOKEN_PATH)
				  .retrieve()
				  .body(String.class);
	}

	@Test
	void testProxyJeeCsrfToken() {
		fail("Not yet implemented");
	}

	@Test
	void testProxyGet() {
		fail("Not yet implemented");
	}

	@Test
	void testProxyGet_Utils_Js() {
		fail("Not yet implemented");
	}

	@Test
	void testProxyPost() {
		fail("Not yet implemented");
	}

	@SpringBootApplication
	@EnableConfigurationProperties({AemConfiguration.class, AemProxyConfiguration.class})
	public static class TestApplication {
		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}

//		@Bean
//		public ResourceConfigCustomizer afProxyConfigurer(AemConfiguration aemConfig, AemProxyConfiguration aemProxyConfig, @Autowired(required = false) SslBundles sslBundles) {
//			return config->config.register(new AemProxyEndpoint(aemConfig, aemProxyConfig, sslBundles));
//		}
	}
}
