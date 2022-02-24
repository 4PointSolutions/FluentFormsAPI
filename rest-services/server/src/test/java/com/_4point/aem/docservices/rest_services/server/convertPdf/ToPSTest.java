package com._4point.aem.docservices.rest_services.server.convertPdf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Supplier;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.server.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.impl.convertPdf.TraditionalConvertPdfService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.convertPdf.MockTraditionalConvertPdfService;

import io.wcm.testing.mock.aem.junit5.AemContext;

public class ToPSTest {
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String APPLICATION_PS = "application/postscript";
	
	private static final String PDF_PARAM = "inPdfDoc";
	private static final String COLOR_PARAM = "toPSOptionsSpec.color";
	private static final String FONT_INCLUSION_PARAM = "toPSOptionsSpec.fontInclusion";
	private static final String LINE_WEIGHT_PARAM = "toPSOptionsSpec.lineWeight";
	private static final String PAGE_RANGE_PARAM = "toPSOptionsSpec.pageRange";
	private static final String PAGE_SIZE_PARAM = "toPSOptionsSpec.pageSize";
	private static final String PAGE_SIZE_HEIGHT_PARAM = "toPSOptionsSpec.pageSizeHeight";
	private static final String PAGE_SIZE_WIDTH_PARAM = "toPSOptionsSpec.pageSizeWidth";
	private static final String PS_LEVEL_PARAM = "toPSOptionsSpec.psLevel";
	private static final String STYLE_PARAM = "toPSOptionsSpec.style";
	private static final String ALLOW_BINARY_CONTENT_PARAM = "toPSOptionsSpec.allowBinaryContent";
	private static final String BLEED_MARKS_PARAM = "toPSOptionsSpec.bleedMarks";
	private static final String COLOR_BARS_PARAM = "toPSOptionsSpec.colorBars";
	private static final String CONVERT_TRUE_TYPE_TO_TYPE1_PARAM = "toPSOptionsSpec.convertTrueTypeToType1";
	private static final String EMIT_CID_FONT_TYPE2_PARAM = "toPSOptionsSpec.emitCIDFontType2";
	private static final String EMIT_PS_FORM_OBJECTS_PARAM = "toPSOptionsSpec.emitPSFormsObjects";
	private static final String EXPAND_TO_FIT_PARAM = "toPSOptionsSpec.expandToFit";
	private static final String INCLUDE_COMMENTS_PARAM = "toPSOptionsSpec.includeComments";
	private static final String LEGACY_TO_SIMPLE_PS_FLAG_PARAM = "toPSOptionsSpec.legacyToSimplePSFlag";
	private static final String PAGE_INFORMATION_PARAM = "toPSOptionsSpec.pageInformation";
	private static final String REGISTRATION_MARKS_PARAM = "toPSOptionsSpec.registrationMarks";
	private static final String REVERSE_PARAM = "toPSOptionsSpec.reverse";
	private static final String ROTATE_AND_CENTER_PARAM = "toPSOptionsSpec.rotateAndCenter";
	private static final String SHRINK_TO_FIT_PARAM = "toPSOptionsSpec.shrinkToFit";
	private static final String TRIM_MARKS_PARAM = "toPSOptionsSpec.trimMarks";
	private static final String USE_MAX_JPEG_IMAGE_RESOLUTION_PARAM = "toPSOptionsSpec.useMaxJPEGImageResolution";
	
	private final ToPS underTest = new ToPS();

	private final AemContext aemContext = new AemContext();

	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();

	@BeforeEach
	void setUp() throws Exception {
		// Always use the MockDocumentFactory() in the class that's under test because the Adobe Document object has unresolved dependencies.
		junitx.util.PrivateAccessor.setField(underTest, "docFactory", (DocumentFactory)mockDocumentFactory);
	}

	@Test
	void testDoPost_HappyPath_JustPdf() throws ServletException, IOException, NoSuchFieldException {
		String resultData = "testDoPost Happy Path Result";
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalConvertPdfService convertPdfMock = mockToPS(resultDataBytes);
		
		byte[] inPdfDoc = Files.readAllBytes(TestUtils.SAMPLE_FORM);
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(PDF_PARAM, inPdfDoc, APPLICATION_PDF);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus(), "Response was not OK, '" + response.getStatus() + "'. (" + response.getStatusMessage() + ")" );
		assertEquals(APPLICATION_PS, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
	}

	public MockTraditionalConvertPdfService mockToPS(byte[] resultDataBytes) throws NoSuchFieldException {
		Document convertPdfResult = mockDocumentFactory.create(resultDataBytes);
		convertPdfResult.setContentType(APPLICATION_PS);
		MockTraditionalConvertPdfService convertPdfMock = MockTraditionalConvertPdfService.createDocumentMock(convertPdfResult);
		junitx.util.PrivateAccessor.setField(underTest, "convertPdfServiceFactory", (Supplier<TraditionalConvertPdfService>)()->(TraditionalConvertPdfService)convertPdfMock);
		return convertPdfMock;
	}
}
