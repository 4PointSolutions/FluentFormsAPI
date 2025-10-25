package com._4point.aem.fluentforms.sampleapp.resources;

import static com._4point.testing.matchers.jaxrs.ResponseMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public abstract class AbstractFluentFormsResourcesTest {
	private static final String APPLICATION_PDF = "application/pdf";
	private static final MediaType APPLICATION_PDF_TYPE = MediaType.valueOf(APPLICATION_PDF);

	private static final Path RESOURCES_DIR = Path.of("src", "test", "resources");
	private static final Path SAMPLE_FILES_DIR = RESOURCES_DIR.resolve("SampleFiles");
	protected static final Path SAMPLE_XDP_FILENAME_PATH = Path.of("SampleForm.xdp");

	@LocalServerPort
	private int port;
	private final String sampleFormLocation;
	
	protected AbstractFluentFormsResourcesTest(String sampleFormLocation) {
		this.sampleFormLocation = sampleFormLocation;
	}

	@Test
	void testOutputServiceGeneratePdf_NoData() {
		Response response = ClientBuilder.newClient()
										 .target(getBaseUri(port))
										 .path("/FluentForms/OutputServiceGeneratePdf")
										 .queryParam("form", sampleFormLocation)
										 .request()
										 .get();
		
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testOutputServiceGeneratePdf_WithData() {
		Client client = ClientBuilder.newClient();
		String dataKeyValue = "testOutputServiceGeneratePdf_WithData.xml";
		Response dataResponse = client.target(getBaseUri(port))
								  .path("/FluentForms/SaveData")
								  .queryParam("dataKey", dataKeyValue)
								  .request()
								  .post(Entity.xml(readXmlData()));
		assertThat(dataResponse, allOf(isStatus(Status.NO_CONTENT)));

		Response response = ClientBuilder.newClient()
				 .target(getBaseUri(port))
				 .path("/FluentForms/OutputServiceGeneratePdf")
				 .queryParam("form", sampleFormLocation)
				 .queryParam("dataKey", dataKeyValue)
				 .request()
				 .get();

		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testOutputServiceGeneratePdf_WithPostedXmlData() {
		Response response = ClientBuilder.newClient()
				 .target(getBaseUri(port))
				 .path("/FluentForms/OutputServiceGeneratePdf")
				 .queryParam("form", sampleFormLocation)
				 .request()
				 .post(Entity.xml(readXmlData()));

		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testOutputServiceGeneratePdf_WithPostedJsonData() {
		Response response = ClientBuilder.newClient()
				 .target(getBaseUri(port))
				 .path("/FluentForms/OutputServiceGeneratePdf")
				 .queryParam("form", sampleFormLocation)
				 .request()
				 .post(Entity.json(readJsonData()));

		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testAdaptiveFormsServiceRenderAdaptiveForm_NoData() {
		Response response = ClientBuilder.newClient()
										 .target(getBaseUri(port))
										 .path("/FluentForms/AdaptiveFormsServiceRenderAdaptiveForm")
										 .queryParam("form", "sample00002test")
										 .request()
										 .get();
		
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE)));
	}

	@Test
	void testAdaptiveFormsServiceRenderAdaptiveForm_WithData() {
		Client client = ClientBuilder.newClient();
		String dataKeyValue = "testAdaptiveFormsServiceRenderAdaptiveForm_WithData";
		Response dataResponse = client.target(getBaseUri(port))
								  .path("/FluentForms/SaveData")
								  .queryParam("dataKey", dataKeyValue)
								  .request()
								  .post(Entity.xml(readXmlData()));
		assertThat(dataResponse, allOf(isStatus(Status.NO_CONTENT)));

		Response response = client.target(getBaseUri(port))
								  .path("/FluentForms/AdaptiveFormsServiceRenderAdaptiveForm")
								  .queryParam("form", "sample00002test")
								  .queryParam("dataKey", dataKeyValue)
								  .request()
								  .get();
		
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE)));
	}

	@Test
	void testHtml5FormsServiceRenderHtml5Form_NoData() {
		Response response = ClientBuilder.newClient()
										 .target(getBaseUri(port))
										 .path("/FluentForms/Html5FormsServiceRenderHtml5Form")
										 .queryParam("form", sampleFormLocation)
										 .request()
										 .get();
		
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE)));
	}

	@Test
	void testHtml5FormsServiceRenderHtmlForm_WithData() {
		Client client = ClientBuilder.newClient();
		String dataKeyValue = "testHtml5FormsServiceRenderHtmlForm_WithData";
		Response dataResponse = client.target(getBaseUri(port))
								  .path("/FluentForms/SaveData")
								  .queryParam("dataKey", dataKeyValue)
								  .request()
								  .post(Entity.xml(readXmlData()));
		assertThat(dataResponse, allOf(isStatus(Status.NO_CONTENT)));

		Response response = client.target(getBaseUri(port))
								  .path("/FluentForms/Html5FormsServiceRenderHtml5Form")
								  .queryParam("form", sampleFormLocation)
								  .queryParam("dataKey", dataKeyValue)
								  .request()
								  .get();
		
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE)));
	}

	private byte[] readXmlData() {
		Path sampleData = SAMPLE_FILES_DIR.resolve("SampleForm_data.xml");
		try {
			return Files.readAllBytes(sampleData);
		} catch (IOException e) {
			throw new IllegalStateException("Error while reading sample data (%s)".formatted(sampleData.toAbsolutePath()), e);
		}
	}
	
	private byte[] readJsonData() {
		Path sampleData = SAMPLE_FILES_DIR.resolve("SampleForm_data.json");
		try {
			return Files.readAllBytes(sampleData);
		} catch (IOException e) {
			throw new IllegalStateException("Error while reading sample data (%s)".formatted(sampleData.toAbsolutePath()), e);
		}
	}

	@Test
	void testSaveData() {
		Response response = ClientBuilder.newClient()
										 .target(getBaseUri(port))
										 .path("/FluentForms/SaveData")
										 .queryParam("dataKey", "saveDataKey_testSaveData")
										 .request()
										 .post(Entity.xml("<root/>"));
		
		assertThat(response, allOf(isStatus(Status.NO_CONTENT)));
	}

	protected static String getBaseUriString(int port) {
		return getBaseUri(port).toString();
	}

	private static URI getBaseUri(int port) {
		return URI.create("http://localhost:" + port);
	}
}
