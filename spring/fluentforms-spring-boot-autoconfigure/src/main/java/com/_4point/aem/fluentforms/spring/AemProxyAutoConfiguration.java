package com._4point.aem.fluentforms.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * AutoConfiguration for the Reverse Proxy Library which reverse proxies secondary
 * resources (.css, .js, etc.) that the browser will request.  These requests are forwarded to AEM.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type=Type.SERVLET)
@ConditionalOnProperty(prefix="fluentforms.rproxy", name="enabled", havingValue="true", matchIfMissing=true )
@EnableConfigurationProperties({AemConfiguration.class, AemProxyConfiguration.class})
public class AemProxyAutoConfiguration {

	@Bean
	public ResourceConfigCustomizer afProxyConfigurer(AemConfiguration aemConfig, AemProxyConfiguration aemProxyConfig) {
		return config->config.register(new AemProxyEndpoint(aemConfig, aemProxyConfig));
	}
}
