package com._4point.aem.docservices.rest_services.it_tests.server.docassurance;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.io.InputStream;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.it_tests.TestUtils;

public class SecureDocumentTest {
	private static final String SECURE_DOCUMENT_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + "/services/DocAssuranceService/SecureDocument";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");

	private static final String DOCUMENT_PARAM = "inDoc";
	private static final String CREDENTIAL_ALIAS_PARAM = "credentialAlias";
	private static final String MESSAGE_PARAM = "message";
	private static final String IS_MODE_FINAL_PARAM = "isModeFinal";
	private static final String ENABLED_BARCODED_DECODING_PARAM = "usageRights.enabledBarcodedDecoding";
	private static final String ENABLED_COMMENTS_PARAM = "usageRights.enabledComments";
	private static final String ENABLED_COMMENTS_ONLINE_PARAM = "usageRights.enabledCommentsOnline";
	private static final String ENABLED_DIGITAL_SIGNATURES_PARAM = "usageRights.enabledDigitalSignatures";
	private static final String ENABLED_DYNAMIC_FORM_FIELDS_PARAM = "usageRights.enabledDynamicFormFields";
	private static final String ENABLED_DYNAMIC_FORM_PAGES_PARAM = "usageRights.enabledDynamicFormPages";
	private static final String ENABLED_EMBEDDED_FILES_PARAM = "usageRights.enabledEmbeddedFiles";
	private static final String ENABLED_FORM_DATA_IMPORT_EXPORT_PARAM = "usageRights.enabledFormDataImportExport";
	private static final String ENABLED_FORM_FILL_IN_PARAM = "usageRights.enabledFormFillIn";
	private static final String ENABLED_ONLINE_FORMS_PARAM = "usageRights.enabledOnlineForms";
	private static final String ENABLED_SUBMIT_STANDALONE_PARAM = "usageRights.enabledSubmitStandalone";

	private static final boolean SAVE_OUTPUT = false;
	
	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD); // default AEM passwords
		target = ClientBuilder.newClient().register(feature).register(MultiPartFeature.class)
				.target(SECURE_DOCUMENT_URL);
	}

	@Test
	void testReaderExtendPDF_AllArgs() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DOCUMENT_PARAM, SAMPLE_FORM_PDF.toFile(), APPLICATION_PDF)
					 .field(CREDENTIAL_ALIAS_PARAM, "recred")
					 .field(MESSAGE_PARAM, "ReaderExtendPDF Test")
					 .field(IS_MODE_FINAL_PARAM, "true")
					 .field(ENABLED_BARCODED_DECODING_PARAM, "false")
					 .field(ENABLED_COMMENTS_PARAM, "false")
					 .field(ENABLED_COMMENTS_ONLINE_PARAM, "false")
					 .field(ENABLED_DIGITAL_SIGNATURES_PARAM, "false")
					 .field(ENABLED_DYNAMIC_FORM_FIELDS_PARAM, "false")
					 .field(ENABLED_DYNAMIC_FORM_PAGES_PARAM, "false")
					 .field(ENABLED_EMBEDDED_FILES_PARAM, "false")
					 .field(ENABLED_FORM_DATA_IMPORT_EXPORT_PARAM, "true")
					 .field(ENABLED_FORM_FILL_IN_PARAM, "true")
					 .field(ENABLED_ONLINE_FORMS_PARAM, "true")
					 .field(ENABLED_SUBMIT_STANDALONE_PARAM, "true")
					 ;

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			assertEquals(APPLICATION_PDF, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			TestUtils.validatePdfResult(resultBytes, "testReaderExtendPDF_AllArgs.pdf", true, true, true);
		}
	}

	@Test
	void testReaderExtendPDF_AllArgsTrue() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DOCUMENT_PARAM, SAMPLE_FORM_PDF.toFile(), APPLICATION_PDF)
					 .field(CREDENTIAL_ALIAS_PARAM, "recred")
					 .field(MESSAGE_PARAM, "ReaderExtendPDF Test")
					 .field(IS_MODE_FINAL_PARAM, "true")
					 .field(ENABLED_BARCODED_DECODING_PARAM, "true")
					 .field(ENABLED_COMMENTS_PARAM, "true")
					 .field(ENABLED_COMMENTS_ONLINE_PARAM, "true")
					 .field(ENABLED_DIGITAL_SIGNATURES_PARAM, "true")
					 .field(ENABLED_DYNAMIC_FORM_FIELDS_PARAM, "true")
					 .field(ENABLED_DYNAMIC_FORM_PAGES_PARAM, "true")
					 .field(ENABLED_EMBEDDED_FILES_PARAM, "true")
					 .field(ENABLED_FORM_DATA_IMPORT_EXPORT_PARAM, "true")
					 .field(ENABLED_FORM_FILL_IN_PARAM, "true")
					 .field(ENABLED_ONLINE_FORMS_PARAM, "true")
					 .field(ENABLED_SUBMIT_STANDALONE_PARAM, "true")
					 ;

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			assertEquals(APPLICATION_PDF, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			TestUtils.validatePdfResult(resultBytes, "testReaderExtendPDF_AllArgsTrue.pdf", true, true, true);
		}
	}

	@Test
	void testReaderExtendPDF_JustInDocAndCred() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DOCUMENT_PARAM, SAMPLE_FORM_PDF.toFile(), APPLICATION_PDF)
					 .field(CREDENTIAL_ALIAS_PARAM, "recred");

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
//			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
//			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
//			assertThat("Expected a PDF to be returned.", ByteArrayString.toString(resultBytes, 8), containsString("%, P, D, F, -, 1, ., 7"));
//			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testReaderExtendPDF_InputAndCred.pdf")));
			assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatus(), () -> "Expected response to be '500 Internal Error', entity='" + TestUtils.readEntityToString(result) + "'.");
			String statusMsg = TestUtils.readEntityToString(result);
			assertThat(statusMsg, containsStringIgnoringCase("Internal Error while reader extending a PDF."));
			// At least one non-null usage right must be specified or will get a com.adobe.internal.pdftoolkit.core.exceptions.PDFInvalidParameterException, which will cause...
			assertThat(statusMsg, containsStringIgnoringCase("AEM-REX-001-008: Unable to apply the requested usage rights to the given document."));
		}
	}

	@Test
	void testReaderExtendPDF_BadCredentialAlias() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DOCUMENT_PARAM, SAMPLE_FORM_PDF.toFile(), APPLICATION_PDF)
					 .field(CREDENTIAL_ALIAS_PARAM, "bad");

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatus(), () -> "Expected response to be '500 Internal Error', entity='" + TestUtils.readEntityToString(result) + "'.");
			String statusMsg = TestUtils.readEntityToString(result);
			assertThat(statusMsg, containsStringIgnoringCase("Internal Error while reader extending a PDF."));
			assertThat(statusMsg, containsStringIgnoringCase("No credential found with alias [bad]."));
		}
	}

	@Test
	void testReaderExtendPDF_NoCredentialAlias() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DOCUMENT_PARAM, SAMPLE_FORM_PDF.toFile(), APPLICATION_PDF);

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus(), () -> "Expected response to be '400 Bad Request Exception', entity='" + TestUtils.readEntityToString(result) + "'.");
			String statusMsg = TestUtils.readEntityToString(result);
			assertThat(statusMsg, containsStringIgnoringCase("Missing form parameter 'credentialAlias'"));
		}
	}

	@Test
	void testReaderExtendPDF_InDocNotPdf() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DOCUMENT_PARAM, SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(CREDENTIAL_ALIAS_PARAM, "recred");

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatus(), () -> "Expected response to be '500 Internal Error', entity='" + TestUtils.readEntityToString(result) + "'.");
			String statusMsg = TestUtils.readEntityToString(result);
			assertThat(statusMsg, containsStringIgnoringCase("Internal Error while reader extending a PDF."));
			assertThat(statusMsg, containsStringIgnoringCase("Exception while converting Doc to PDF in DocAssuranceService"));
			assertThat(statusMsg, containsStringIgnoringCase("Stream does not represent a PDF document."));
		}
	}

	@Test
	void testReaderExtendPDF_NoInDoc() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(CREDENTIAL_ALIAS_PARAM, "recred");

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus(), () -> "Expected response to be '400 Bad Request Exception', entity='" + TestUtils.readEntityToString(result) + "'.");
			String statusMsg = TestUtils.readEntityToString(result);
			assertThat(statusMsg, containsStringIgnoringCase("Missing form parameter 'inDoc'"));
		}
	}

}
