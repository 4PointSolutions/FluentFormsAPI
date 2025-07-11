package com._4point.aem.docservices.rest_services.client.output;

//import static com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter.;

import java.io.IOException;
import java.util.Map;
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
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;
import com.adobe.fd.output.api.RenderType;

public class RestServicesOutputServiceAdapter extends RestServicesServiceAdapter implements TraditionalOutputService {

	private static final String OUTPUT_SERVICE_NAME = "OutputService";
	private static final String GENERATE_PDF_OUTPUT_METHOD = "GeneratePdfOutput";
	private static final String GENERATE_PRINTED_OUTPUT_METHOD = "GeneratePrintedOutput";

	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";
	private static final String ACROBAT_VERSION_PARAM = "outputOptions.acrobatVersion";
	private static final String CONTENT_ROOT_PARAM = "outputOptions.contentRoot";
	private static final String DEBUG_DIR_PARAM = "outputOptions.debugDir";
	private static final String EMBED_FONTS_PARAM = "outputOptions.embedFonts";
	private static final String LINEARIZED_PDF_PARAM = "outputOptions.linearizedPdf";
	private static final String LOCALE_PARAM = "outputOptions.locale";
	private static final String RETAIN_PDF_FORM_STATE_PARAM = "outputOptions.retainPdfFormState";
	private static final String RETAIN_UNSIGNED_SIGNATURE_FIELDS_PARAM = "outputOptions.retainUnsignedSignatureFields";
	private static final String TAGGED_PDF_PARAM = "outputOptions.taggedPdf";
	private static final String XCI_PARAM = "outputOptions.xci";
	private static final String COPIES_PARAM = "outputOptions.copies";
	private static final String PAGINATION_OVERRIDE_PARAM = "outputOptions.paginationOverride";
	private static final String PRINT_CONFIG_PARAM = "outputOptions.printConfig";
	
	private final RestClient generatePdfOoutputRestClient;
	private final RestClient generatePrintedOutputRestClient;
	
	// Only callable from Builder
	private RestServicesOutputServiceAdapter(BuilderImpl builder, Supplier<String> correlationIdFn) {
		super(correlationIdFn);
		this.generatePdfOoutputRestClient = builder.createClient(OUTPUT_SERVICE_NAME, GENERATE_PDF_OUTPUT_METHOD);
		this.generatePrintedOutputRestClient = builder.createClient(OUTPUT_SERVICE_NAME, GENERATE_PRINTED_OUTPUT_METHOD);
	}

	@Override
	public Document generatePDFOutput(Document template, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		return internalGeneratePDFOutput(template, null, data, pdfOutputOptions);
	}


	@Override
	public Document generatePDFOutput(String urlOrFileName, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		return internalGeneratePDFOutput(null, urlOrFileName, data, pdfOutputOptions);
	}

	private Document internalGeneratePDFOutput(Document template, String templateStr, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		if (template == null && templateStr == null) {
			throw new NullPointerException("template parameter cannot be null.");
		}
		Objects.requireNonNull(pdfOutputOptions, "PdfOutputOptions Argument cannot be null.");
		
		try(MultipartPayload payload = generatePdfOoutputRestClient.multipartPayloadBuilder()
				 								 .addIfNotNull(DATA_PARAM, data, ContentType.APPLICATION_XML)
				 								 .addIfNotNull(TEMPLATE_PARAM, template, ContentType.APPLICATION_XDP)	// We're currently labeling everything as an XDP but we accept PDFs too.
				 								 .addIfNotNull(TEMPLATE_PARAM, templateStr)
				 								 .addStringVersion(ACROBAT_VERSION_PARAM, pdfOutputOptions.getAcrobatVersion())
				 								 .addStringVersionOfPath(CONTENT_ROOT_PARAM, pdfOutputOptions.getContentRoot())
				 								 .addStringVersion(DEBUG_DIR_PARAM, pdfOutputOptions.getDebugDir())
				 								 .addStringVersion(EMBED_FONTS_PARAM, pdfOutputOptions.getEmbedFonts())
				 								 .addStringVersion(LINEARIZED_PDF_PARAM, pdfOutputOptions.getLinearizedPDF())
				 								 .addStringVersion(LOCALE_PARAM, pdfOutputOptions.getLocale())
				 								 .addStringVersion(RETAIN_PDF_FORM_STATE_PARAM, pdfOutputOptions.getRetainPDFFormState())
				 								 .addStringVersion(RETAIN_UNSIGNED_SIGNATURE_FIELDS_PARAM, pdfOutputOptions.getRetainUnsignedSignatureFields())
				 								 .addStringVersion(TAGGED_PDF_PARAM, pdfOutputOptions.getTaggedPDF())
				 								 .addIfNotNull(XCI_PARAM, pdfOutputOptions.getXci(), ContentType.APPLICATION_XML)
				 								 .build()) {
			
			
			return payload.postToServer(ContentType.APPLICATION_PDF)
						  .map(RestServicesServiceAdapter::responseToDoc)
						  .orElseThrow();
		} catch (IOException e) {
			throw new OutputServiceException("I/O Error while generating PDF. (" + generatePdfOoutputRestClient.target() + ").", e);
		} catch (RestClientException e) {
			throw new OutputServiceException("Error while POSTing to server (" + generatePdfOoutputRestClient.target() + ").", e);
		}
	}


	@Override
	public BatchResult generatePDFOutputBatch(Map<String, String> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		throw new UnsupportedOperationException("generatePDFOutputBatch is not implemented yet.");
	}

	@Override
	public Document generatePrintedOutput(Document template, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		return internalGeneratePrintedOutput(template, null, data, printedOutputOptions);
	}

	@Override
	public Document generatePrintedOutput(String urlOrFileName, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		return internalGeneratePrintedOutput(null, urlOrFileName, data, printedOutputOptions);
	}

	@Override
	public BatchResult generatePrintedOutputBatch(Map<String, String> templates, Map<String, Document> data, PrintedOutputOptions printedOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		throw new UnsupportedOperationException("generatePrintedOutputBatch is not implemented yet.");
	}

	private Document internalGeneratePrintedOutput(Document template, String templateStr, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		if (template == null && templateStr == null) {
			throw new NullPointerException("template parameter cannot be null.");
		}
		Objects.requireNonNull(printedOutputOptions, "PrintedOutputOptions Argument cannot be null.");
		
		var restClient = generatePrintedOutputRestClient;
		try(MultipartPayload payload = restClient.multipartPayloadBuilder()
												 .addIfNotNull(DATA_PARAM, data, ContentType.APPLICATION_XML)
												 .addIfNotNull(TEMPLATE_PARAM, template, ContentType.APPLICATION_XDP)	// We're currently labeling everything as an XDP but we accept PDFs too.
												 .addIfNotNull(TEMPLATE_PARAM, templateStr)
				 								 .addStringVersionOfPath(CONTENT_ROOT_PARAM, printedOutputOptions.getContentRoot())
				 								 .addStringVersion(COPIES_PARAM, printedOutputOptions.getCopies())
				 								 .addStringVersion(DEBUG_DIR_PARAM, printedOutputOptions.getDebugDir())
				 								 .addStringVersion(LOCALE_PARAM, printedOutputOptions.getLocale())
				 								 .addStringVersion(PAGINATION_OVERRIDE_PARAM, printedOutputOptions.getPaginationOverride())
				 								 .addStringVersion(PRINT_CONFIG_PARAM, printedOutputOptions.getPrintConfig())
				 								 .addIfNotNull(XCI_PARAM, printedOutputOptions.getXci(), ContentType.APPLICATION_XML)
												 .build()
												 ) {


			return payload.postToServer(getAcceptType(printedOutputOptions.getPrintConfig().getRenderType()))
					  .map(RestServicesServiceAdapter::responseToDoc)
					  .orElseThrow(()->new OutputServiceException("Error - empty response from AEM server."));
		} catch (IOException e) {
			throw new OutputServiceException("I/O Error while generating print output. (" + restClient.target() + ").", e);
		} catch (RestClientException e) {
			throw new OutputServiceException("Error while POSTing to server", e);
		}
	}
	
	private ContentType getAcceptType(RenderType renderType) {
		return switch(renderType) {
			case DPL -> ContentType.APPLICATION_DPL;
			case IPL -> ContentType.APPLICATION_IPL;
			case PCL -> ContentType.APPLICATION_PCL;
			case PostScript -> ContentType.APPLICATION_PS;
			case TPCL -> ContentType.APPLICATION_TPCL;
			case ZPL -> ContentType.APPLICATION_ZPL;
		};
	}
	
	/**
	 * Creates a Builder object for building a RestServicesFormServiceAdapter object.
	 * 
	 * @return build object
	 */
	public static OutputServiceBuilder builder(RestClientFactory clientFactory) {
		return new OutputServiceBuilder(clientFactory);
	}

	public static class OutputServiceBuilder implements Builder {
		private final BuilderImpl builder;
		
		private OutputServiceBuilder(RestClientFactory clientFactory) {
			this.builder = new BuilderImpl(clientFactory);
		}

		@Override
		public OutputServiceBuilder machineName(String machineName) {
			builder.machineName(machineName);
			return this;
		}

		@Override
		public OutputServiceBuilder port(int port) {
			builder.port(port);
			return this;
		}

		@Override
		public OutputServiceBuilder useSsl(boolean useSsl) {
			builder.useSsl(useSsl);
			return this;
		}

		@Override
		public OutputServiceBuilder basicAuthentication(String username, String password) {
			builder.basicAuthentication(username, password);
			return this;
		}

		@Override
		public OutputServiceBuilder correlationId(Supplier<String> correlationIdFn) {
			builder.correlationId(correlationIdFn);
			return this;
		}

		@Override
		public Supplier<String> getCorrelationIdFn() {
			return builder.getCorrelationIdFn();
		}

		@Override
		public OutputServiceBuilder aemServerType(AemServerType serverType) {
			builder.aemServerType(serverType);
			return this;
		}
		
		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}

		public RestServicesOutputServiceAdapter build() {
			return new RestServicesOutputServiceAdapter(builder, this.getCorrelationIdFn());
		}
	}
}
