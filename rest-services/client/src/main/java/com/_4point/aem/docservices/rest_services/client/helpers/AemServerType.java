package com._4point.aem.docservices.rest_services.client.helpers;

/**
 * This interface can be implemented by client code if the standard types are not suitable.
 */
public interface AemServerType {
	/**
	 * Return a prefix that is placed in front of the /services portion of the path part of the service URL
	 * On most JEE servers, this would be "/lc" and on OSGi installations it would be empty string.
	 *  
	 * @return String that will be prefixed on the URL path.
	 */
	public String pathPrefix();
	
	/**
	 * Standard types that should cover the most common use cases. 
	 */
	public enum StandardType implements AemServerType {
		JEE("/lc"), 
		OSGI("");
		
		private final String pathPrefix;

		private StandardType(String pathPrefix) {
			this.pathPrefix = pathPrefix;
		}

		public final String pathPrefix() {
			return this.pathPrefix;
		}
	}
}

