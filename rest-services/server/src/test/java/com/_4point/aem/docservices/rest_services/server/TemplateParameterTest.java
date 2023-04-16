package com._4point.aem.docservices.rest_services.server;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.sling.api.request.RequestParameter;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.server.TemplateParameter.ParameterType;
import com._4point.aem.fluentforms.api.PathOrUrl;

class TemplateParameterTest {

	private static final String XDP_LOCATION = TestUtils.SAMPLE_FORM.toString();
	private static final byte[] SAMPLE_XDP = readAllBytes(TestUtils.SAMPLE_FORM);
	private static final byte[] SAMPLE_PDF = readAllBytes(TestUtils.SAMPLE_PDF);
	
	private static byte[] readAllBytes(Path filePath) {
		try {
			return Files.readAllBytes(filePath);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	
	@Test
	void testReadParameter_URL() {
		TemplateParameter result = TemplateParameter.readParameter(createMockRequestParameter("text/plain", XDP_LOCATION));
		assertAll(
				()->assertEquals(ParameterType.PathOrUrl, result.getType()),
				()->assertEquals(result.getPathOrUrl(), PathOrUrl.from(XDP_LOCATION))
				);
	}

	@Test
	void testReadParameter_XDP() {
		TemplateParameter result = TemplateParameter.readParameter(createMockRequestParameter("application/vnd.adobe.xdp+xml", SAMPLE_XDP));
		assertAll(
				()->assertEquals(ParameterType.ByteArray, result.getType()),
				()->assertArrayEquals(result.getArray(), SAMPLE_XDP)
				);
	}

	@Test
	void testReadParameter_PDF() {
		TemplateParameter result = TemplateParameter.readParameter(createMockRequestParameter("application/pdf", SAMPLE_PDF));
		assertAll(
				()->assertEquals(ParameterType.ByteArray, result.getType()),
				()->assertArrayEquals(result.getArray(), SAMPLE_PDF)
				);
	}

	@Test
	void testReadParameter_NoContentType_URL() {
		TemplateParameter result = TemplateParameter.readParameter(createMockRequestParameter("text/plain", XDP_LOCATION));
		assertAll(
				()->assertEquals(ParameterType.PathOrUrl, result.getType()),
				()->assertEquals(result.getPathOrUrl(), PathOrUrl.from(XDP_LOCATION))
				);
	}

	@Test
	void testReadParameter_NoContentType_XDP() {
		TemplateParameter result = TemplateParameter.readParameter(createMockRequestParameter("application/vnd.adobe.xdp+xml", SAMPLE_XDP));
		assertAll(
				()->assertEquals(ParameterType.ByteArray, result.getType()),
				()->assertArrayEquals(result.getArray(), SAMPLE_XDP)
				);
	}

	@Test
	void testReadParameter_NoContentType_PDF() {
		TemplateParameter result = TemplateParameter.readParameter(createMockRequestParameter("application/pdf", SAMPLE_PDF));
		assertAll(
				()->assertEquals(ParameterType.ByteArray, result.getType()),
				()->assertArrayEquals(result.getArray(), SAMPLE_PDF)
				);
	}

	@Test
	void testReadParameter_BadURL() {
		String badXdpLocation = "/";
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()->TemplateParameter.readParameter(createMockRequestParameter("text/plain", badXdpLocation)));
		
		String msg = ex.getMessage();
		assertNotNull(msg);
		
		assertThat(msg, allOf(containsString(FileSystems.getDefault().getSeparator()), containsString("Template Parameter must point to an XDP file")));
	}

	private static RequestParameter createMockRequestParameter(String contentType, String stringContents) { return createMockRequestParameter(contentType, stringContents, stringContents.getBytes()); }

	private static RequestParameter createMockRequestParameter(String contentType, byte[] byteContents) { return createMockRequestParameter(contentType, null, byteContents); }
	
	private static RequestParameter createMockRequestParameter(String contentType, String stringContents, byte[] byteContents) {
		return new RequestParameter() {
			
			@Override
			public String getString() {
				return stringContents;
			}
			
			@Override
			public String getContentType() {
				return contentType;
			}
			
			@Override
			public byte[] get() {
				return byteContents;
			}
			
			@Override
			public boolean isFormField() {
				throw new UnsupportedOperationException("Not implemented");
			}
			
			@Override
			public String getString(String encoding) throws UnsupportedEncodingException {
				throw new UnsupportedOperationException("Not implemented");
			}
			
			@Override
			public long getSize() {
				throw new UnsupportedOperationException("Not implemented");
			}
			
			@Override
			public String getName() {
				throw new UnsupportedOperationException("Not implemented");
			}
			
			@Override
			public InputStream getInputStream() throws IOException {
				throw new UnsupportedOperationException("Not implemented");
			}
			
			@Override
			public String getFileName() {
				throw new UnsupportedOperationException("Not implemented");
			}
		};
	}
	
}
