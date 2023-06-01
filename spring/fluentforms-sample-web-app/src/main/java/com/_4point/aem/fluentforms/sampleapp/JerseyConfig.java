package com._4point.aem.fluentforms.sampleapp;

import java.util.Map;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com._4point.aem.fluentforms.sampleapp.resources.FluentFormsResources;

@Component
public class JerseyConfig extends ResourceConfig {
 
	public JerseyConfig() {
        registerEndpoints();
    }
 
    private void registerEndpoints() {
    	// Internal classes that contain JAX-RS Annotations
    	register(FluentFormsResources.class);

    	// Add properties that we want set
    	// Turn off Wadl generation (this was interfering with some CORS functionality
    	addProperties(Map.of("jersey.config.server.wadl.disableWadl", "true"));
    }
}