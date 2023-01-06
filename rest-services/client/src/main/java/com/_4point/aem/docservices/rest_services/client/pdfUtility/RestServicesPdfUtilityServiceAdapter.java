package com._4point.aem.docservices.rest_services.client.pdfUtility;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.core.Response.Status.Family;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService.PdfUtilityException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.pdfUtility.TraditionalPdfUtilityService;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesOptionSpec;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesResult;
import com.adobe.fd.pdfutility.services.client.RedactionOptionSpec;
import com.adobe.fd.pdfutility.services.client.RedactionResult;
import com.adobe.fd.pdfutility.services.client.SanitizationResult;

public class RestServicesPdfUtilityServiceAdapter extends RestServicesServiceAdapter implements TraditionalPdfUtilityService {

	private static final String PDF_UTILITY_SERVICE_NAME = "PdfUtility";
	private static final String CONVERT_PDF_TO_XDP_METHOD_NAME = "ConvertPdfToXdp";

	private static final String DOCUMENT_PARAM_NAME = "document";

	// Only callable from Builder
	private RestServicesPdfUtilityServiceAdapter(WebTarget baseTarget, Supplier<String> correlationIdFn, AemServerType aemServerType) {
		super(baseTarget, correlationIdFn, aemServerType);
	}

	@Override
	public Document clone(Document doc) throws PdfUtilityException {
		throw new UnsupportedOperationException("clone is not supported as a remote operation.");
	}

	@Override
	public Document convertPDFtoXDP(Document doc) throws PdfUtilityException {
		WebTarget convertPdfTarget = baseTarget.path(constructStandardPath(PDF_UTILITY_SERVICE_NAME, CONVERT_PDF_TO_XDP_METHOD_NAME));
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DOCUMENT_PARAM_NAME, Objects.requireNonNull(doc, "document parameter cannot be null.").getInputStream(), APPLICATION_PDF);			
			Response result = postToServer(convertPdfTarget, multipart, APPLICATION_XDP);
			
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String message = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					message += "\n" + inputStreamtoString(entityStream);
				}
				throw new PdfUtilityException(message);
			}
			if (!result.hasEntity()) {
				throw new PdfUtilityException("Call to server succeeded but server failed to return document.  This should never happen.");
			}

			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			if ( responseContentType == null || !APPLICATION_XDP.isCompatible(MediaType.valueOf(responseContentType))) {
				String msg = "Response from AEM server was not a PDF.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new PdfUtilityException(msg);
			}

			Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
			resultDoc.setContentType(APPLICATION_XDP.toString());
			return resultDoc;
		} catch (IOException e) {
			throw new PdfUtilityException("I/O Error while converting PDF to XDP. (" + baseTarget.getUri().toString() + ").", e);
		} catch (RestServicesServiceException e) {
			throw new PdfUtilityException("Error while POSTing to server (" + baseTarget.getUri().toString() + ").", e);
		}
	}

	@Override
	public PDFPropertiesResult getPDFProperties(Document doc, PDFPropertiesOptionSpec pdfPropOptionsSpec) throws PdfUtilityException {
		throw new UnsupportedOperationException("getPDFProperties is not implemented yet.");
	}

	@Override
	public List<Document> multiclone(Document doc, int numClones) throws PdfUtilityException {
		throw new UnsupportedOperationException("multiclone is not supported as a remote operation.");
	}

	@Override
	public RedactionResult redact(Document doc, RedactionOptionSpec redactOptionsSpec) throws PdfUtilityException {
		throw new UnsupportedOperationException("redact is not implemented yet.");
	}

	@Override
	public SanitizationResult sanitize(Document doc) throws PdfUtilityException {
		throw new UnsupportedOperationException("sanitize is not implemented yet.");
	}
	
	public static PdfUtilityServiceBuilder builder() {
		return new PdfUtilityServiceBuilder();
	}

	public static class PdfUtilityServiceBuilder implements Builder {
		private BuilderImpl builder = new BuilderImpl();

		@Override
		public PdfUtilityServiceBuilder machineName(String machineName) {
			builder.machineName(machineName);
			return this;
		}

		@Override
		public PdfUtilityServiceBuilder port(int port) {
			builder.port(port);
			return this;
		}

		@Override
		public PdfUtilityServiceBuilder useSsl(boolean useSsl) {
			builder.useSsl(useSsl);
			return this;
		}

		@Override
		public PdfUtilityServiceBuilder clientFactory(Supplier<Client> clientFactory) {
			builder.clientFactory(clientFactory);
			return this;
		}

		@Override
		public PdfUtilityServiceBuilder basicAuthentication(String username, String password) {
			builder.basicAuthentication(username, password);
			return this;
		}

		@Override
		public PdfUtilityServiceBuilder correlationId(Supplier<String> correlationIdFn) {
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

		@Override
		public PdfUtilityServiceBuilder aemServerType(AemServerType serverType) {
			builder.aemServerType(serverType);
			return this;
		}

		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}
		
		public RestServicesPdfUtilityServiceAdapter build() {
			return new RestServicesPdfUtilityServiceAdapter(this.createLocalTarget(), this.getCorrelationIdFn(), this.getAemServerType());
		}
	}
}
