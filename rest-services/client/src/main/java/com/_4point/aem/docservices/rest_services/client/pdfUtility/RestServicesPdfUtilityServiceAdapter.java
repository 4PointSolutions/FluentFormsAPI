package com._4point.aem.docservices.rest_services.client.pdfUtility;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.Builder.RestClientFactory;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService.PdfUtilityException;
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
	
	private final RestClient convertPdfToXdpRestClient;

	private RestServicesPdfUtilityServiceAdapter(BuilderImpl builder, Supplier<String> correlationIdFn) {
		super(correlationIdFn);
		this.convertPdfToXdpRestClient = builder.createClient(PDF_UTILITY_SERVICE_NAME, CONVERT_PDF_TO_XDP_METHOD_NAME);
	}

	@Override
	public Document clone(Document doc) throws PdfUtilityException {
		throw new UnsupportedOperationException("clone is not supported as a remote operation.");
	}

	@Override
	public Document convertPDFtoXDP(Document doc) throws PdfUtilityException {
		try(MultipartPayload payload = convertPdfToXdpRestClient.multipartPayloadBuilder()
												 				.add(DOCUMENT_PARAM_NAME, Objects.requireNonNull(doc, "document parameter cannot be null.").getInputStream(), ContentType.APPLICATION_PDF)
												 				.build()) {
			return payload.postToServer(ContentType.APPLICATION_XDP)
						  .map(RestServicesServiceAdapter::responseToDoc)
						  .orElseThrow(()->new PdfUtilityException("Error - empty response from AEM server."));
		} catch (IOException e) {
			throw new PdfUtilityException("I/O Error while converting PDF to XDP. (" + convertPdfToXdpRestClient.target() + ").", e);
		} catch (RestClientException e) {
			throw new PdfUtilityException("Error while POSTing to server (" + convertPdfToXdpRestClient.target() + ").", e);
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
	
	public static PdfUtilityServiceBuilder builder(RestClientFactory clientFactory) {
		return new PdfUtilityServiceBuilder(clientFactory);
	}

	public static class PdfUtilityServiceBuilder implements Builder {
		private final BuilderImpl builder;

		private PdfUtilityServiceBuilder(RestClientFactory clientFactory) {
			this.builder = new BuilderImpl(clientFactory);
		}

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
		public PdfUtilityServiceBuilder aemServerType(AemServerType serverType) {
			builder.aemServerType(serverType);
			return this;
		}

		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}
		
		public RestServicesPdfUtilityServiceAdapter build() {
			return new RestServicesPdfUtilityServiceAdapter(builder, this.getCorrelationIdFn());
		}
	}
}
