package com._4point.aem.docservices.rest_services.it_tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

public class TestUtils {

	private static final String SAMPLE_FORM_PDF_NAME = "SampleForm.pdf";
	private static final String SAMPLE_FORM_XDP_NAME = "SampleForm.xdp";
	private static final String SAMPLE_FORM_DATA_XML_NAME = "SampleForm_data.xml";

	private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	
	public static final Path SAMPLE_FORM_PDF = getPath(SAMPLE_FORM_PDF_NAME);
	public static final Path SAMPLE_FORM_XDP = getPath(SAMPLE_FORM_XDP_NAME);
	public static final Path SAMPLE_FORM_DATA_XML = getPath(SAMPLE_FORM_DATA_XML_NAME);
	
	public static final Path RESOURCES_DIR = Paths.get("src", "test", "resources");
	public static final Path ACTUAL_RESULTS_DIR = RESOURCES_DIR.resolve("actualResults");
	public static final Path SERVER_FORMS_DIR = Paths.get("D:", "FluentForms", "Forms");

	private static Path getPath(String name) {
		try {
			return Paths.get(classLoader.getResource(name).toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("getResource returned invalid URI. (This should never happen!)", e);
		}
	}
	
	public static String readEntityToString(Response result) {
		try {
			return IOUtils.toString((InputStream)result.getEntity(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Exception while reading response stream.", e);
		}
	}
}
