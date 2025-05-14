package com._4point.aem.fluentforms.spring;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Objects.requireNonNullElse;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@WireMockTest(httpPort = AemProxyEndpointTest.WIREMOCK_PORT)
@SpringBootTest(classes = {com._4point.aem.fluentforms.spring.AemProxyEndpointTest.TestApplication.class}, 
webEnvironment = WebEnvironment.RANDOM_PORT,
properties = {
"fluentforms.aem.servername=localhost", 
"fluentforms.aem.port=" + AemProxyEndpointTest.WIREMOCK_PORT, 
"fluentforms.aem.user=ENC(7FgD3ZsSExfUGRYlXNc++6C1upPBURNKq6HouzagnNZW4FsBwFs5+crawv+djhw6)",		 
"fluentforms.aem.password=ENC(QmQ6iTm/+TOO8U3dDuBzJWH129vReWgYNdgqQwWhjWaQy6j8sMnk2/Auhehmlh3v)",
//"fluentforms.aem.useSsl=true",
"fluentforms.rproxy.af-base-location=" + AemProxyEndpointTest.AF_BASE_LOCATION,
"jasypt.encryptor.algorithm=PBEWITHHMACSHA512ANDAES_256",
"jasypt.encryptor.password=4Point",
"jasypt.encryptor.iv-generator-classname=org.jasypt.iv.RandomIvGenerator",
"jasypt.encryptor.salt-generator-classname=org.jasypt.salt.RandomSaltGenerator",
"logging.level.com._4point.aem.fluentforms.spring.AemProxyEndpoint=DEBUG"
})
class AemProxyEndpointTest {
	static final int WIREMOCK_PORT = 5504;
	static final String AF_BASE_LOCATION = "/aem";

	// The following is a string that contains all possible values that may be modified by the AemProxyEndpoint.
	private static final String MODIFICATION_TARGETS_FORMAT_STR = """
			'contextPath = %sresult[1];'
			'"%s/etc.clientlibs/toggles.json"'
			""";
	private static final String MODIFICATION_TARGETS = MODIFICATION_TARGETS_FORMAT_STR.formatted("", "");

	@LocalServerPort
	private int port;

	private RestClient restClient;

	@BeforeEach
	void setup(WireMockRuntimeInfo wmRuntimeInfo) {
		restClient = RestClient.builder()
							   .baseUrl(("http://localhost:%d" + AF_BASE_LOCATION).formatted(port))	// "/aem" added to the front of the base URL we're testing
							   .build();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"/libs/granite/csrf/token.json", 
			"/lc/libs/granite/csrf/token.json",
			"/etc.clientlibs/clientlibs/granite/jquery/granite/csrf.js",
			"/etc.clientlibs/fd/xfaforms/clientlibs/I18N/en.js",
			"/etc.clientlibs/fd/xfaforms/clientlibs/I18N/en_US.js",
			"/etc.clientlibs/fd/xfaforms/clientlibs/profile.css",
			})
	void testProxyUnmodifiedGet(String endpoint) {
		// Given
		String aemResponseText = "Value should be unmodified. " + MODIFICATION_TARGETS;
		runTest(endpoint, aemResponseText, aemResponseText);
	}

	final static List<Arguments> MODIFIED_GET_ARGUMENTS = List.of(
			Arguments.of("/etc.clientlibs/clientlibs/granite/utils.js", "\"" + AF_BASE_LOCATION + "\" + ", ""),
			Arguments.of("/etc.clientlibs/fd/xfaforms/clientlibs/profile.js", "", "/aem")
			);

	@ParameterizedTest
	@FieldSource("MODIFIED_GET_ARGUMENTS")
	void testProxyModifiedGet(String endpoint, String modValueUtilsJs, String modValueProfileJs) {
		// Given
		String aemResponseText = "Value should be modified. " + MODIFICATION_TARGETS;
		String expectedResult = "Value should be modified. " + MODIFICATION_TARGETS_FORMAT_STR.formatted(requireNonNullElse(modValueUtilsJs, ""), requireNonNullElse(modValueProfileJs, ""));
		runTest(endpoint, aemResponseText, expectedResult);
	}

	@Test
	void testProxyGet_Utils_Js() {
		// Given
		String endpoint = "/etc.clientlibs/clientlibs/granite/utils.js";
		String aemResponseText = "Value to be modified 'contextPath = result[1];'";
		String expectedResponseText = "Value to be modified 'contextPath = \"" + AF_BASE_LOCATION + "\" + result[1];'";
		runTest(endpoint, aemResponseText, expectedResponseText);
	}

	private void runTest(String endpoint, String inputText, String expectedResponseText) {
		stubFor(get(urlPathEqualTo(endpoint)).willReturn(okForContentType("text/plain", inputText)));
		
		// When
		// Make rest call to the proxy endpoint
		String result = restClient.get()
								  .uri(endpoint)
								  .retrieve()
								  .body(String.class);
		
		assertNotNull(result);
		assertEquals(expectedResponseText, result);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"/jcr:content/guideContainer.af.internalsubmit.jsp", 
			"/jcr:content/guideContainer.af.submit.jsp"
			})
	void testProxyPost(String endpoint) {
		String aemResponseText = "Value should be unmodified. " + MODIFICATION_TARGETS;
		stubFor(post(urlPathEqualTo(endpoint)).willReturn(okForContentType("text/plain", aemResponseText)));
		
		// When
		// Make rest call to the proxy endpoint
		String result = restClient.post()
								  .uri(endpoint)
								  .retrieve()
								  .body(String.class);
		
		assertNotNull(result);
		assertEquals(aemResponseText, result);
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
