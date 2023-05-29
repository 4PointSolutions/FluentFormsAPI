package com._4point.aem.fluentforms.sampleapp.resources;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com._4point.aem.fluentforms.sampleapp.FluentFormsSpringApplication;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = FluentFormsSpringApplication.class)
class FluentFormsResourcesTest {

	@LocalServerPort
	private int port;

	@Test
	void testOutputServiceGeneratePdf() {
		Response response = ClientBuilder.newClient()
										 .target("http://localhost:" + port)
										 .path("/FluentForms/OutputServiceGeneratePdf")
										 .queryParam("formName", "formName")
										 .request()
										 .get();
		
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}
