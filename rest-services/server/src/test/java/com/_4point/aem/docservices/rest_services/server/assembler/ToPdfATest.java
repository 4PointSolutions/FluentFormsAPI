package com._4point.aem.docservices.rest_services.server.assembler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Supplier;

import javax.servlet.ServletException;

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
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.assembler.PDFAConversionResultImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.assembler.MockTraditionalAssemblerService;
import com._4point.aem.fluentforms.testing.assembler.MockTraditionalAssemblerService.toPdfaArguments;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;

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

	// All expected values must be non-default values
	private static final ColorSpace COLOR_SPACE_VALUE = ColorSpace.JAPAN_COLOR_COATED;
	private static final Compliance COMPLIANCE_VALUE = Compliance.PDFA_2B;
	private static final LogLevel LOG_LEVEL_VALUE = LogLevel.FINER;
	private static final Document METADATA_EXTENSION_VALUE1 = SimpleDocumentFactoryImpl.INSTANCE.create("Metadata Extension #1".getBytes(StandardCharsets.UTF_8));
	private static final Document METADATA_EXTENSION_VALUE2 = SimpleDocumentFactoryImpl.INSTANCE.create("Metadata Extension #2".getBytes(StandardCharsets.UTF_8));
	private static final OptionalContent OPTIONAL_CONTENT_VALUE = OptionalContent.VISIBLE;
	private static final ResultLevel RESULT_LEVEL_VALUE = ResultLevel.SUMMARY;
	private static final Signatures SIGNATURES_VALUE = Signatures.ARCHIVE_AS_NEEDED;
	private static final Boolean REMOVE_INVALID_XMP_VALUE = true;
	private static final Boolean RETAIN_PDF_FORM_STATE_VALUE = false;
	private static final Boolean VERIFY_VALUE = false;

	private static final byte[] PDFA_DOCUMENT_CONTENTS = "PDFA Document Contents".getBytes(StandardCharsets.UTF_8);
	private static final byte[] JOB_LOG_DOCUMENT_CONTENTS = "JOB LOG Document Contents".getBytes(StandardCharsets.UTF_8);
	private static final byte[] CONVERSION_LOG_DOCUMENT_CONTENTS = "Conversion Log Document Contents".getBytes(StandardCharsets.UTF_8);
	private static final Document PDFA_DOC = SimpleDocumentFactoryImpl.getFactory().create(PDFA_DOCUMENT_CONTENTS);
	private static final Document JOB_LOG_DOC = SimpleDocumentFactoryImpl.getFactory().create(JOB_LOG_DOCUMENT_CONTENTS);
	private static final Document CONVERSION_LOG_DOC = SimpleDocumentFactoryImpl.getFactory().create(CONVERSION_LOG_DOCUMENT_CONTENTS);

	private static final String EXPECTED_RESULT_DATA = "<ToPdfAResult>\n"
			+ "  <ConversionLog>" + Base64.getEncoder().encodeToString(CONVERSION_LOG_DOCUMENT_CONTENTS) + "</ConversionLog>\n"
			+ "  <JobLog>" + Base64.getEncoder().encodeToString(JOB_LOG_DOCUMENT_CONTENTS) + "</JobLog>\n"
			+ "  <PdfADocument>" + Base64.getEncoder().encodeToString(PDFA_DOCUMENT_CONTENTS) + "</PdfADocument>\n"
			+ "  <IsPdfA>true</IsPdfA>\n"
			+ "</ToPdfAResult>\n";
	
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
		tester().performPost(TestUtils.SAMPLE_PDF.toString().getBytes())
				.validateResponseWasOK()
				.validateMinimalToPdfaArguments();
	}

	@Test
	void testDoPostSlingHttpServletRequestSlingHttpServletResponse_MaximalRequest() throws Exception {
		tester()
			.addRequestParameter(COLOR_SPACE_PARAM, COLOR_SPACE_VALUE.toString())
			.addRequestParameter(COMPLIANCE_PARAM, COMPLIANCE_VALUE.toString())
			.addRequestParameter(LOG_LEVEL_PARAM, LOG_LEVEL_VALUE.toString())
			.addRequestParameter(METADATA_EXTENSION_PARAM, toByteArray(METADATA_EXTENSION_VALUE1), METADATA_EXTENSION_VALUE1.getContentType())
			.addRequestParameter(METADATA_EXTENSION_PARAM, toByteArray(METADATA_EXTENSION_VALUE2), METADATA_EXTENSION_VALUE2.getContentType())
			.addRequestParameter(OPTIONAL_CONTENT_PARAM, OPTIONAL_CONTENT_VALUE.toString())
			.addRequestParameter(RESULT_LEVEL_PARAM, RESULT_LEVEL_VALUE.toString())
			.addRequestParameter(SIGNATURES_PARAM, SIGNATURES_VALUE.toString())
			.addRequestParameter(REMOVE_INVALID_XMP_PARAM, REMOVE_INVALID_XMP_VALUE.toString())
			.addRequestParameter(RETAIN_PDF_FORM_STATE_PARAM, RETAIN_PDF_FORM_STATE_VALUE.toString())
			.addRequestParameter(VERIFY_PARAM, VERIFY_VALUE.toString())
			.performPost(TestUtils.SAMPLE_PDF.toString().getBytes())
			.validateResponseWasOK()
			.validateMaximalToPdfaArguments();
	}

	private Tester tester() throws NoSuchFieldException { return new Tester(); }
	
	private class Tester {
		private final MockSlingHttpServletRequest request;
		private final MockSlingHttpServletResponse response;
		private final MockTraditionalAssemblerService mockAssemblerService;

		public Tester() throws NoSuchFieldException {
			this.request = new MockSlingHttpServletRequest(aemContext.bundleContext());
			this.response = new MockSlingHttpServletResponse();
			this.mockAssemblerService = mockAssemblePdf(new PDFAConversionResultImpl(CONVERSION_LOG_DOC, JOB_LOG_DOC, PDFA_DOC, true));
		}
		
		// Inject a mock service into the system under test that returns a specific result.
		private MockTraditionalAssemblerService mockAssemblePdf(PDFAConversionResult conversionResult) throws NoSuchFieldException {
			MockTraditionalAssemblerService assemblerMock = MockTraditionalAssemblerService.createAssemblerMock(conversionResult);
			junitx.util.PrivateAccessor.setField(underTest, "assemblerServiceFactory", (Supplier<TraditionalDocAssemblerService>)()->(TraditionalDocAssemblerService)assemblerMock);
			return assemblerMock;
		}
		
		private Tester addRequestParameter(String key, byte[] value, String contentType) {
			request.addRequestParameter(key, value, contentType);
			return this;
		}

		private Tester addRequestParameter(String key, String value) {
			request.addRequestParameter(key, value);
			return this;
		}

		private Asserter performPost(byte[] inputPdf) throws ServletException, IOException {
			addRequestParameter(INPUT_DOCUMENT_PARAM, inputPdf, APPLICATION_PDF);

			underTest.doPost(request, response);
			
			toPdfaArguments toPdfaArguments = mockAssemblerService.getToPdfaArguments();
			return new Asserter(response, toPdfaArguments, inputPdf);
		}
		
	}
	
	private static class Asserter {
		private final MockSlingHttpServletResponse response;
		private final toPdfaArguments toPdfaArguments;
		private final byte[] inputPdf;

		private Asserter(MockSlingHttpServletResponse response, toPdfaArguments toPdfaArguments, byte[] inputPdf) {
			this.response = response;
			this.toPdfaArguments = toPdfaArguments;
			this.inputPdf = inputPdf;
		}

	
		// Validate the response.
		private Asserter validateResponseWasOK() {
			// Validate the result
			assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
			assertEquals(APPLICATION_XML, response.getContentType());
			assertThat(Input.fromString(response.getOutputAsString()), isIdenticalTo(Input.fromString(EXPECTED_RESULT_DATA.trim())).ignoreWhitespace());

			return this;
		}
		
		// Validate arguments to mock Assembler Service.
		private Asserter validateMinimalToPdfaArguments() {
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
					()->assertNull(options.isRemoveInvalidXMPProperties(), "isRemoveInvalidXMPProperties should be true if not specified."),
					()->assertNull(options.isRetainPDFFormState(), "isRetainPDFFormState should be false if not specified."),
					()->assertNull(options.isVerify(), "isVerify should be true if not specified.")
					);
			return this;
		}

		// Validate arguments to mock Assembler Service.
		private Asserter validateMaximalToPdfaArguments() {
			final PDFAConversionOptionSpec options = toPdfaArguments.getOptions();
			assertAll(
					()->assertArrayEquals(IOUtils.toByteArray(toPdfaArguments.getInDoc().getInputStream()), inputPdf),
					()->assertEquals(options.getColorSpace(), COLOR_SPACE_VALUE),
					()->assertEquals(options.getCompliance(), COMPLIANCE_VALUE),
					()->assertEquals(options.getLogLevel(), LOG_LEVEL_VALUE),
					()->assertArrayEquals(toByteArray(options.getMetadataSchemaExtensions().get(0)), toByteArray(METADATA_EXTENSION_VALUE1)),
					()->assertArrayEquals(toByteArray(options.getMetadataSchemaExtensions().get(1)), toByteArray(METADATA_EXTENSION_VALUE2)),
					()->assertEquals(options.getOptionalContent(), OPTIONAL_CONTENT_VALUE),
					()->assertEquals(options.getResultLevel(), RESULT_LEVEL_VALUE),
					()->assertEquals(options.getSignatures(), SIGNATURES_VALUE),
					()->assertEquals(options.isRemoveInvalidXMPProperties(), REMOVE_INVALID_XMP_VALUE),
					()->assertEquals(options.isRetainPDFFormState(), RETAIN_PDF_FORM_STATE_VALUE),
					()->assertEquals(options.isVerify(), VERIFY_VALUE)
					);
			return this;
		}
	}
	
	static byte[] toByteArray(Document doc) {
		try {
			return toByteArray(doc.getInputStream());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	static byte[] toByteArray(InputStream is) {
		try {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
