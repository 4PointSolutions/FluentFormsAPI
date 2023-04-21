package com._4point.aem.docservices.rest_services.server.assembler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xmlunit.builder.Input;

import com._4point.aem.docservices.rest_services.server.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.assembler.PDFAConversionResultImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.assembler.MockTraditionalAssemblerService;
import com._4point.aem.fluentforms.testing.assembler.MockTraditionalAssemblerService.toPdfaArguments;

import io.wcm.testing.mock.aem.junit5.AemContext;

class ToPdfATest {
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_PDF = "application/pdf";

	private static final String INPUT_DOCUMENT_PARAM = "inDoc";
	private static final String COLOR_SPACE_PARAM = "colorSpace";
	private static final String COMPLIANCE_PARAM = "compliance";
	private static final String LOG_LEVEL_PARAM = "logLevel";
	private static final String METADATA_EXTENSION_PARAM = "metadataExtension";
	private static final String OPTIONAL_CONTENT_PARAM = "optionalContent";
	private static final String RESULT_LEVEL_PARAM = "resultLevel";
	private static final String SIGNATURES_PARAM = "signatures";
	private static final String REMOVE_INVALID_XMP_PARAM = "removeInvalidXmlProperties";
	private static final String RETAIN_PDF_FORM_STATE_PARAM = "retainPdfFormState";
	private static final String VERIFY_PARAM = "verify";
	
	private static final byte[] PDFA_DOCUMENT_CONTENTS = "PDFA Document Contents".getBytes(StandardCharsets.UTF_8);
	private static final byte[] JOB_LOG_DOCUMENT_CONTENTS = "JOB LOG Document Contents".getBytes(StandardCharsets.UTF_8);
	private static final byte[] CONVERSION_LOG_DOCUMENT_CONTENTS = "Conversion Log Document Contents".getBytes(StandardCharsets.UTF_8);
	private static final Document PDFA_DOC = SimpleDocumentFactoryImpl.getFactory().create(PDFA_DOCUMENT_CONTENTS);
	private static final Document JOB_LOG_DOC = SimpleDocumentFactoryImpl.getFactory().create(JOB_LOG_DOCUMENT_CONTENTS);
	private static final Document CONVERSION_LOG_DOC = SimpleDocumentFactoryImpl.getFactory().create(CONVERSION_LOG_DOCUMENT_CONTENTS);

	private final AemContext aemContext = new AemContext();

	private final ToPdfA underTest =  new ToPdfA();

	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
	
	@BeforeEach
	void setUp() throws Exception {
		// Always use the MockDocumentFactory() in the class that's under test because the Adobe Document object has unresolved dependencies.
		junitx.util.PrivateAccessor.setField(underTest, "docFactory",  (DocumentFactory)mockDocumentFactory);
	}
	
	@Test
	void testDoPostSlingHttpServletRequestSlingHttpServletResponse_MinimalRequest() throws Exception {
		String expectedResultData = "<ToPdfAResult>\n"
				+ "  <ConversionLog>" + Base64.getEncoder().encodeToString(CONVERSION_LOG_DOCUMENT_CONTENTS) + "</ConversionLog>\n"
				+ "  <JobLog>" + Base64.getEncoder().encodeToString(JOB_LOG_DOCUMENT_CONTENTS) + "</JobLog>\n"
				+ "  <PdfADocument>" + Base64.getEncoder().encodeToString(PDFA_DOCUMENT_CONTENTS) + "</PdfADocument>\n"
				+ "  <IsPdfA>true</IsPdfA>\n"
				+ "</ToPdfAResult>\n";
		
		// Set up a mock Assembler service response
		MockTraditionalAssemblerService mockAssemblerService = mockAssemblePdf(new PDFAConversionResultImpl(CONVERSION_LOG_DOC, JOB_LOG_DOC, PDFA_DOC, true));
		
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		byte[] inputPdf = TestUtils.SAMPLE_PDF.toString().getBytes();
		request.addRequestParameter(INPUT_DOCUMENT_PARAM,inputPdf, APPLICATION_PDF);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_XML, response.getContentType());
		assertThat(Input.fromString(response.getOutputAsString()), isIdenticalTo(Input.fromString(expectedResultData.trim())).ignoreWhitespace());

		// Validate arguments to mock Assembler Service.
		toPdfaArguments toPdfaArguments = mockAssemblerService.getToPdfaArguments();
		final PDFAConversionOptionSpec options = toPdfaArguments.getOptions();
		assertAll(
				()->assertArrayEquals(IOUtils.toByteArray(toPdfaArguments.getInDoc().getInputStream()), inputPdf),
				()->assertNull(options.getColorSpace(), "getColorSpace should be null because it was not specified"),
				()->assertNull(options.getCompliance(), "getCompliance should be null because it was not specified"),
				()->assertNull(options.getLogLevel(), "getLogLevel should be null because it was not specified"),
				()->assertNull(options.getMetadataSchemaExtensions(), "getMetadataSchemaExtensions should be null because it was not specified"),
				()->assertNull(options.getOptionalContent(), "getOptionalContent should be null because it was not specified"),
				()->assertNull(options.getResultLevel(), "getResultLevel should be null because it was not specified"),
				()->assertNull(options.getSignatures(), "getSignatures should be null because it was not specified"),
				()->assertTrue(options.isRemoveInvalidXMPProperties(), "isRemoveInvalidXMPProperties should be true if not specified."),
				()->assertFalse(options.isRetainPDFFormState(), "isRetainPDFFormState should be false if not specified."),
				()->assertTrue(options.isVerify(), "isVerify should be true if not specified.")
				);
	}

	public MockTraditionalAssemblerService mockAssemblePdf(PDFAConversionResult conversionResult) throws NoSuchFieldException {
		MockTraditionalAssemblerService assemblerMock = MockTraditionalAssemblerService.createAssemblerMock(conversionResult);
		junitx.util.PrivateAccessor.setField(underTest, "assemblerServiceFactory", (Supplier<TraditionalDocAssemblerService>)()->(TraditionalDocAssemblerService)assemblerMock);
		return assemblerMock;
	}
}
