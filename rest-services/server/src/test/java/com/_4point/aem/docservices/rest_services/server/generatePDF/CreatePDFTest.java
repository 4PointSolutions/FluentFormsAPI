
package com._4point.aem.docservices.rest_services.server.generatePDF;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.function.Supplier;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.TestUtils;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.impl.generatePDF.CreatePDFResultImpl;
import com._4point.aem.fluentforms.impl.generatePDF.PDFSettings;
import com._4point.aem.fluentforms.impl.generatePDF.SecuritySettings;
import com._4point.aem.fluentforms.impl.generatePDF.TraditionalGeneratePDFService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.generatePDF.ExceptionalMockTraditionalGeneratePDFService;
import com._4point.aem.fluentforms.testing.generatePDF.MockTraditionalGeneratePDFService;
import com._4point.aem.fluentforms.testing.generatePDF.MockTraditionalGeneratePDFService.GeneratePDFResultArgs;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)

@ExtendWith(MockitoExtension.class)
public class CreatePDFTest {
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String TEXT_HTML = "text/html";
	private static final String DATA_PARAM_NAME = "data";
	private static final String FILE_EXTENSION = "fileExtension";
	private static final String FILE_TYPE_SETTINGS = "fileTypeSettings";
	private static final String PDF_SETTINGS = "pdfSettings";
	private static final String SECURITY_SETTINGS = "securitySettings";
	private static final String SETTING_DOC = "settingDoc";
	private static final String XMP_DOC = "xmpDoc";

	private final AemContext aemContext = new AemContext();
	private final CreatePDF underTest = new CreatePDF();
	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();

	@BeforeEach void setUp() throws Exception { 
		junitx.util.PrivateAccessor.setField(underTest, "docFactory",
				(DocumentFactory)mockDocumentFactory); }

	@Test
	void testDoPost_HappyPath_JustForm() throws ServletException, IOException, NoSuchFieldException {
		String resultData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><createPDFResult><createdDoc createdDocValue=\"c3JjXHRlc3RccmVzb3VyY2VzXFNhbXBsZUZvcm1zXFNhbXBsZUZvcm0uZG9jeA==\"/><logDoc/></createPDFResult>";
		String templateData = TestUtils.SAMPLE_FORM_DOCX.toString();
		String fileTypeSettings = "Filetype Settings";
		PDFSettings pdfSettings = PDFSettings.High_Quality_Print;
		SecuritySettings securitySettings = SecuritySettings.Adobe_Policy_Server;
		String settingsDoc = "settingsDoc";
		String xmpDoc = "xmpDoc";
		CreatePDFResultImpl createResult = new CreatePDFResultImpl();
		createResult.setCreatedDocument(mockDocumentFactory.create(templateData.getBytes()));
		MockTraditionalGeneratePDFService generatePDFResultMock = mockGeneratePdf(createResult);

		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		request.addRequestParameter(DATA_PARAM_NAME, templateData);
		request.addRequestParameter(FILE_EXTENSION, "docx");
		request.addRequestParameter(FILE_TYPE_SETTINGS, fileTypeSettings);
		request.addRequestParameter(PDF_SETTINGS, pdfSettings.toString());
		request.addRequestParameter(SECURITY_SETTINGS, securitySettings.toString());
		request.addRequestParameter(SETTING_DOC, settingsDoc.getBytes(), APPLICATION_PDF);
		request.addRequestParameter(XMP_DOC, xmpDoc.getBytes(), APPLICATION_PDF);

		underTest.doPost(request, response);
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_XML, response.getContentType());
		assertEquals(resultData.trim(), response.getOutputAsString().trim());

		GeneratePDFResultArgs generatePDFResultArgs = generatePDFResultMock.getGeneratePDFResultArgs();
		assertNotNull(generatePDFResultArgs.getInputDoc());
		assertEquals("docx", generatePDFResultArgs.getInputFileExtension());
		assertAll(() -> assertEquals(fileTypeSettings, generatePDFResultArgs.getFileTypeSettings()),
				() -> assertEquals(pdfSettings, generatePDFResultArgs.getPdfSettings()),
				() -> assertEquals(securitySettings.getSecuritySetting(), generatePDFResultArgs.getSecuritySettings().getSecuritySetting()));
	}

	@Test
	void testDoPost_HappyPath_JustForm_Optionsal_Parameters_Null()
			throws ServletException, IOException, NoSuchFieldException {
		String resultData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><createPDFResult><createdDoc createdDocValue=\"c3JjXHRlc3RccmVzb3VyY2VzXFNhbXBsZUZvcm1zXFNhbXBsZUZvcm0uZG9jeA==\"/><logDoc/></createPDFResult>";
		String templateData = TestUtils.SAMPLE_FORM_DOCX.toString();
		CreatePDFResultImpl createResult = new CreatePDFResultImpl();
		createResult.setCreatedDocument(mockDocumentFactory.create(templateData.getBytes()));
		MockTraditionalGeneratePDFService generatePDFResultMock = mockGeneratePdf(createResult);

		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		request.addRequestParameter(DATA_PARAM_NAME, templateData);
		request.addRequestParameter(FILE_EXTENSION, "docx");

		underTest.doPost(request, response);
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_XML, response.getContentType());
		assertEquals(resultData.trim(), response.getOutputAsString().trim());

		GeneratePDFResultArgs generatePDFResultArgs = generatePDFResultMock.getGeneratePDFResultArgs();
		assertNotNull(generatePDFResultArgs.getInputDoc());
		assertEquals("docx", generatePDFResultArgs.getInputFileExtension());
		assertAll(() -> assertNull(generatePDFResultArgs.getFileTypeSettings()),
				() -> assertNull(generatePDFResultArgs.getPdfSettings()),
				() -> assertNull(generatePDFResultArgs.getSecuritySettings()),
				() -> assertNull(generatePDFResultArgs.getSettingsDoc()),
				() -> assertNull(generatePDFResultArgs.getXmpDoc()));
	}

	@Test
	void testDoPost_BadAccept() throws ServletException, IOException, NoSuchFieldException {
		String templateData = TestUtils.SAMPLE_FORM_DOCX.toString();
		String pdfSettings = "High_Quality_Print";
		CreatePDFResultImpl createResult = new CreatePDFResultImpl();
		createResult.setCreatedDocument(mockDocumentFactory.create(templateData.getBytes()));
		MockTraditionalGeneratePDFService generatePDFResultMock = mockGeneratePdf(createResult);

		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		request.addRequestParameter(DATA_PARAM_NAME, templateData);
		request.addRequestParameter(FILE_EXTENSION, "docx");
		request.addRequestParameter(PDF_SETTINGS, pdfSettings);
		request.addHeader("Accept", TEXT_HTML);

		underTest.doPost(request, response);
		assertEquals(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, response.getStatus());
		String statusMsg = response.getStatusMessage();
		assertThat(statusMsg, containsStringIgnoringCase(TEXT_HTML));
		assertThat(statusMsg, containsStringIgnoringCase(APPLICATION_XML));
	}

	@Test
	void testDoPost_Bad_GeneratePDFServiceException() throws ServletException, IOException, NoSuchFieldException {
		String templateData = TestUtils.SAMPLE_FORM_DOCX.toString();
		String pdfSettings = "High_Quality_Print";

		String exceptionMessage = "Exception Message";
		junitx.util.PrivateAccessor.setField(underTest, "generatePDFServiceFactory",
				(Supplier<TraditionalGeneratePDFService>) () -> (TraditionalGeneratePDFService) ExceptionalMockTraditionalGeneratePDFService
				.create(exceptionMessage));

		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		request.addRequestParameter(DATA_PARAM_NAME, templateData);
		request.addRequestParameter(FILE_EXTENSION, "docx");
		request.addRequestParameter(PDF_SETTINGS, pdfSettings);

		underTest.doPost(request, response);
		assertEquals(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		String statusMsg = response.getStatusMessage();
		assertThat(statusMsg, containsStringIgnoringCase("Internal Error while converting doc to PDF"));
		assertThat(statusMsg, containsStringIgnoringCase(exceptionMessage));
	}

	@Test
	void testConvertGeneratePdfResultToXml_EmptyResult() throws InternalServerErrorException,
	ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		String resultXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><createPDFResult><createdDoc/><logDoc/></createPDFResult>";
		CreatePDFResultImpl createPDFResult = new CreatePDFResultImpl();

		String responseXml = CreatePDF.convertCreatePDFResultToXml(createPDFResult);
		assertEquals(resultXml, responseXml);
	}

	@Test
	void testConvertGeneratePdfResultToXml() throws InternalServerErrorException, ParserConfigurationException,
	TransformerFactoryConfigurationError, TransformerException {
		String resultXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><createPDFResult><createdDoc createdDocValue=\"dGVzdERvUG9zdCBIYXBweSBQYXRoIFJlc3VsdA==\"/><logDoc logDocValue=\"dGVzdERvUG9zdCBIYXBweSBQYXRoIGxvZyBSZXN1bHQ=\"/></createPDFResult>";
		CreatePDFResultImpl createPDFResult = new CreatePDFResultImpl();
		setCreatePDFResultProperties(createPDFResult);
		String responseXml = CreatePDF.convertCreatePDFResultToXml(createPDFResult);
		assertEquals(resultXml, responseXml);
	}

	private void setCreatePDFResultProperties(CreatePDFResultImpl createPDFResult) {
		String data = "testDoPost Happy Path Result";
		String logData = "testDoPost Happy Path log Result";
		createPDFResult.setCreatedDocument(mockDocumentFactory.create(data.getBytes()));
		createPDFResult.setLogDocument(mockDocumentFactory.create(logData.getBytes()));
	}

	public MockTraditionalGeneratePDFService mockGeneratePdf(CreatePDFResult createPDFResult)
			throws NoSuchFieldException {
		createPDFResult.getCreatedDocument().setContentType(APPLICATION_PDF);
		MockTraditionalGeneratePDFService generatePDFResultMock = MockTraditionalGeneratePDFService
				.createGeneratePDFMock(createPDFResult);
		junitx.util.PrivateAccessor.setField(underTest, "generatePDFServiceFactory",
				(Supplier<TraditionalGeneratePDFService>) () -> (TraditionalGeneratePDFService) generatePDFResultMock);
		return generatePDFResultMock;
	}
}
