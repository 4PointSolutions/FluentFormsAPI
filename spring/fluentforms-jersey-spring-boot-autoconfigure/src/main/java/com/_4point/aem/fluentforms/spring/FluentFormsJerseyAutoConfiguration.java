package com._4point.aem.fluentforms.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com._4point.aem.docservices.rest_services.client.helpers.Builder.RestClientFactory;
import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;

import jakarta.ws.rs.client.Client;

/**
 * AutoConfiguration for the FluentForms Rest Services Client library using the Jersey Rest Client.
 * 
 * This class automatically configures a set of beans (one for each AEM service) that can be injected
 * into any Spring Boot code. 
 *
 */
@Lazy
@AutoConfiguration
@EnableConfigurationProperties(AemConfiguration.class)
public class FluentFormsJerseyAutoConfiguration {
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(org.glassfish.jersey.client.JerseyClient.class)
	public static class JerseyRestClientConfiguration {

		@ConditionalOnProperty(prefix="fluentforms", name="restclient", havingValue="jersey", matchIfMissing=true )
		@ConditionalOnMissingBean
		@Bean
		public RestClientFactory jerseyRestClientFactory(AemConfiguration aemConfig, @Autowired(required = false) SslBundles sslBundles) {
			Client jerseyClient = JerseyClientFactory.createClient(sslBundles, aemConfig.sslBundle());	// Create custom Jersey Client with SSL bundle
			return JerseyRestClient.factory(jerseyClient); // Create a RestClientFactory using JerseyClient implementation
		}
		
	}
}
