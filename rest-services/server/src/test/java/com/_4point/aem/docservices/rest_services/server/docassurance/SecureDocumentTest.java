package com._4point.aem.docservices.rest_services.server.docassurance;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.function.Supplier;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.server.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com._4point.aem.fluentforms.impl.docassurance.TraditionalDocAssuranceService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.docassurance.MockTraditionalDocAssuranceService;
import com._4point.aem.fluentforms.testing.docassurance.MockTraditionalDocAssuranceService.SecureDocumentArgs;
import com.adobe.fd.readerextensions.client.ReaderExtensionsOptionSpec;
import com.adobe.fd.readerextensions.client.UsageRights;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class SecureDocumentTest {
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String APPLICATION_XML = "application/xml";

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

	private final SecureDocument underTest = new SecureDocument();

	private final AemContext aemContext = new AemContext();

	private TestLogger loggerCapture = TestLoggerFactory.getTestLogger(SecureDocument.class);

	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();

	@BeforeEach
	void setUp() throws Exception {
		// Always use the MockDocumentFactory() in the class that's under test because the Adobe Document object has unresolved dependencies.
		junitx.util.PrivateAccessor.setField(underTest, "docFactory", (DocumentFactory)mockDocumentFactory);
	}

	@Test
	void testDoPost_HappyPath_RE_PDF() throws ServletException, IOException, NoSuchFieldException {
		String resultData = "testDoPost Happy Path Result";
		String inPDF = TestUtils.SAMPLE_PDF.toString();
		String credentialAlias = "recred";

		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalDocAssuranceService readerExtendPdfMock =  mockReaderExtendPdf(resultDataBytes);

		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(DOCUMENT_PARAM, inPDF);
		parameterMap.put(CREDENTIAL_ALIAS_PARAM, credentialAlias);
		request.setParameterMap(parameterMap);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_PDF, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
	
		// Validate that the correct parameters were passed in to renderPdf
		SecureDocumentArgs secureDocumentArgs = readerExtendPdfMock.getSecureDocumentArgs();
		ReaderExtensionOptions reOptions = secureDocumentArgs.getReaderExtensionOptions();
		ReaderExtensionsOptionSpec reoSpec = reOptions.getReOptions();
		UsageRights usageRights = reoSpec.getUsageRights();
		assertAll(
				()->assertNotNull(secureDocumentArgs.getInDoc()),
				()->assertEquals(reOptions.getCredentialAlias(), credentialAlias),
				()->assertNotNull(reoSpec),
				()->assertNull(reoSpec.getMessage()),
				()->assertTrue(reoSpec.isModeFinal()),
				()->assertNotNull(usageRights),
				()->assertFalse(usageRights.isEnabledBarcodeDecoding()),
				()->assertFalse(usageRights.isEnabledComments()),
				()->assertFalse(usageRights.isEnabledCommentsOnline()),
				()->assertTrue(usageRights.isEnabledDigitalSignatures()),
				()->assertTrue(usageRights.isEnabledDynamicFormFields()),
				()->assertTrue(usageRights.isEnabledDynamicFormPages()),
				()->assertTrue(usageRights.isEnabledEmbeddedFiles()),
				()->assertTrue(usageRights.isEnabledFormDataImportExport()),
				()->assertTrue(usageRights.isEnabledFormFillIn()),
				()->assertTrue(usageRights.isEnabledOnlineForms()),
				()->assertTrue(usageRights.isEnabledSubmitStandalone())
			);
	}

	@Test
	void testDoPost_HappyPath_RE_MaxArgs() throws ServletException, IOException, NoSuchFieldException {
		String resultData = "testDoPost Happy Path Result";
		String inPDF = TestUtils.SAMPLE_PDF.toString();
		String credentialAlias = "recred";

		String message = "test message";
		boolean isModeFinal = false;
		boolean enabledBarcodeDecoding = true;
		boolean enabledComments = false;
		boolean enabledCommentsOnline = true;
		boolean enabledDigitalSignatures = false;
		boolean enabledDynamicFormFields = true;
		boolean enabledDynamicFormPages = false;
		boolean enabledEmbeddedFiles = true;
		boolean enabledFormDataImportExport = false;
		boolean enabledFormFillIn = true;
		boolean enabledOnlineForms = false;
		boolean enabledSubmitStandalone = true;
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalDocAssuranceService readerExtendPdfMock =  mockReaderExtendPdf(resultDataBytes);

		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(DOCUMENT_PARAM, inPDF);
		parameterMap.put(CREDENTIAL_ALIAS_PARAM, credentialAlias);
		parameterMap.put(MESSAGE_PARAM, message);
		parameterMap.put(IS_MODE_FINAL_PARAM, isModeFinal);
		parameterMap.put(ENABLED_BARCODED_DECODING_PARAM, enabledBarcodeDecoding);
		parameterMap.put(ENABLED_COMMENTS_PARAM, enabledComments);
		parameterMap.put(ENABLED_COMMENTS_ONLINE_PARAM, enabledCommentsOnline);
		parameterMap.put(ENABLED_DIGITAL_SIGNATURES_PARAM, enabledDigitalSignatures);
		parameterMap.put(ENABLED_DYNAMIC_FORM_FIELDS_PARAM, enabledDynamicFormFields);
		parameterMap.put(ENABLED_DYNAMIC_FORM_PAGES_PARAM, enabledDynamicFormPages);
		parameterMap.put(ENABLED_EMBEDDED_FILES_PARAM, enabledEmbeddedFiles);
		parameterMap.put(ENABLED_FORM_DATA_IMPORT_EXPORT_PARAM, enabledFormDataImportExport);
		parameterMap.put(ENABLED_FORM_FILL_IN_PARAM, enabledFormFillIn);
		parameterMap.put(ENABLED_ONLINE_FORMS_PARAM, enabledOnlineForms);
		parameterMap.put(ENABLED_SUBMIT_STANDALONE_PARAM, enabledSubmitStandalone);
		request.setParameterMap(parameterMap);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_PDF, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
	
		// Validate that the correct parameters were passed in to renderPdf
		SecureDocumentArgs secureDocumentArgs = readerExtendPdfMock.getSecureDocumentArgs();
		ReaderExtensionOptions reOptions = secureDocumentArgs.getReaderExtensionOptions();
		ReaderExtensionsOptionSpec reoSpec = reOptions.getReOptions();
		UsageRights usageRights = reoSpec.getUsageRights();
		assertAll(
				()->assertNotNull(secureDocumentArgs.getInDoc()),
				()->assertEquals(reOptions.getCredentialAlias(), credentialAlias),
				()->assertNotNull(reoSpec),
				()->assertEquals(reoSpec.getMessage(), "test message"),
				()->assertFalse(reoSpec.isModeFinal()),
				()->assertNotNull(usageRights),
				()->assertTrue(usageRights.isEnabledBarcodeDecoding()),
				()->assertFalse(usageRights.isEnabledComments()),
				()->assertTrue(usageRights.isEnabledCommentsOnline()),
				()->assertFalse(usageRights.isEnabledDigitalSignatures()),
				()->assertTrue(usageRights.isEnabledDynamicFormFields()),
				()->assertFalse(usageRights.isEnabledDynamicFormPages()),
				()->assertTrue(usageRights.isEnabledEmbeddedFiles()),
				()->assertFalse(usageRights.isEnabledFormDataImportExport()),
				()->assertTrue(usageRights.isEnabledFormFillIn()),
				()->assertFalse(usageRights.isEnabledOnlineForms()),
				()->assertTrue(usageRights.isEnabledSubmitStandalone())
			);
	}

	@Disabled
	void testDoPost_BadCredentialAlias() {
		// TODO Auto-generated constructor stub
	}

	@Disabled
	void testDoPost_InDocNotAPdf() {
		// TODO Auto-generated constructor stub
	}

	@Disabled
	void testDoPost_NoInDoc() {
		// TODO Auto-generated constructor stub
	}

	public MockTraditionalDocAssuranceService mockReaderExtendPdf(byte[] resultDataBytes) throws NoSuchFieldException {
		Document readerExtendPdfResult = mockDocumentFactory.create(resultDataBytes);
		readerExtendPdfResult.setContentType(APPLICATION_PDF);
		MockTraditionalDocAssuranceService readerExtendPdfMock = MockTraditionalDocAssuranceService.createSecureDocumentMock(readerExtendPdfResult);
		junitx.util.PrivateAccessor.setField(underTest, "docAssuranceServiceFactory", (Supplier<TraditionalDocAssuranceService>)()->(TraditionalDocAssuranceService)readerExtendPdfMock);
		return readerExtendPdfMock;
	}

}
