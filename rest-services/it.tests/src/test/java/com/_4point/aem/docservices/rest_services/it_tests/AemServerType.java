package com._4point.aem.docservices.rest_services.it_tests;

public enum AemServerType {
	JEE("/lc"), 
	OSGI("");
	
	private final String pathPrefix;

	private AemServerType(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}

	public final String pathPrefix() {
		return this.pathPrefix;
	}

}
