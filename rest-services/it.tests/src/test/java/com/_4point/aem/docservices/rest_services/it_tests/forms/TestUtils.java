package com._4point.aem.docservices.rest_services.it_tests.forms;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

	private static Path getPath(String name) {
		try {
			return Paths.get(classLoader.getResource(name).toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("getResource returned invalid URI. (This should never happen!)", e);
		}
	}
}
