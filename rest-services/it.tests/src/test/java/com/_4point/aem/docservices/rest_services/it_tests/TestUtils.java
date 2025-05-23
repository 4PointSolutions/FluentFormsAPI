package com._4point.aem.docservices.rest_services.it_tests;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.function.Executable;

import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.it_tests.Pdf.PdfException;

public class TestUtils {
	
	public static final String TEST_MACHINE_NAME = "localhost"; //"172.22.132.85";
	public static final AemServerType TEST_MACHINE_AEM_TYPE = AemServerType.StandardType.OSGI;
	public static final int TEST_MACHINE_PORT = 4502;
	public static final String TEST_MACHINE_PORT_STR = Integer.toString(TEST_MACHINE_PORT);
	public static final String TEST_USER = "admin";
	public static final String TEST_USER_PASSWORD = "admin";
	
	public enum AemTargetType {
		LOCAL,			// Running on local machine (assumes that the port is TEST_MACHINE_PORT)	
		REMOTE_WINDOWS, // Running on remote Windows machine (assumes that machine name is TEST_MACHINE_NAME and port is TEST_MACHINE_PORT) 
		REMOTE_LINUX, 	// Running on remote Linux machine (assumes that machine name is TEST_MACHINE_NAME and port is TEST_MACHINE_PORT)
		TESTCONTAINERS; // Running on local testcontainers image (gets port from TestContainers)
	}
	
	// Set this to indicate the type of machine that AEM is running on:
	//  The integration tests will run against an AEM instance running in a Docker container otherwise they will run against a local AEM instance.
	public static final AemTargetType AEM_TARGET_TYPE = AemTargetType.LOCAL; 
	// Status of LOCAL AEM Target support.
	//		AEM Integration test issues:
	//		* Sample JSON Adaptive Form not deployed - Causes JSON AF Rendering test to fail
	//		* Sample XDP not copied to target file system - Causes GeneratePdfOututTest "For mWindows Machine" rendering test failures
	//		* Reader Extensions not configured - Causes Reader Extensions (Secure Document) tests to fail
	//		* OpenOffice not installed - causes GeneratePdf tests to fail
	//
	// Status of TestContainers support.
	//		AEM Integration test issues (in addition to those above)::
	//		* Sample Adaptive Form not deployed - Causes AF Rendering test to fail (should be fixed in latest AEM image)
	//		* JSAFE not configured - Causes Assembler test to fail (should be fixed in latest AEM image)
	//		* Sample XDP not deployed - Causes HTML5, PDF and Print rendering tests to fail (should be fixed in latest AEM image)
	//		* GeneratePrintedOutput test AllArgs seems to want D:\FluentForms\Forms - Need to fix tests
	//		* Generate HTLML5 test fails because proteted mode is on - Need to fix AEM image creation code to set this.
	
	private static final String SAMPLE_FORM_PDF_NAME = "SampleForm.pdf";
	private static final String SAMPLE_FORM_XDP_NAME = "SampleForm.xdp";
	private static final String SAMPLE_FORM_WITH_DATA_PDF_NAME = "SampleFormWithData.pdf";
	private static final String SAMPLE_FORM_WITHOUT_DATA_PDF_NAME = "SampleFormNonInteractive.pdf";
	private static final String SAMPLE_FORM_DATA_XML_NAME = "SampleForm_data.xml";
	private static final String SAMPLE_FORM_DATA_JSON_NAME = "SampleForm_data.json";
	private static final String SAMPLE_FORM_DATA_DOCX_NAME = "SampleForm.docx";
	private static final String SAMPLE_FORM_DDX_NAME = "SampleForm_DDX.xml";
	private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	
	public static final Path SAMPLE_FORM_PDF = getPath(SAMPLE_FORM_PDF_NAME);
	public static final Path SAMPLE_FORM_WITH_DATA_PDF = getPath(SAMPLE_FORM_WITH_DATA_PDF_NAME);
	public static final Path SAMPLE_FORM_WITHOUT_DATA_PDF = getPath(SAMPLE_FORM_WITHOUT_DATA_PDF_NAME);
	public static final Path SAMPLE_FORM_XDP = getPath(SAMPLE_FORM_XDP_NAME);
	public static final Path SAMPLE_FORM_DATA_XML = getPath(SAMPLE_FORM_DATA_XML_NAME);
	public static final Path SAMPLE_FORM_DATA_JSON = getPath(SAMPLE_FORM_DATA_JSON_NAME);
	public final static Path SAMPLE_FORM_DOCX =  getPath(SAMPLE_FORM_DATA_DOCX_NAME);
	public static final Path SAMPLE_FORM_DDX = getPath(SAMPLE_FORM_DDX_NAME);
	
	public static final Path RESOURCES_DIR = Paths.get("src", "test", "resources");
	public static final Path ACTUAL_RESULTS_DIR = RESOURCES_DIR.resolve("actualResults");
	public static final Path SERVER_FORMS_DIR = Paths.get("D:", "FluentForms", "Forms");

	private static final boolean SAVE_RESULTS = false;

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
	
	@SuppressWarnings("unused")
	public static void validatePdfResult(byte[] pdfBytes, String testResultFilename, boolean dynamic, boolean interactive, boolean hasRights)
			throws IOException, Exception, PdfException {
		if (SAVE_RESULTS == true) {
			IOUtils.write(pdfBytes, Files.newOutputStream(ACTUAL_RESULTS_DIR.resolve(testResultFilename)));
		}
		assertThat("Expected a PDF to be returned.", ByteArrayString.toString(pdfBytes, 8), containsString("%, P, D, F, -, 1, ., 7"));
		try (Pdf pdf = Pdf.from(pdfBytes)) {
			Executable dynamicTest = dynamic ? ()->assertTrue(pdf.isDynamic(), "Expected Pdf to be dynamic.") : ()->assertFalse(pdf.isDynamic(), "Expected Pdf to be static.");
			Executable interactiveTest = interactive ? ()->assertTrue(pdf.isInteractive(), "Expected Pdf to be interactive.") : ()->assertFalse(pdf.isInteractive(), "Expected Pdf to be non-interactive.");
			Executable rightsTest = hasRights ? ()->assertTrue(pdf.hasRights(), "Expected Pdf to have Usage Rights.") : ()->assertFalse(pdf.hasRights(), "Expected Pdf to have no Usage Rights.");
			assertAll(dynamicTest, interactiveTest, rightsTest);
		}
	}
	
}
	

