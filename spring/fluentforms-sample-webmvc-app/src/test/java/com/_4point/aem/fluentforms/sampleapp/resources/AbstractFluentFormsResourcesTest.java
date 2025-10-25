package com._4point.aem.fluentforms.sampleapp.resources;

import static com._4point.aem.fluentforms.sampleapp.resources.ResponseEntityMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class AbstractFluentFormsResourcesTest {
	private static final String APPLICATION_PDF = "application/pdf";
	private static final MediaType APPLICATION_PDF_TYPE = MediaType.valueOf(APPLICATION_PDF);

	private static final Path RESOURCES_DIR = Path.of("src", "test", "resources");
	private static final Path SAMPLE_FILES_DIR = RESOURCES_DIR.resolve("SampleFiles");
	protected static final Path SAMPLE_XDP_FILENAME_PATH = Path.of("SampleForm.xdp");

	private static final RestClient REST_CLIENT = RestClient.create();

	@LocalServerPort
	private int port;
	private final String sampleFormLocation;
	
	protected AbstractFluentFormsResourcesTest(String sampleFormLocation) {
		this.sampleFormLocation = sampleFormLocation;
	}

	@Test
	void testOutputServiceGeneratePdf_NoData() {
		URI uri = UriComponentsBuilder.fromUri(getBaseUri(port))
									  .path("/FluentForms/OutputServiceGeneratePdf")
									  .queryParam("form", sampleFormLocation)
									  .build()
									  .toUri(); 

		ResponseEntity<byte[]> response = REST_CLIENT.get()
				.uri(uri)
				.retrieve()
				.toEntity(byte[].class);

		assertThat(response, allOf(isStatus(HttpStatus.OK), isMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testOutputServiceGeneratePdf_WithData() {
		String dataKeyValue = "testOutputServiceGeneratePdf_WithData.xml";
		URI dataUri = UriComponentsBuilder.fromUri(getBaseUri(port))
				  					  .path("/FluentForms/SaveData")
									  .queryParam("dataKey", dataKeyValue)
				  					  .build()
				  					  .toUri(); 

		ResponseEntity<Void> dataResponse = REST_CLIENT.post()
													 .uri(dataUri)
													 .body(readXmlData())
													 .retrieve()
													 .toBodilessEntity();

		assertThat(dataResponse, allOf(isStatus(HttpStatus.NO_CONTENT)));

		URI uri = UriComponentsBuilder.fromUri(getBaseUri(port))
				  .path("/FluentForms/OutputServiceGeneratePdf")
				  .queryParam("form", sampleFormLocation)
				  .queryParam("dataKey", dataKeyValue)
				  .build()
				  .toUri(); 

		ResponseEntity<byte[]> response = REST_CLIENT.get()
								 .uri(uri)
								 .retrieve()
								 .toEntity(byte[].class);

		assertThat(response, allOf(isStatus(HttpStatus.OK), isMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testOutputServiceGeneratePdf_WithPostedXmlData() {
		URI uri = UriComponentsBuilder.fromUri(getBaseUri(port))
				  .path("/FluentForms/OutputServiceGeneratePdf")
				  .queryParam("form", sampleFormLocation)
				  .build()
				  .toUri(); 
		
		ResponseEntity<byte[]> response = REST_CLIENT.post()
				 .uri(uri)
				 .body(readXmlData())
				 .retrieve()
				 .toEntity(byte[].class);

		assertThat(response, allOf(isStatus(HttpStatus.OK), isMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testOutputServiceGeneratePdf_WithPostedJsonData() {
		URI uri = UriComponentsBuilder.fromUri(getBaseUri(port))
				  .path("/FluentForms/OutputServiceGeneratePdf")
				  .queryParam("form", sampleFormLocation)
				  .build()
				  .toUri(); 
		
		ResponseEntity<byte[]> response = REST_CLIENT.post()
				 .uri(uri)
				 .body(readJsonData())
				 .retrieve()
				 .toEntity(byte[].class);

		assertThat(response, allOf(isStatus(HttpStatus.OK), isMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testAdaptiveFormsServiceRenderAdaptiveForm_NoData() {
		URI uri = UriComponentsBuilder.fromUri(getBaseUri(port))
				.path("/FluentForms/AdaptiveFormsServiceRenderAdaptiveForm")
				.queryParam("form", "sample00002test")
				.build()
				.toUri(); 

		ResponseEntity<byte[]> response = REST_CLIENT.get()
								 .uri(uri)
								 .retrieve()
								 .toEntity(byte[].class);

		assertThat(response, allOf(isStatus(HttpStatus.OK), hasMediaType(isCompatibleWith(MediaType.TEXT_HTML))));
	}

	@Test
	void testAdaptiveFormsServiceRenderAdaptiveForm_WithData() {
		String dataKeyValue = "testAdaptiveFormsServiceRenderAdaptiveForm_WithData";
		URI dataUri = UriComponentsBuilder.fromUri(getBaseUri(port))
				  					  .path("/FluentForms/SaveData")
									  .queryParam("dataKey", dataKeyValue)
				  					  .build()
				  					  .toUri(); 

		ResponseEntity<Void> dataResponse = REST_CLIENT.post()
													 .uri(dataUri)
													 .body(readXmlData())
													 .retrieve()
													 .toBodilessEntity();

		assertThat(dataResponse, allOf(isStatus(HttpStatus.NO_CONTENT)));

		URI uri = UriComponentsBuilder.fromUri(getBaseUri(port))
				.path("/FluentForms/AdaptiveFormsServiceRenderAdaptiveForm")
				.queryParam("form", "sample00002test")
				.queryParam("dataKey", dataKeyValue)
				.build()
				.toUri(); 

		ResponseEntity<byte[]> response = REST_CLIENT.get()
								 .uri(uri)
								 .retrieve()
								 .toEntity(byte[].class);

		assertThat(response, allOf(isStatus(HttpStatus.OK), hasMediaType(isCompatibleWith(MediaType.TEXT_HTML))));
	}

	@Test
	void testHtml5FormsServiceRenderHtml5Form_NoData() {
		URI uri = UriComponentsBuilder.fromUri(getBaseUri(port))
				.path("/FluentForms/Html5FormsServiceRenderHtml5Form")
				.queryParam("form", sampleFormLocation)
				.build()
				.toUri(); 

		ResponseEntity<byte[]> response = REST_CLIENT.get()
								 .uri(uri)
								 .retrieve()
								 .toEntity(byte[].class);

		assertThat(response, allOf(isStatus(HttpStatus.OK), hasMediaType(isCompatibleWith(MediaType.TEXT_HTML))));
	}

	@Test
	void testHtml5FormsServiceRenderHtmlForm_WithData() {
		String dataKeyValue = "testHtml5FormsServiceRenderHtmlForm_WithData";
		URI dataUri = UriComponentsBuilder.fromUri(getBaseUri(port))
				  					  .path("/FluentForms/SaveData")
									  .queryParam("dataKey", dataKeyValue)
				  					  .build()
				  					  .toUri(); 

		ResponseEntity<Void> dataResponse = REST_CLIENT.post()
													 .uri(dataUri)
													 .body(readXmlData())
													 .retrieve()
													 .toBodilessEntity();

		assertThat(dataResponse, allOf(isStatus(HttpStatus.NO_CONTENT)));

		URI uri = UriComponentsBuilder.fromUri(getBaseUri(port))
				.path("/FluentForms/Html5FormsServiceRenderHtml5Form")
				.queryParam("form", sampleFormLocation)
				.queryParam("dataKey", dataKeyValue)
				.build()
				.toUri(); 

		ResponseEntity<byte[]> response = REST_CLIENT.get()
								 .uri(uri)
								 .retrieve()
								 .toEntity(byte[].class);

		assertThat(response, allOf(isStatus(HttpStatus.OK), hasMediaType(isCompatibleWith(MediaType.TEXT_HTML))));
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
		URI dataUri = UriComponentsBuilder.fromUri(getBaseUri(port))
				.path("/FluentForms/SaveData")
				.queryParam("dataKey", "saveDataKey_testSaveData")
				  					  .build()
				  					  .toUri(); 

		ResponseEntity<Void> dataResponse = REST_CLIENT.post()
													 .uri(dataUri)
													 .body("<root/>")
													 .retrieve()
													 .toBodilessEntity();

		assertThat(dataResponse, allOf(isStatus(HttpStatus.NO_CONTENT)));
	}

	protected static String getBaseUriString(int port) {
		return getBaseUri(port).toString();
	}

	private static URI getBaseUri(int port) {
		return URI.create("http://localhost:" + port);
	}
}
