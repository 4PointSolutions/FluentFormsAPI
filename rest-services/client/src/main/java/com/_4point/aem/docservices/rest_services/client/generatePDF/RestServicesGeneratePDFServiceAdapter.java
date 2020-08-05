package com._4point.aem.docservices.rest_services.client.generatePDF;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.function.Supplier;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.MultipartTransformer;
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
	private static final String GENERATE_PDF_PATH = "/services/GeneratePDFService/CreatePDF";
	private static final String DATA_PARAM_NAME = "data";
	private static final String FILE_EXTENSION = "fileExtension";
	private static final String FILE_TYPE_SETTINGS = "fileTypeSettings";
	private static final String PDF_SETTINGS = "pdfSettings";
	private static final String SECURITY_SETTINGS = "securitySettings";
	private static final String SETTING_DOC = "settingDoc";
	private static final String XMP_DOC = "xmpDoc";

	protected RestServicesGeneratePDFServiceAdapter(WebTarget baseTarget) {
		super(baseTarget);
	}

	// Only callable from Builder
	private RestServicesGeneratePDFServiceAdapter(WebTarget target, Supplier<String> correlationId) {
		super(target, correlationId);
	}

	@Override
	public CreatePDFResult createPDF2(Document inputDoc, String inputFileExtension, String fileTypeSettings,
			PDFSettings pdfSettings, SecuritySettings securitySettings, Document settingsDoc, Document xmpDoc)
					throws GeneratePDFServiceException {
		WebTarget geneWebTarget = baseTarget.path(GENERATE_PDF_PATH);

		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			if (inputDoc != null) {
				multipart.field(DATA_PARAM_NAME, inputDoc.getInputStream(), MediaType.MULTIPART_FORM_DATA_TYPE);
			} else {
				throw new NullPointerException("inputDoc can not be null");
			}

			if (inputFileExtension != null) {
				multipart.field(FILE_EXTENSION, inputFileExtension);
			} else {
				throw new NullPointerException("fileextension can not be null");
			}

			MultipartTransformer.create(multipart)
			.transform((t) -> fileTypeSettings == null ? t : t.field(FILE_TYPE_SETTINGS, fileTypeSettings))
			.transform((t) -> pdfSettings == null ? t : t.field(PDF_SETTINGS, pdfSettings.toString()))
			.transform((t) -> securitySettings == null ? t : t.field(SECURITY_SETTINGS, securitySettings.toString()))
			.transform((t) -> {
				try {
					return settingsDoc == null ? t
							: t.field(SETTING_DOC, settingsDoc.getInlineData(),
									MediaType.MULTIPART_FORM_DATA_TYPE);
				} catch (IOException e) {
					// if we encounter an exception, then we just don't add this field. This should
					// of error shouldn't ever happen.
					return t;
				}
			}).transform((t) -> {
				try {
					return xmpDoc == null ? t
							: t.field(XMP_DOC, xmpDoc.getInlineData(), MediaType.MULTIPART_FORM_DATA_TYPE);
				} catch (IOException e) {
					// if we encounter an exception, then we just don't add this field. This should
					// of error shouldn't ever happen.
					return t;
				}
			});

			Response result = postToServer(geneWebTarget, multipart, MediaType.APPLICATION_XML_TYPE);
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String msg = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='"
						+ resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					msg += "\n" + inputStreamtoString(entityStream);
				}
				throw new GeneratePDFServiceException(msg);
			}

			if (!result.hasEntity()) {
				throw new GeneratePDFServiceException(
						"Call to server succeeded but server failed to return createPDFResult xml.  This should never happen.");
			}

			MediaType responseContentType = result.getMediaType();
			
			if (responseContentType == null || !responseContentType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
				String msg = "Response from AEM server was  "
						+ (responseContentType != null ? "content-type='" + responseContentType.toString() + "'"
								: "content-type was null")
						+ ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new GeneratePDFServiceException(msg);
			}
			CreatePDFResult createPDFResult = convertXmlToCreatePDFResult((InputStream) result.getEntity());
			return createPDFResult;
		} catch (IOException e) {
			throw new GeneratePDFServiceException(
					"I/O Error while converting document to pdf. (" + baseTarget.getUri().toString() + ").", e);
		} catch (RestServicesServiceException e) {
			throw new GeneratePDFServiceException("Error while posting to server", e);
		}
	}

	// Package visibility so that it can be unit tested.
	/* package */public static CreatePDFResult convertXmlToCreatePDFResult(InputStream createPDFResultXml)
			throws GeneratePDFServiceException {
		//DocumentBuilder db;
		CreatePDFResultImpl createPDFResult = new CreatePDFResultImpl();
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();		
			org.w3c.dom.Document doc = db.parse(createPDFResultXml);
			doc.getDocumentElement().normalize();
			getNodeValueForAttribute(doc, "createdDoc", "createdDocValue", createPDFResult);
			getNodeValueForAttribute(doc, "logDoc", "logDocValue", createPDFResult);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new GeneratePDFServiceException("Error while parsing xml", e);
		}
		return createPDFResult;
	}

	private static void getNodeValueForAttribute(org.w3c.dom.Document doc, String parentNodeName, String attributeName,
			CreatePDFResultImpl createPDFResult) {
		Element eElement = (Element) doc.getElementsByTagName(parentNodeName).item(0);
		byte[] bytesJobLog = Base64.getDecoder().decode(eElement.getAttribute(attributeName));
		if (parentNodeName.equals("createdDoc")) {
			createPDFResult.setCreatedDocument(SimpleDocumentFactoryImpl.getFactory().create(bytesJobLog));
			createPDFResult.getCreatedDocument().setContentType(APPLICATION_PDF.toString());
		} else {
			createPDFResult.setLogDocument(SimpleDocumentFactoryImpl.getFactory().create(bytesJobLog));
		}
	}

	public static GeneratePDFServiceBuilder builder() {
		return new GeneratePDFServiceBuilder();
	}

	public static class GeneratePDFServiceBuilder implements Builder {
		private BuilderImpl builder = new BuilderImpl();

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
		public GeneratePDFServiceBuilder clientFactory(Supplier<Client> clientFactory) {
			builder.clientFactory(clientFactory);
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
		public WebTarget createLocalTarget() {
			return builder.createLocalTarget();
		}

		public RestServicesGeneratePDFServiceAdapter build() {
			return new RestServicesGeneratePDFServiceAdapter(this.createLocalTarget(), this.getCorrelationIdFn());
		}

	}

}
