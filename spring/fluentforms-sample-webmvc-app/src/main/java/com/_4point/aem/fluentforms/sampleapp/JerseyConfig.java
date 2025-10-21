package com._4point.aem.fluentforms.sampleapp;

import java.util.Map;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		// Add properties that we want set
    	addProperties(Map.of(
			    			// Turn off Wadl generation (this was interfering with some CORS functionality
    						 "jersey.config.server.wadl.disableWadl", true, 
    						 "jersey.config.server.response.setStatusOverSendError", true,
    						 // See https://docs.spring.io/spring-boot/how-to/jersey.html#howto .jersey.alongside-another-web-framework
    						 ServletProperties.FILTER_FORWARD_ON_404, true
    						));
	}

}