package com._4point.aem.fluentforms.impl;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * This class is required to enable the use if "crx:" as a protocol with the java.net.URL object.  It instantiates and
 * registers an URLStreamHandler for the "crx:" protocol.  Once that is done, we can turn url strings that contain crx:// into
 * URL objects. 
 *
 */
public class CrxUrlHandler {
	
	private static boolean crxProtocolInstalled = false;

	public static void enableCrxProtocol() {
		if (!crxProtocolInstalled) {	// Only do this once.
			URL.setURLStreamHandlerFactory(protocol -> "crx".equals(protocol) ? new URLStreamHandler() {
			    protected URLConnection openConnection(URL url) throws IOException {
			        return new URLConnection(url) {
			            public void connect() throws IOException {
			            	// This should never happen!
			                throw new UnsupportedOperationException("Attempt to connect to URL using crx protocol detected.  crx: URLs should only be used by AEM!");
			            }
			        };
			    }
			} : null);
			crxProtocolInstalled = true;
		}
	}

	public static boolean isCrxProtocolInstalled() {
		return crxProtocolInstalled;
	}
}
