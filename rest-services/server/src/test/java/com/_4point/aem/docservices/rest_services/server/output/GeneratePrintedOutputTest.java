package com._4point.aem.docservices.rest_services.server.output;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.Supplier;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.server.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.output.PrintConfig;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService.GeneratePrintedOutputArgs;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class GeneratePrintedOutputTest {

	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";
	private static final String CONTENT_ROOT_PARAM = "outputOptions.contentRoot";
	private static final String COPIES_PARAM = "outputOptions.copies";
	private static final String DEBUG_DIR_PARAM = "outputOptions.debugDir";
	private static final String LOCALE_PARAM = "outputOptions.locale";
	private static final String PAGINATION_OVERRIDE_PARAM = "outputOptions.paginationOverride";
	private static final String PRINT_CONFIG_PARAM = "outputOptions.printConfig";
	private static final String XCI_PARAM = "outputOptions.xci";
	private static final String APPLICATION_DPL = "application/vnd.datamax-dpl";
	private static final String APPLICATION_IPL = "application/vnd.intermec-ipl";
	private static final String APPLICATION_PCL = "application/vnd.hp-pcl";
	private static final String APPLICATION_PS = "application/postscript";
	private static final String APPLICATION_TPCL = "application/vnd.toshiba-tpcl";
	private static final String APPLICATION_ZPL = "x-application/zpl";
	private static final String APPLICATION_XML = "application/xml";

	private final GeneratePrintedOutput underTest =  new GeneratePrintedOutput();

	private final AemContext aemContext = new AemContext();

	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();

	@BeforeEach
	void setUp() throws Exception {
		// Always use the MockDocumentFactory() in the class that's under test because the Adobe Document object has unresolved dependencies.
		junitx.util.PrivateAccessor.setField(underTest, "docFactory",  (DocumentFactory)mockDocumentFactory);
	}

	private enum Printer {
		DPL300(APPLICATION_DPL, PrintConfig.DPL300),
		DPL406(APPLICATION_DPL, PrintConfig.DPL406),
		DPL600(APPLICATION_DPL, PrintConfig.DPL600),
		IPL300(APPLICATION_IPL, PrintConfig.IPL300),
		IPL400(APPLICATION_IPL, PrintConfig.IPL400),
		PCL_5C(APPLICATION_PCL, PrintConfig.GenericColor_PCL_5c),
		PCL_5E(APPLICATION_PCL, PrintConfig.HP_PCL_5e),
		PS_L3(APPLICATION_PS, PrintConfig.Generic_PS_L3),
		PS_PLAIN(APPLICATION_PS, PrintConfig.PS_PLAIN),
		TPCL305(APPLICATION_TPCL, PrintConfig.TPCL305),
		TPCL600(APPLICATION_TPCL, PrintConfig.TPCL600),
		ZPL300(APPLICATION_ZPL, PrintConfig.ZPL300),
		ZPL600(APPLICATION_ZPL, PrintConfig.ZPL600);
		
		private final String contentType;
		private final PrintConfig printConfig;
		
		private Printer(String contentType, PrintConfig printConfig) {
			this.contentType = contentType;
			this.printConfig = printConfig;
		}
		
		public String getContentType() {
			return contentType;
		}
		
		public PrintConfig getPrintConfig() {
			return printConfig;
		}
	}
	
	@ParameterizedTest
	@EnumSource(Printer.class)
	void testDoPost_HappyPath_MinArgs(Printer printer) throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalOutputService generatePrintedOutputMock = mockGeneratePrintedOutput(resultDataBytes, printer.getContentType());

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData.getBytes(), APPLICATION_XML);
		request.addRequestParameter(PRINT_CONFIG_PARAM, printer.getPrintConfig().toString());
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus(), "Expected OK Status code.  Response='" + response.getStatusMessage() + "'");
		assertEquals(printer.getContentType(), response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		
		// Validate that the correct parameters were passed in
		GeneratePrintedOutputArgs generatePrintedOutputArgs = generatePrintedOutputMock.getGeneratePrintedOutputArgs();
		assertArrayEquals(formData.getBytes(), generatePrintedOutputArgs.getData().getInlineData());
		assertEquals(TestUtils.SAMPLE_FORM.getFileName().toString(), generatePrintedOutputArgs.getUrlOrFilename());
		PrintedOutputOptions printedOutputOptions = generatePrintedOutputArgs.getPrintedOutputOptions();
//		assertEquals(printer.getPrintConfig(), printedOutputOptions.getPrintConfig());
	}
	
	@ParameterizedTest
	@EnumSource(Printer.class)
	void testDoPost_HappyPath_MaxArgs(Printer printer) throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		String contentRootData = "/"; //TestUtils.SAMPLE_FORM.getParent().getParent().toString();
		Integer copies = 1;
		String debugDirData = "/debug/dir";
		String localeData = "en-CA";
		String paginationOverride = "simplex";
		String xciData = "Xci Data";
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalOutputService generatePrintedOutputMock = mockGeneratePrintedOutput(resultDataBytes, printer.getContentType());

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData.getBytes(), APPLICATION_XML);
//		request.addRequestParameter(CONTENT_ROOT_PARAM, contentRootData);
		request.addRequestParameter(COPIES_PARAM, String.valueOf(copies.intValue()));
		request.addRequestParameter(DEBUG_DIR_PARAM, debugDirData);
		request.addRequestParameter(LOCALE_PARAM, localeData);
		request.addRequestParameter(PAGINATION_OVERRIDE_PARAM, paginationOverride);
		request.addRequestParameter(PRINT_CONFIG_PARAM, printer.getPrintConfig().toString());  // TODO
		request.addRequestParameter(XCI_PARAM, xciData.getBytes(), APPLICATION_XML);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus(), "Expected OK Status code.  Response='" + response.getStatusMessage() + "'");
		assertEquals(printer.getContentType(), response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		
		// Validate that the correct parameters were passed in
		GeneratePrintedOutputArgs generatePrintedOutputArgs = generatePrintedOutputMock.getGeneratePrintedOutputArgs();
		assertArrayEquals(formData.getBytes(), generatePrintedOutputArgs.getData().getInlineData());
		assertEquals(TestUtils.SAMPLE_FORM.getFileName().toString(), generatePrintedOutputArgs.getUrlOrFilename());
		PrintedOutputOptions printedOutputOptions = generatePrintedOutputArgs.getPrintedOutputOptions();
		assertAll(
				()->assertEquals(TestUtils.SAMPLE_FORM.getParent(), printedOutputOptions.getContentRoot().getPath()),
				()->assertEquals(copies, printedOutputOptions.getCopies()),
				()->assertEquals(Paths.get(debugDirData), printedOutputOptions.getDebugDir()),
				()->assertEquals(Locale.forLanguageTag(localeData), printedOutputOptions.getLocale()),
				()->assertArrayEquals(xciData.getBytes(), printedOutputOptions.getXci().getInlineData())
		);
	}

	public MockTraditionalOutputService mockGeneratePrintedOutput(byte[] resultDataBytes, String contentType) throws NoSuchFieldException {
		Document renderResult = mockDocumentFactory.create(resultDataBytes);
		renderResult.setContentType(contentType);
		MockTraditionalOutputService generatePrintOutputMock = MockTraditionalOutputService.createDocumentMock(renderResult);
		junitx.util.PrivateAccessor.setField(underTest, "outputServiceFactory", (Supplier<TraditionalOutputService>)()->(TraditionalOutputService)generatePrintOutputMock);
		return generatePrintOutputMock;
	}
}
