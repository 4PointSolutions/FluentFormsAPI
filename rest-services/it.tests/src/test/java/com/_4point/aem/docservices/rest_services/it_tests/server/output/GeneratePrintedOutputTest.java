package com._4point.aem.docservices.rest_services.it_tests.server.output;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.RESOURCES_DIR;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_DATA_XML;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_XDP;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SERVER_FORMS_DIR;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_AEM_TYPE;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_NAME;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_PORT_STR;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;

public class GeneratePrintedOutputTest {

	private static final String GENERATE_PRINTED_OUTPUT_PATH = TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/OutputService/GeneratePrintedOutput";
	private static final String GENERATE_PRINTED_OUTPUT_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + GENERATE_PRINTED_OUTPUT_PATH;
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final MediaType APPLICATION_XDP = new MediaType("application", "vnd.adobe.xdp+xml");
	private static final MediaType APPLICATION_DPL = new MediaType("application", "vnd.datamax-dpl");
	private static final MediaType APPLICATION_IPL = new MediaType("application", "vnd.intermec-ipl");
	private static final MediaType APPLICATION_PCL = new MediaType("application", "vnd.hp-pcl");
	private static final MediaType APPLICATION_PS = new MediaType("application", "postscript");
	private static final MediaType APPLICATION_TPCL = new MediaType("application", "vnd.toshiba-tpcl");
	private static final MediaType APPLICATION_ZPL = new MediaType("x-application", "zpl");
	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms";

	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";
	private static final String CONTENT_ROOT_PARAM = "outputOptions.contentRoot";
	private static final String COPIES_PARAM = "outputOptions.copies";
	private static final String DEBUG_DIR_PARAM = "outputOptions.debugDir";
	private static final String LOCALE_PARAM = "outputOptions.locale";
	private static final String PAGINATION_OVERRIDE_PARAM = "outputOptions.paginationOverride";
	private static final String PRINT_CONFIG_PARAM = "outputOptions.printConfig";
	private static final String XCI_PARAM = "outputOptions.xci";

	private static final boolean SAVE_OUTPUT = false;
	
	private WebTarget target;
	
	private enum Printer {
		DPL300("DPL300", APPLICATION_DPL),
		DPL406("DPL406", APPLICATION_DPL),
		DPL600("DPL600", APPLICATION_DPL),
		Generic_PS_L3("Generic_PS_L3", APPLICATION_PS          ),
		GenericColor_PCL_5e("GenericColor_PCL_5c", APPLICATION_PCL),
		HP_PCL_5e("HP_PCL_5e", APPLICATION_PCL),
		IPL300("IPL300", APPLICATION_IPL),
		IPL400("IPL400", APPLICATION_IPL),
		PS_PLAIN("PS_PLAIN", APPLICATION_PS),
		TPCL305("TPCL305", APPLICATION_TPCL),
		TPCL600("TPCL600", APPLICATION_TPCL),
		ZPL300("ZPL300", APPLICATION_ZPL),
		ZPL600("ZPL600", APPLICATION_ZPL);
		
		private final String printConfig;
		private final MediaType mediaType;
		
		private Printer(String printConfig, MediaType mediaType) {
			this.printConfig = printConfig;
			this.mediaType = mediaType;
		}

		public String getPrintConfig() {
			return printConfig;
		}

		public MediaType getMediaType() {
			return mediaType;
		}
	}

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD); // default AEM passwords
		target = ClientBuilder.newClient().register(feature).register(MultiPartFeature.class)
				.target(GENERATE_PRINTED_OUTPUT_URL);
	}

	@ParameterizedTest
	@EnumSource(Printer.class)
	void testGeneratePrintedOutput_AllArgs(Printer printer) throws Exception {
		String printConfig = printer.getPrintConfig();
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, SERVER_FORMS_DIR.resolve(SAMPLE_FORM_XDP).toString())
					 .field(COPIES_PARAM, "1")
//					 .field(DEBUG_DIR_PARAM, "")	We don't want to generate debug outputs.
					 .field(LOCALE_PARAM, "en-CA")
					 .field(PAGINATION_OVERRIDE_PARAM, "simplex")
					 .field(PRINT_CONFIG_PARAM, printConfig)
					 .field(CONTENT_ROOT_PARAM, SERVER_FORMS_DIR.toString())
					 .field(XCI_PARAM, RESOURCES_DIR.resolve("pa.xci").toFile(), MediaType.APPLICATION_XML_TYPE)
					 ;

			Response result = target.request()
									.accept(printer.getMediaType())
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			assertEquals(printer.getMediaType(), MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));

			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			validateResult(resultBytes, "GeneratePrintedOutput_AllArgsResult_" + printConfig + ".txt", printConfig);
		}
	}
	
	public static void validateResult(byte[] output, String testResultFilename, String printConfig) throws IOException {
		if (SAVE_OUTPUT == true) {
			IOUtils.write(output, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve(testResultFilename)));
		}
		switch (printConfig) {
		case "DPL300":
		case "DPL406":
		case "DPL600":
			assertThat("Expected DPL to be returned.", ByteArrayString.toString(output, 5), containsString(", y, S, W, 1"));
			assertThat("Expected DPL to be returned.", ByteArrayString.toString(output, 10), containsString(", n"));
			break;
		case "Generic_PS_L3":
		case "PS_PLAIN":
			assertThat("Expected PostScript to be returned.", ByteArrayString.toString(output, 10), containsString("%, !, P, S, -, A, d, o, b, e"));
			break;
		case "GenericColor_PCL_5c":
		case "HP_PCL_5e":
			assertThat("Expected PCL to be returned.", ByteArrayString.toString(output, 15), containsString("%, -, 1, 2, 3, 4, 5, X, @, P, J, L"));
			break;
		case "IPL300":
		case "IPL400":
			assertThat("Expected IPL to be returned.", ByteArrayString.toString(output, 5), containsString(", C, "));
			assertThat("Expected IPL to be returned.", ByteArrayString.toString(output, 10), containsString(", P, "));
			break;
		case "TPCL305":
		case "TPCL600":
			assertThat("Expected TPCL to be returned.", ByteArrayString.toString(output, 2), containsString(", D"));
			assertThat("Expected TPCL to be returned.", ByteArrayString.toString(output, 25), containsString(", C"));
			break;
		case "ZPL300":
		case "ZPL600":
			assertThat("Expected ZPL to be returned.", ByteArrayString.toString(output, 5), containsString("^, X, A"));
			break;
		}
	}
	
	@ParameterizedTest
	@EnumSource(Printer.class)
	void testGeneratePrintedOutput_JustFormAndData_MinArgs(Printer printer) throws Exception {
		String printConfig = printer.getPrintConfig();
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, SERVER_FORMS_DIR.resolve(SAMPLE_FORM_XDP).toString())
					 .field(PRINT_CONFIG_PARAM, printConfig);

			Response result = target.request()
									.accept(printer.getMediaType())
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			assertEquals(printer.getMediaType(), MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));

			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			validateResult(resultBytes, "GeneratePrintedOutput_JustFormAndData_" + printConfig + ".txt", printConfig);
		}
	}
	
	@Test
	void testGeneratePrintedOutput_JustFormDocAndData() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, SAMPLE_FORM_XDP.toFile(), APPLICATION_XDP)
					 .field(PRINT_CONFIG_PARAM, "Generic_PS_L3");
	
			Response result = target.request()
									.accept(APPLICATION_PS)
									.post(Entity.entity(multipart, multipart.getMediaType()));
	
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			assertEquals(APPLICATION_PS, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
	
			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			validateResult(resultBytes, "GeneratePrintedOutput_JustFormDocAndData_Generic_PS_L3.txt", "Generic_PS_L3");
		}
	}
	
	@Test
	void testGeneratePrintedOutput_CRXFormAndData() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, SAMPLE_FORM_XDP.getFileName().toString())
					 .field(CONTENT_ROOT_PARAM, CRX_CONTENT_ROOT)
					 .field(PRINT_CONFIG_PARAM, "HP_PCL_5e");

			Response result = target.request()
									.accept(APPLICATION_PCL)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			assertEquals(APPLICATION_PCL, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));

			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			validateResult(resultBytes, "GeneratePrintedOutput_CRXFormAndData_HP_PCL_5e.txt", "HP_PCL_5e");
		}
	}

	@Test
	void testGeneratePdfOutput_BadXDP() throws Exception {
		String badFormName = "BadForm.xdp";
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, TestUtils.SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, TestUtils.SERVER_FORMS_DIR.resolve(badFormName).toString())
					 .field(PRINT_CONFIG_PARAM, "HP_PCL_5e");

			Response result = target.request()
									.accept(APPLICATION_PCL)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			
			String statusMsg = IOUtils.toString((InputStream)result.getEntity(), StandardCharsets.UTF_8); 
			assertThat(statusMsg, containsStringIgnoringCase("Bad request parameter"));
			assertThat(statusMsg, containsStringIgnoringCase("unable to find template"));
			assertThat(statusMsg, containsString(badFormName));
		}
	}
}
