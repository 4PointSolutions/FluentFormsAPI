package com._4point.aem.fluentforms.spring;

import javax.net.ssl.SSLContext;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ssl.NoSuchSslBundleException;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

/**
 * 
 */
public class JerseyClientFactory {
	private final static Logger logger = LoggerFactory.getLogger(JerseyClientFactory.class);

	public static Client createClient(SslBundles sslBundles, String bundleName, String username, String password) {
		return createClient(sslBundles, bundleName)
						.register(HttpAuthenticationFeature.basic(username, password))
						.register(MultiPartFeature.class);
	}

	public static Client createClient(SslBundles sslBundles, String bundleName) {
		if (sslBundles != null) {
			logger.info("SslBundles is not null");
			try {
				SslBundle bundle = sslBundles.getBundle(bundleName);
				logger.info("Client sslBundle is not null");
				SSLContext sslContext = bundle.createSslContext();
				return ClientBuilder.newBuilder().sslContext(sslContext).build();
			} catch (NoSuchSslBundleException e)  {
				// Eat the exception and fall through to the default client
				// Default the SSL context (which includes the default trust store)
			}
		}
		logger.info("Creating default client");
		return ClientBuilder.newClient();
	}
}