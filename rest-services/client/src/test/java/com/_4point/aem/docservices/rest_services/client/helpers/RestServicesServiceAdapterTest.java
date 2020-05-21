package com._4point.aem.docservices.rest_services.client.helpers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter.RestServicesServiceException;

class RestServicesServiceAdapterTest {

	@Test
	void testPostToServer_ConnectException() throws Exception {
		// Point to a server that does not exist.
		String expectedUri = "http://localhost:2/";
		String expectedPath = "foobar";
		WebTarget target = ClientBuilder.newClient()
										.register(MultiPartFeature.class)
										.target(expectedUri)
										.path(expectedPath);
		RestServicesServiceAdapter underTest = new RestServicesServiceAdapter(target) {
		};
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field("SomeField", "SomeFieldValue");
			
			RestServicesServiceException ex = assertThrows(RestServicesServiceException.class, ()->underTest.postToServer(target, multipart, MediaType.APPLICATION_OCTET_STREAM_TYPE));
			String msg = ex.getMessage();
			assertNotNull(msg);
			assertAll(
					()->assertTrue(msg.contains(expectedUri), "Expected exception message '" + msg + "' to contain uri '" + expectedUri + "'"),
					()->assertTrue(msg.contains(expectedPath), "Expected exception message '" + msg + "' to contain path '" + expectedPath + "'")
					);
		}
	}

	@Disabled
	void testInputStreamtoString() {
		fail("Not yet implemented");
	}

	
}
