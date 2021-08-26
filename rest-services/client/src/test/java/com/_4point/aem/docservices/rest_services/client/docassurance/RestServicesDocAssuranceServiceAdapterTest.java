package com._4point.aem.docservices.rest_services.client.docassurance;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.allOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService.DocAssuranceServiceException;
import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.docassurance.client.api.SignatureOptions;
import com.adobe.fd.signatures.client.types.FieldMDPOptionSpec;
import com.adobe.fd.signatures.client.types.PDFSeedValueOptionSpec;
import com.adobe.fd.signatures.client.types.PDFSignatureFieldProperties;
import com.adobe.fd.signatures.client.types.PositionRectangle;
import com.adobe.fd.signatures.client.types.VerificationTime;
import com.adobe.fd.signatures.pdf.inputs.UnlockOptions;
import com.adobe.fd.signatures.pki.client.types.common.RevocationCheckStyle;

@ExtendWith(MockitoExtension.class)
public class RestServicesDocAssuranceServiceAdapterTest {

	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	
	@Mock(answer = Answers.RETURNS_SELF) Client client;	// answers used to mock Client's fluent interface. 
	@Mock WebTarget target;
	@Mock Response response;
	@Mock Builder builder;
	@Mock StatusType statusType;
	
	@Captor ArgumentCaptor<String> machineName;
	@Captor ArgumentCaptor<String> path;
	@SuppressWarnings("rawtypes")
	@Captor ArgumentCaptor<Entity> entity;
	@Captor ArgumentCaptor<String> correlationId;

	RestServicesDocAssuranceServiceAdapter underTest;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Disabled
	void testExportData() {
		fail("Not yet implemented");
	}

	private enum HappyPaths { SSL, NO_SSL };
	
	@ParameterizedTest
	@EnumSource(HappyPaths.class)
	void testSecureDocument_noSsl(HappyPaths codePath) throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());

		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn("application/pdf");
		
		boolean useSSL = false;
		boolean useCorrelationId = false;
		switch (codePath) {
		case SSL:
			useSSL = true;
			useCorrelationId = true;
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
			break;
		case NO_SSL:
			useSSL = false;
			useCorrelationId = false;
			break;
		default:
			throw new IllegalStateException("Found unexpected HappyPaths value (" + codePath.toString() + ").");
		}
		
		com._4point.aem.docservices.rest_services.client.docassurance.RestServicesDocAssuranceServiceAdapter.DocAssuranceServiceBuilder adapterBuilder = RestServicesDocAssuranceServiceAdapter.builder()
						.machineName(TEST_MACHINE_NAME)
						.port(TEST_MACHINE_PORT)
						.basicAuthentication("username", "password")
						.useSsl(useSSL)
						.aemServerType(AemServerType.StandardType.JEE)
						.clientFactory(()->client);
		if (useCorrelationId) {
			adapterBuilder.correlationId(()->CORRELATION_ID);
		}
		
		underTest = adapterBuilder
						.build();
				
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		EncryptionOptions eOptions = null;
		SignatureOptions sOptions = null;
		UnlockOptions uOptions = null;
		ReaderExtensionOptions reOptions = Mockito.mock(ReaderExtensionOptions.class);
		Document pdfResult = underTest.secureDocument(pdf, eOptions, sOptions, reOptions, uOptions);
		
		// Make sure the correct URL is called.
		final String expectedPrefix = useSSL ? "https://" : "http://";
		assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix));
		assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME));
		assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT)));
		assertThat("Expected target url contains 'SecureDocument'", path.getValue(), containsString("SecureDocument"));

		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		validateDocumentFormField(postedData, "inDoc", new MediaType("application", "pdf"), pdf.getInlineData());
		
		if (useCorrelationId) {
			assertEquals(CORRELATION_ID, correlationId.getValue());
		}
		
		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), pdfResult.getInlineData());
		assertEquals(APPLICATION_PDF, MediaType.valueOf(pdfResult.getContentType()));
	}
	
	private void validateDocumentFormField(FormDataMultiPart postedData, String fieldName, MediaType expectedMediaType, byte[] expectedData) throws IOException {
		List<FormDataBodyPart> pdfFields = postedData.getFields(fieldName);
		assertEquals(1, pdfFields.size());
		
		FormDataBodyPart pdfPart = pdfFields.get(0);
		assertEquals(expectedMediaType, pdfPart.getMediaType());
		byte[] pdfBytes = IOUtils.toByteArray((InputStream) pdfPart.getEntity());
		assertArrayEquals(expectedData, pdfBytes);  // TODO: Need to figure out how to test for entity.
	}

	@Test
	void testSecureDocument_SuccessButNoEntity() throws Exception {

		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		
		underTest = RestServicesDocAssuranceServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication("username", "password")
				.useSsl(false)
				.clientFactory(()->client)
				.build();
		
		
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());

		Exception ex = assertThrows(Exception.class, ()->underTest.secureDocument(pdf, null, null, null, null));
		assertThat(ex.getMessage(), containsString("should never happen"));
	}

	@Test
	void testImportData_FailureWithHTMLResponse() throws Exception {
		final String SAMPLE_HTML_RESPONSE = "<html><head><title>Content modified /services/DocAssuranceService/SecureDocument</title></head><body><h1>Content modified /services/DocAssuranceService/SecureDocument</h1></body></html>";
		final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(SAMPLE_HTML_RESPONSE.getBytes()));
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(HTML_CONTENT_TYPE);
		
		underTest = RestServicesDocAssuranceServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication("username", "password")
				.useSsl(false)
				.clientFactory(()->client)
				.build();
		
		
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());

		Exception ex = assertThrows(Exception.class, ()->underTest.secureDocument(pdf, null, null, null, null));
		assertThat(ex.getMessage(), containsString("was not a PDF"));
		assertThat(ex.getMessage(), containsString(HTML_CONTENT_TYPE));
	}

	@FunctionalInterface
	public interface Consumer_WithExceptions<T, E extends Exception> {
		void accept(T t) throws E;
	}
	   
	private void testForNotImplementedYetException(String fnName, 
			Consumer_WithExceptions<RestServicesDocAssuranceServiceAdapter, DocAssuranceServiceException> fn) {
		testForUnsupportedException(fn, allOf(containsString(fnName), containsString("is not implemented yet")));
	}

	private void testForUnsupportedException(
			Consumer_WithExceptions<RestServicesDocAssuranceServiceAdapter, DocAssuranceServiceException> fn,
			Matcher<String> matcher) {
		RestServicesDocAssuranceServiceAdapter underTest = RestServicesDocAssuranceServiceAdapter.builder().build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->fn.accept(underTest));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, matcher);
	}

	@Test
	void testGetDocumentUsageRights() {
		testForNotImplementedYetException("getDocumentUsageRights", underTest->underTest.getDocumentUsageRights(mock(Document.class), mock(UnlockOptions.class)));
	}

	@Test
	void testGetCredentialUsageRights() {
		testForNotImplementedYetException("getCredentialUsageRights", underTest->underTest.getCredentialUsageRights("credential"));
	}

	@Test
	void testAddInvisibleSignatureField() {
		testForNotImplementedYetException("addInvisibleSignatureField", underTest->underTest.addInvisibleSignatureField(mock(Document.class), "FieldName", mock(FieldMDPOptionSpec.class), mock(PDFSeedValueOptionSpec.class), mock(UnlockOptions.class)));
	}

	void testAddSignatureField() {
		testForNotImplementedYetException("addSignatureField", underTest->underTest.addSignatureField(mock(Document.class), "FieldName", 2, mock(PositionRectangle.class), mock(FieldMDPOptionSpec.class), mock(PDFSeedValueOptionSpec.class), mock(UnlockOptions.class)));
	}

	@Test
	void testClearSignatureField() {
		testForNotImplementedYetException("clearSignatureField", underTest->underTest.clearSignatureField(mock(Document.class), "FieldName", mock(UnlockOptions.class)));
	}

	@Test
	void testGetCertifyingSignatureField() {
		testForNotImplementedYetException("getCertifyingSignatureField", underTest->underTest.getCertifyingSignatureField(mock(Document.class), mock(UnlockOptions.class)));
	}

	@Test
	void testGetSignature() {
		testForNotImplementedYetException("getSignature", underTest->underTest.getSignature(mock(Document.class), "FieldName", mock(UnlockOptions.class)));
	}

	@Test
	void testGetSignatureFieldList() {
		testForNotImplementedYetException("getSignatureFieldList", underTest->underTest.getSignatureFieldList(mock(Document.class), mock(UnlockOptions.class) ));
	}

	@Test
	void testModifySignatureField() {
		testForNotImplementedYetException("modifySignatureField", underTest->underTest.modifySignatureField(mock(Document.class), "FieldName", mock(PDFSignatureFieldProperties.class), mock(UnlockOptions.class)));
	}

	@Test
	void testRemoveSignatureField() {
		testForNotImplementedYetException("removeSignatureField", underTest->underTest.removeSignatureField(mock(Document.class), "FieldName", mock(UnlockOptions.class)));
	}

	@ParameterizedTest
	@MethodSource("verifyEnums")
	void testVerify(RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime) {
		testForNotImplementedYetException("verify", underTest->underTest.verify(mock(Document.class), "FieldName",  revocationCheckStyle, verificationTime, null ));
	}

	@Test
	void testGetPDFEncryption() {
		testForNotImplementedYetException("getPDFEncryption", underTest->underTest.getPDFEncryption(mock(Document.class)));
	}

	@Test
	void testRemovePDFCertificateSecurity() {
		testForNotImplementedYetException("removePDFCertificateSecurity", underTest->underTest.removePDFCertificateSecurity(mock(Document.class), "alias"));
	}

	@Test
	void testRemovePDFPasswordSecurity() {
		testForNotImplementedYetException("removePDFPasswordSecurity", underTest->underTest.removePDFPasswordSecurity(mock(Document.class), "password" ));
	}

	@ParameterizedTest
	@MethodSource("verifyEnums")
	void testVerifyDocument(RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime) {
		testForNotImplementedYetException("verifyDocument", underTest->underTest.verifyDocument(mock(Document.class), revocationCheckStyle, verificationTime, null));
	}

	@Test
	void testRemoveUsageRights() {
		testForNotImplementedYetException("removeUsageRights", underTest->underTest.removeUsageRights(mock(Document.class), mock(UnlockOptions.class)));
	}

	@Test
	void testApplyDocumentTimeStamp() {
		testForNotImplementedYetException("applyDocumentTimeStamp", underTest->underTest.applyDocumentTimeStamp(mock(Document.class), VerificationTime.SIGNING_TIME, null, mock(UnlockOptions.class)));
	}

	static Stream<Arguments> verifyEnums() {
		java.util.stream.Stream.Builder<Arguments> builder = Stream.builder();
		for (RevocationCheckStyle rcs : RevocationCheckStyle.values()) {
			for (VerificationTime vt: VerificationTime.values()) {
				builder.accept(arguments(rcs, vt));
			}
		}
		return builder.build();
	}	
}
