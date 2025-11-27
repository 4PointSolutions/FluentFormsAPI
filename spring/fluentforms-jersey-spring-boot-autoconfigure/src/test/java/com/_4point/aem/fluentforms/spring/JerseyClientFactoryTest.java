package com._4point.aem.fluentforms.spring;

import static org.junit.jupiter.api.Assertions.*;

import javax.net.ssl.SSLContext;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Configuration;

@ExtendWith(MockitoExtension.class)
class JerseyClientFactoryTest {

	@Test
	void testCreateClientSslBundlesStringStringString(@Mock SslBundles mockSslBundles, 
													  @Captor ArgumentCaptor<String> bundleName,
													  @Mock SslBundle mockSslBundle,
													  @Mock SSLContext mockSslContext
													  ) {
		Mockito.when(mockSslBundles.getBundle(bundleName.capture())).thenReturn(mockSslBundle);
		Mockito.when(mockSslBundle.createSslContext()).thenReturn(mockSslContext);
		
		String expectedBundleName = "expectedBundle";
		
		Client client = JerseyClientFactory.createClient(mockSslBundles, expectedBundleName, "user", "password");
		
		Configuration configuration = client.getConfiguration();
		assertAll(
				()->assertEquals(expectedBundleName, bundleName.getValue()),
				()->assertSame(mockSslContext, client.getSslContext()),
				()->assertTrue(configuration.isRegistered(MultiPartFeature.class), "MultiPartFeature should be registered."),
				()->assertTrue(configuration.isRegistered(HttpAuthenticationFeature.class), "HttpAuthenticationFeature should be registered.")
				);
	}

	@Test
	void testCreateClientSslBundlesString(@Mock SslBundles mockSslBundles, 
										  @Captor ArgumentCaptor<String> bundleName,
										  @Mock SslBundle mockSslBundle,
										  @Mock SSLContext mockSslContext
										  ) {
		Mockito.when(mockSslBundles.getBundle(bundleName.capture())).thenReturn(mockSslBundle);
		Mockito.when(mockSslBundle.createSslContext()).thenReturn(mockSslContext);
		
		String expectedBundleName = "expectedBundle";
		
		Client client = JerseyClientFactory.createClient(mockSslBundles, expectedBundleName);
		
		assertAll(
				()->assertEquals(expectedBundleName, bundleName.getValue()),
				()->assertSame(mockSslContext, client.getSslContext())
				);
	}

	@Test
	void testCreateClient_NullSslBundles_NullString() throws Exception {
		Client client = JerseyClientFactory.createClient(null, null);
		
		assertNotNull(client.getSslContext());
	}

}