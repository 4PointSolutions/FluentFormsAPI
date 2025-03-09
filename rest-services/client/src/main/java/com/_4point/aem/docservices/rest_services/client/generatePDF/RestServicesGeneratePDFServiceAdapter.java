 package com._4point.aem.docservices.rest_services.client.generatePDF;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl.TriFunction;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.generatePDF.CreatePDFResultImpl;
import com._4point.aem.fluentforms.impl.generatePDF.PDFSettings;
import com._4point.aem.fluentforms.impl.generatePDF.SecuritySettings;
import com._4point.aem.fluentforms.impl.generatePDF.TraditionalGeneratePDFService;

public class RestServicesGeneratePDFServiceAdapter extends RestServicesServiceAdapter
		implements TraditionalGeneratePDFService {
	private static final String GENERATE_PDF_SERVICE_NAME = "GeneratePDFService";
	private static final String CREATE_PDF_METHOD_NAME = "CreatePDF";
	private static final String DATA_PARAM_NAME = "data";
	private static final String FILE_EXTENSION = "fileExtension";
	private static final String FILE_TYPE_SETTINGS = "fileTypeSettings";
	private static final String PDF_SETTINGS = "pdfSettings";
	private static final String SECURITY_SETTINGS = "securitySettings";
	private static final String SETTING_DOC = "settingDoc";
	private static final String XMP_DOC = "xmpDoc";


	private final RestClient createPdf2RestClient;

	// Only callable from Builder
	private RestServicesGeneratePDFServiceAdapter(BuilderImpl builder, Supplier<String> correlationId) {
		super(correlationId);
		this.createPdf2RestClient = builder.createClient(GENERATE_PDF_SERVICE_NAME, CREATE_PDF_METHOD_NAME);
	}

    @Override
    public CreatePDFResult createPDF2(Document inputDoc, String inputFileExtension, String fileTypeSettings,
                                      PDFSettings pdfSettings, SecuritySettings securitySettings, Document settingsDoc, Document xmpDoc)
            throws GeneratePDFServiceException {
        try (MultipartPayload payload = createPdf2RestClient.multipartPayloadBuilder()
                .add(DATA_PARAM_NAME, Objects.requireNonNull(inputDoc, "inputDoc can not be null"), ContentType.APPLICATION_OCTET_STREAM)
                .add(FILE_EXTENSION, Objects.requireNonNull(inputFileExtension, "file extension can not be null"))
                .addIfNotNull(FILE_TYPE_SETTINGS, fileTypeSettings)
                .addStringVersion(PDF_SETTINGS, pdfSettings)
                .addStringVersion(SECURITY_SETTINGS, securitySettings)
                .addIfNotNull(SETTING_DOC, settingsDoc, ContentType.APPLICATION_XML)
                .addIfNotNull(XMP_DOC, xmpDoc, ContentType.APPLICATION_XML)
                .build()) {

            Optional<Response> postToServerResult = payload.postToServer(ContentType.APPLICATION_XML);
            
			return postToServerResult
            			  .map(RestServicesServiceAdapter::responseToDoc)
            			  .map(uncheck(Document::getInputStream))
                    	  .map(RestServicesGeneratePDFServiceAdapter::convertXmlToCreatePDFResult)
                    	  .orElseThrow(() -> new GeneratePDFServiceException("Error - empty response from AEM server."));
        } catch (IOException e) {
            throw new GeneratePDFServiceException("I/O Error while generating PDF. (" + createPdf2RestClient.target() + ").", e);
        } catch (RestClientException e) {
            throw new GeneratePDFServiceException("Error while POSTing to server (" + createPdf2RestClient.target() + ").", e);
        } catch (GeneratePDFServiceResponseParsingException e) {
            throw new GeneratePDFServiceException("Error while converting server response to PdfResult.", e);
        }
    }

	// Package visibility so that it can be unit tested.
	/* package */ static CreatePDFResult convertXmlToCreatePDFResult(InputStream createPDFResultXml) throws GeneratePDFServiceResponseParsingException {
		CreatePDFResultImpl createPDFResult = new CreatePDFResultImpl();
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			org.w3c.dom.Document doc = db.parse(createPDFResultXml);
			doc.getDocumentElement().normalize();
			getNodeValueForAttribute(doc, "createdDoc", "createdDocValue", createPDFResult);
			getNodeValueForAttribute(doc, "logDoc", "logDocValue", createPDFResult);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new GeneratePDFServiceResponseParsingException("Error while parsing xml", e);
		}
		return createPDFResult;
	}

	private static void getNodeValueForAttribute(org.w3c.dom.Document doc, String parentNodeName, String attributeName,
			CreatePDFResultImpl createPDFResult) {
		Element eElement = (Element) doc.getElementsByTagName(parentNodeName).item(0);
		byte[] bytesJobLog = Base64.getDecoder().decode(eElement.getAttribute(attributeName));
		if (parentNodeName.equals("createdDoc")) {
			createPDFResult.setCreatedDocument(SimpleDocumentFactoryImpl.getFactory().create(bytesJobLog));
			createPDFResult.getCreatedDocument().setContentType(ContentType.APPLICATION_PDF.contentType());
		} else {
			createPDFResult.setLogDocument(SimpleDocumentFactoryImpl.getFactory().create(bytesJobLog));
			createPDFResult.getLogDocument().setContentType(ContentType.TEXT_PLAIN.contentType());
		}
	}

	public static GeneratePDFServiceBuilder builder(TriFunction<AemConfig, String, Supplier<String>, RestClient> clientFactory) {
		return new GeneratePDFServiceBuilder(clientFactory);
	}

	public static class GeneratePDFServiceBuilder implements Builder {
		private final BuilderImpl builder;

		public GeneratePDFServiceBuilder(TriFunction<AemConfig, String, Supplier<String>, RestClient> clientFactory) {
			this.builder = new BuilderImpl(clientFactory);
		}

		@Override
		public GeneratePDFServiceBuilder machineName(String machineName) {
			builder.machineName(machineName);
			return this;
		}

		@Override
		public GeneratePDFServiceBuilder port(int port) {
			builder.port(port);
			return this;
		}

		@Override
		public GeneratePDFServiceBuilder useSsl(boolean useSsl) {
			builder.useSsl(useSsl);
			return this;
		}

		@Override
		public GeneratePDFServiceBuilder basicAuthentication(String username, String password) {
			builder.basicAuthentication(username, password);
			return this;
		}

		@Override
		public GeneratePDFServiceBuilder correlationId(Supplier<String> correlationIdFn) {
			builder.correlationId(correlationIdFn);
			return this;
		}

		@Override
		public Supplier<String> getCorrelationIdFn() {
			return builder.getCorrelationIdFn();
		}

		@Override
		public GeneratePDFServiceBuilder aemServerType(AemServerType serverType) {
			builder.aemServerType(serverType);
			return this;
		}
		
		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}

		public RestServicesGeneratePDFServiceAdapter build() {
			return new RestServicesGeneratePDFServiceAdapter(builder, this.getCorrelationIdFn());
		}
	}

	@SuppressWarnings("serial")
	private static class GeneratePDFServiceResponseParsingException extends RuntimeException {

		public GeneratePDFServiceResponseParsingException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
