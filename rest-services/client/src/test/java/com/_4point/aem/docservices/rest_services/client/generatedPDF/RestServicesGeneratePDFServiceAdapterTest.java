/*
 * package com._4point.aem.docservices.rest_services.client.generatedPDF;
 * 
 * import static org.hamcrest.CoreMatchers.containsStringIgnoringCase; import
 * static org.hamcrest.MatcherAssert.assertThat; import static
 * org.junit.jupiter.api.Assertions.assertAll; import static
 * org.junit.jupiter.api.Assertions.assertArrayEquals; import static
 * org.junit.jupiter.api.Assertions.assertEquals; import static
 * org.junit.jupiter.api.Assertions.assertFalse; import static
 * org.junit.jupiter.api.Assertions.assertNotNull; import static
 * org.junit.jupiter.api.Assertions.assertThrows; import static
 * org.junit.jupiter.api.Assertions.assertTrue; import static
 * org.mockito.ArgumentMatchers.eq; import static org.mockito.Mockito.when;
 * 
 * import java.io.ByteArrayInputStream; import java.io.IOException; import
 * java.io.InputStream; import java.util.List; import
 * java.util.function.Consumer;
 * 
 * import javax.ws.rs.client.Client; import javax.ws.rs.client.Entity; import
 * javax.ws.rs.client.Invocation.Builder; import javax.ws.rs.client.WebTarget;
 * import javax.ws.rs.core.MediaType; import javax.ws.rs.core.Response; import
 * javax.ws.rs.core.Response.Status; import
 * javax.ws.rs.core.Response.StatusType;
 * 
 * import org.apache.commons.io.IOUtils; import
 * org.glassfish.jersey.media.multipart.FormDataBodyPart; import
 * org.glassfish.jersey.media.multipart.FormDataMultiPart; import
 * org.junit.jupiter.api.Test; import
 * org.junit.jupiter.api.extension.ExtendWith; import
 * org.junit.jupiter.params.ParameterizedTest; import
 * org.junit.jupiter.params.provider.EnumSource; import org.mockito.Answers;
 * import org.mockito.ArgumentCaptor; import org.mockito.Captor; import
 * org.mockito.Mock; import org.mockito.junit.jupiter.MockitoExtension;
 * 
 * import com._4point.aem.docservices.rest_services.client.generatePDF.
 * RestServicesGeneratePDFServiceAdapter; import
 * com._4point.aem.docservices.rest_services.client.generatePDF.
 * RestServicesGeneratePDFServiceAdapter.GeneratePDFServiceBuilder; import
 * com._4point.aem.fluentforms.api.Document; import
 * com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult; import
 * com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.
 * GeneratePDFServiceException; import
 * com._4point.aem.fluentforms.impl.generatePDF.PDFSettings; import
 * com._4point.aem.fluentforms.impl.generatePDF.SecuritySettings; import
 * com._4point.aem.fluentforms.testing.MockDocumentFactory;
 * 
 * @ExtendWith(MockitoExtension.class) public class
 * RestServicesGeneratePDFServiceAdapterTest { private static final String
 * CORRELATION_ID_HTTP_HDR = "X-Correlation-ID"; private static final String
 * CORRELATION_ID = "correlationId"; private static final String
 * TEST_MACHINE_NAME = "testmachinename"; private static final int
 * TEST_MACHINE_PORT = 8080;
 * 
 * private static final MediaType APPLICATION_XML = new MediaType("application",
 * "xml");
 * 
 * @Mock(answer = Answers.RETURNS_SELF) Client client; // answers used to mock
 * Client's fluent interface.
 * 
 * @Mock WebTarget target;
 * 
 * @Mock Response response;
 * 
 * @Mock Builder builder;
 * 
 * @Mock StatusType statusType;
 * 
 * @Captor ArgumentCaptor<String> machineName;
 * 
 * @Captor ArgumentCaptor<String> path;
 * 
 * @SuppressWarnings("rawtypes")
 * 
 * @Captor ArgumentCaptor<Entity> entity;
 * 
 * @Captor ArgumentCaptor<String> correlationId;
 * 
 * RestServicesGeneratePDFServiceAdapter underTest; private static final String
 * POPULATED_CREATE_PDF_RESULT_XML =
 * "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><createPDFResult><createdDoc createdDocValue=\"c3JjXHRlc3RccmVzb3VyY2VzXFNhbXBsZUZvcm1zXFNhbXBsZUZvcm0uZG9jeA==\"/><logDoc/></createPDFResult>"
 * ; private static final String EMPTY_CREATE_PDF_RESULT_XML =
 * "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><createPDFResult><createdDoc/><logDoc/></createPDFResult>"
 * ;
 * 
 * @Test void testCreatePDF() throws Exception { Document responseData =
 * MockDocumentFactory.GLOBAL_INSTANCE.create(POPULATED_CREATE_PDF_RESULT_XML.
 * getBytes());
 * 
 * setupRestClientMocks(true, responseData, APPLICATION_XML);
 * 
 * GeneratePDFServiceBuilder generatePDFServiceBuilder =
 * RestServicesGeneratePDFServiceAdapter.builder().machineName(
 * TEST_MACHINE_NAME) .port(TEST_MACHINE_PORT) .basicAuthentication("username",
 * "password") .useSsl(false) .correlationId(()->CORRELATION_ID)
 * .clientFactory(()->client);
 * 
 * underTest = generatePDFServiceBuilder.build(); byte[] doc =
 * "document Data".getBytes(); Document inputDoc =
 * MockDocumentFactory.GLOBAL_INSTANCE.create("document Data".getBytes());
 * Document settingsDoc = null; Document xmpDoc = null; CreatePDFResult result =
 * underTest.createPDF2(inputDoc, "docx", "Filetype Settings",
 * PDFSettings.PDFA1b_2005_RGB, SecuritySettings.Certificate_Security,
 * settingsDoc, xmpDoc); // Validate the result was returned as expected.
 * validatePopulatedResult(result); // Validate that the arguments were
 * populated as expected. // Make sure that the arguments we passed in are
 * transmitted correctly.
 * 
 * @SuppressWarnings("unchecked") Entity<FormDataMultiPart> postedEntity =
 * (Entity<FormDataMultiPart>)entity.getValue(); FormDataMultiPart postedData =
 * postedEntity.getEntity();
 * 
 * assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE,
 * postedEntity.getMediaType()); validateDocumentFormField(postedData, "data",
 * MediaType.MULTIPART_FORM_DATA_TYPE, doc); }
 * 
 * private static void validateDocumentFormField(FormDataMultiPart postedData,
 * String fieldName, MediaType expectedMediaType, byte[] expectedData) throws
 * IOException { List<FormDataBodyPart> pdfFields =
 * postedData.getFields(fieldName); assertEquals(1, pdfFields.size());
 * 
 * FormDataBodyPart pdfPart = pdfFields.get(0); assertEquals(expectedMediaType,
 * pdfPart.getMediaType()); byte[] pdfBytes = IOUtils.toByteArray((InputStream)
 * pdfPart.getEntity()); assertArrayEquals(expectedData, pdfBytes); // TODO:
 * Need to figure out how to test for entity. }
 * 
 * private void validatePopulatedResult(CreatePDFResult result) {
 * assertFalse(result.getCreatedDocument().isEmpty());
 * assertTrue(result.getLogDocument().isEmpty()); }
 * 
 * 
 * private void setupRestClientMocks(boolean setupCorrelationId, Document
 * responseData, MediaType produces) throws IOException {
 * setupRestClientMocks(setupCorrelationId, responseData, produces, produces,
 * Response.Status.OK); }
 * 
 * private void setupRestClientMocks(boolean setupCorrelationId, Document
 * responseData, MediaType accepts, MediaType produces, Response.Status status)
 * throws IOException { // TODO: Change this based on
 * https://maciejwalkowiak.com/mocking-fluent-interfaces/
 * when(client.target(machineName.capture())).thenReturn(target);
 * when(target.path(path.capture())).thenReturn(target);
 * when(target.request()).thenReturn(builder);
 * when(builder.accept(accepts)).thenReturn(builder);
 * when(builder.post(entity.capture())).thenReturn(response);
 * when(response.getStatusInfo()).thenReturn(status); if (responseData != null)
 * { when(response.hasEntity()).thenReturn(true);
 * when(response.getEntity()).thenReturn(new
 * ByteArrayInputStream(responseData.getInlineData())); if
 * (Status.OK.getFamily().equals(status.getFamily())) {
 * when(response.getMediaType()).thenReturn(produces); } } else {
 * when(response.hasEntity()).thenReturn(false); }
 * 
 * if (setupCorrelationId) { when(builder.header(eq(CORRELATION_ID_HTTP_HDR),
 * correlationId.capture())).thenReturn(builder); } }
 * 
 * private enum ErrorResponseScenario {
 * INTERNAL_SERVER_ERROR(ErrorResponseScenario::setupInternalServerError,
 * ErrorResponseScenario::validateInternalServerError),
 * NO_ENTITY(ErrorResponseScenario::setupNoEntity,
 * ErrorResponseScenario::validateNoEntity),
 * HTML_RESPONSE(ErrorResponseScenario::setupHtmlResponse,
 * ErrorResponseScenario::validateHtmlResponse),
 * NO_CONTENT_TYPE_RESPONSE(ErrorResponseScenario::noContentTypeInResponse,
 * ErrorResponseScenario::validateNoContentTypeInResponse);
 * 
 * 
 * private static final String HTML_ERROR_PAGE_HTML = "<HTML>ErrorPage</HTML>";
 * Consumer<RestServicesGeneratePDFServiceAdapterTest> restClientMocksSetup;
 * Consumer<String> validateExceptionMessage;
 * 
 * private
 * ErrorResponseScenario(Consumer<RestServicesGeneratePDFServiceAdapterTest>
 * restClientMocksSetup, Consumer<String> validateExceptionMessage) {
 * this.restClientMocksSetup = restClientMocksSetup;
 * this.validateExceptionMessage = validateExceptionMessage; }
 * 
 * private static void
 * setupInternalServerError(RestServicesGeneratePDFServiceAdapterTest test) {
 * try { test.setupRestClientMocks(false,
 * MockDocumentFactory.GLOBAL_INSTANCE.create(HTML_ERROR_PAGE_HTML.getBytes()),
 * APPLICATION_XML, null, Response.Status.INTERNAL_SERVER_ERROR); } catch
 * (IOException e) { throw new
 * IllegalStateException("IO Exception while setting up RestClientMocks.", e); }
 * }
 * 
 * private static void setupNoEntity(RestServicesGeneratePDFServiceAdapterTest
 * test) { try { test.setupRestClientMocks(false, null, APPLICATION_XML); }
 * catch (IOException e) { throw new
 * IllegalStateException("IO Exception while setting up RestClientMocks.", e); }
 * }
 * 
 * private static void
 * setupHtmlResponse(RestServicesGeneratePDFServiceAdapterTest test) { try {
 * test.setupRestClientMocks(false, MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT,
 * APPLICATION_XML, MediaType.TEXT_HTML_TYPE, Response.Status.OK); } catch
 * (IOException e) { throw new
 * IllegalStateException("IO Exception while setting up RestClientMocks.", e); }
 * }
 * 
 * private static void
 * noContentTypeInResponse(RestServicesGeneratePDFServiceAdapterTest test) { try
 * { test.setupRestClientMocks(false, MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT,
 * APPLICATION_XML, null, Response.Status.OK); } catch (IOException e) { throw
 * new IllegalStateException("IO Exception while setting up RestClientMocks.",
 * e); } }
 * 
 * private static void validateInternalServerError(String exMsg) { assertAll(
 * ()->assertThat(exMsg, containsStringIgnoringCase("Call to server failed")),
 * ()->assertThat(exMsg,
 * containsStringIgnoringCase(Integer.toString(Response.Status.
 * INTERNAL_SERVER_ERROR.getStatusCode()))), ()->assertThat(exMsg,
 * containsStringIgnoringCase(Response.Status.INTERNAL_SERVER_ERROR.
 * getReasonPhrase())), ()->assertThat(exMsg,
 * containsStringIgnoringCase(HTML_ERROR_PAGE_HTML)) ); }
 * 
 * private static void validateNoEntity(String exMsg) { assertAll(
 * ()->assertThat(exMsg,
 * containsStringIgnoringCase("Call to server succeeded")),
 * ()->assertThat(exMsg,
 * containsStringIgnoringCase("server failed to return createPDFResult xml")) );
 * }
 * 
 * private static void validateHtmlResponse(String exMsg) { assertAll(
 * ()->assertThat(exMsg,
 * containsStringIgnoringCase("Response from AEM server was")),
 * ()->assertThat(exMsg, containsStringIgnoringCase("content-type")),
 * ()->assertThat(exMsg, containsStringIgnoringCase(MediaType.TEXT_HTML)) ); }
 * 
 * private static void validateNoContentTypeInResponse(String exMsg) {
 * assertAll( ()->assertThat(exMsg,
 * containsStringIgnoringCase("Response from AEM server was")),
 * ()->assertThat(exMsg, containsStringIgnoringCase("content-type")),
 * ()->assertThat(exMsg, containsStringIgnoringCase("null")) ); } }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource void testCreatePDF(ErrorResponseScenario scenario) throws
 * Exception { scenario.restClientMocksSetup.accept(this);
 * GeneratePDFServiceBuilder generatePDFServiceBuilder =
 * RestServicesGeneratePDFServiceAdapter.builder()
 * .machineName(TEST_MACHINE_NAME) .port(TEST_MACHINE_PORT)
 * .basicAuthentication("username", "password") .useSsl(false)
 * .clientFactory(()->client); underTest = generatePDFServiceBuilder.build();
 * byte[] inputBytes = "document Data".getBytes(); Document inputDoc =
 * MockDocumentFactory.GLOBAL_INSTANCE.create(inputBytes); Document settingsDoc
 * = null; Document xmpDoc = null; GeneratePDFServiceException ex =
 * assertThrows(GeneratePDFServiceException.class,()->underTest.createPDF2(
 * inputDoc, "pptx", "Filetype Settings", PDFSettings.PDFA1b_2005_RGB,
 * SecuritySettings.Certificate_Security, settingsDoc, xmpDoc)); String msg =
 * ex.getMessage(); assertNotNull(msg);
 * scenario.validateExceptionMessage.accept(msg); }
 * 
 * @Test void testConvertXmlToCreatePDFResult() throws Exception {
 * CreatePDFResult createPDFResult =
 * RestServicesGeneratePDFServiceAdapter.convertXmlToCreatePDFResult(new
 * ByteArrayInputStream(POPULATED_CREATE_PDF_RESULT_XML.getBytes()));
 * validatePopulatedResult(createPDFResult); }
 * 
 * @Test void testConvertXmlToAssemblerEmptyResult() throws Exception {
 * CreatePDFResult result =
 * RestServicesGeneratePDFServiceAdapter.convertXmlToCreatePDFResult(new
 * ByteArrayInputStream(EMPTY_CREATE_PDF_RESULT_XML.getBytes()));
 * validateEmptyResult(result); }
 * 
 * private void validateEmptyResult(CreatePDFResult result) {
 * assertTrue(result.getCreatedDocument().isEmpty());
 * assertTrue(result.getLogDocument().isEmpty()); }
 * 
 * }
 */