package com._4point.aem.fluentforms.impl;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

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
