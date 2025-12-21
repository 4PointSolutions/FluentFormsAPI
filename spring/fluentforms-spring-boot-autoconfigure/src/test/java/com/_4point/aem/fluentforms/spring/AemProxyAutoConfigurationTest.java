package com._4point.aem.fluentforms.spring;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.catalina.connector.Connector;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;


class AemProxyAutoConfigurationTest {
	static final int MINIMUM_PARTS_COUNT = 20;

	// TODO:  Maybe add more tests here later.
//	@Test
//	void testDocumentFactory(@Autowired ResourceConfigCustomizer afProxyConfigurer) {
//		assertNotNull(afProxyConfigurer);
//	}


	@SpringBootTest(
			classes = {com._4point.aem.fluentforms.spring.FluentFormsAutoConfigurationTest.TestApplication.class, FluentFormsAutoConfiguration.class, AemProxyAutoConfiguration.class}, 
			properties = {
					"fluentforms.aem.servername=localhost", 
					"fluentforms.aem.port=4502", 
					"fluentforms.aem.user=admin",		 
					"fluentforms.aem.password=admin)",
					})

	static class AemProxyAutoConfiguration_TomcatWenServerFactory_MaxPartsCount_Test {
		
		private static @Nullable TomcatConnectorCustomizer customizer;
		private Connector mockConnector = Mockito.mock(Connector.class);
		
		@BeforeAll
		static void setup(@Autowired WebServerFactoryCustomizer<TomcatServletWebServerFactory> webserverFactoryCustomizer) {
			assertNotNull(webserverFactoryCustomizer);
			customizer = retrieveTomcatCustomizer(webserverFactoryCustomizer);
		}

		// This routine emulates the way Spring retrieves the customizer.  It ensures that the customizer is 
		// configured so that Spring can find it.
		private static TomcatConnectorCustomizer retrieveTomcatCustomizer(WebServerFactoryCustomizer<TomcatServletWebServerFactory> webserverFactoryCustomizer) {
			TomcatServletWebServerFactory tomcatFactory = mock(TomcatServletWebServerFactory.class);
			ArgumentCaptor<TomcatConnectorCustomizer> customizerCaptor = ArgumentCaptor.forClass(TomcatConnectorCustomizer.class);
			doNothing().when(tomcatFactory).addConnectorCustomizers(customizerCaptor.capture());
			webserverFactoryCustomizer.customize(tomcatFactory);
			return customizerCaptor.getValue();
		}
		
		@ParameterizedTest
		@DisplayName("If greater than or equal to the minimum, value should be left unaltered.")
		@ValueSource(ints = {30, -1, MINIMUM_PARTS_COUNT}) // All greater than or equal to the minimum
		void testTomcatMaxPartCountSetting_NoChange(int getValue) {
			// Given: Mock just the get()
			when(mockConnector.getMaxPartCount()).thenReturn(getValue);
			
			// When
			requireNonNull(customizer).customize(mockConnector);
			
			//Then
			verify(mockConnector).getMaxPartCount();
			verify(mockConnector, Mockito.times(0)).setMaxPartCount(Mockito.anyInt());
		}
		
		@ParameterizedTest
		@DisplayName("If lower than the minimum, value should be set to minimum.")
		@ValueSource(ints = {0, 10})	// All lower than minimum
		void testTomcatMaxPartCountSetting(int getValue) {
			// Given: Mock the get() and the set()
			when(mockConnector.getMaxPartCount()).thenReturn(getValue);
			ArgumentCaptor<Integer> updatedMaxPartCountCaptor = ArgumentCaptor.forClass(Integer.class);
			doNothing().when(mockConnector).setMaxPartCount(updatedMaxPartCountCaptor.capture());
			
			// When
			requireNonNull(customizer).customize(mockConnector );

			// Then
			verify(mockConnector).getMaxPartCount();
			verify(mockConnector).setMaxPartCount(MINIMUM_PARTS_COUNT);
			assertEquals(updatedMaxPartCountCaptor.getValue(), MINIMUM_PARTS_COUNT);
		}
	}

	@SpringBootApplication
	@EnableConfigurationProperties({AemConfiguration.class,AemProxyConfiguration.class})
	public static class TestApplication {
		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}

	}
}
