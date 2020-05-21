package com._4point.aem.docservices.rest_services.client.output;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.MultipartTransformer;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;
import com.adobe.fd.output.api.AcrobatVersion;

public class RestServicesOutputServiceAdapter extends RestServicesServiceAdapter implements TraditionalOutputService {

	private static final String GENERATE_PDF_OUTPUT_PATH = "/services/OutputService/GeneratePdfOutput";

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

	// Only callable from Builder
	private RestServicesOutputServiceAdapter(WebTarget baseTarget) {
		super(baseTarget);
	}

	// Only callable from Builder
	private RestServicesOutputServiceAdapter(WebTarget baseTarget, Supplier<String> correlationIdFn) {
		super(baseTarget, correlationIdFn);
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
		WebTarget renderPdfTarget = baseTarget.path(GENERATE_PDF_OUTPUT_PATH);

		if (template == null && templateStr == null) {
			throw new NullPointerException("template parameter cannot be null.");
		}
		Objects.requireNonNull(pdfOutputOptions, "PdfOutputOptions Argument cannot be null.");
		
		AcrobatVersion acrobatVersion = pdfOutputOptions.getAcrobatVersion();
		PathOrUrl contentRoot = pdfOutputOptions.getContentRoot();
		Path debugDir = pdfOutputOptions.getDebugDir();
		Boolean embedFonts = pdfOutputOptions.getEmbedFonts();
		Boolean linearizedPDF = pdfOutputOptions.getLinearizedPDF();
		Locale locale = pdfOutputOptions.getLocale();
		Boolean retainPDFFormState = pdfOutputOptions.getRetainPDFFormState();
		Boolean retainUnsignedSignatureFields = pdfOutputOptions.getRetainUnsignedSignatureFields();
		Boolean taggedPDF = pdfOutputOptions.getTaggedPDF();
		Document xci = pdfOutputOptions.getXci();
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			if (data != null) {
				multipart.field(DATA_PARAM, data.getInputStream(), MediaType.APPLICATION_XML_TYPE);
			}
			if (template != null) {
				multipart.field(TEMPLATE_PARAM, template.getInputStream(), APPLICATION_XDP);
			}
			if (templateStr != null) {
				multipart.field(TEMPLATE_PARAM, templateStr);
			}

			// This code sets the individual fields if they are not null. 
			MultipartTransformer.create(multipart)
								.transform((t)->acrobatVersion == null ? t : t.field(ACROBAT_VERSION_PARAM, acrobatVersion.toString()))
								.transform((t)->contentRoot == null ? t : t.field(CONTENT_ROOT_PARAM, contentRoot.toString()))
								.transform((t)->debugDir == null ? t : t.field(DEBUG_DIR_PARAM, debugDir.toString()))
								.transform((t)->embedFonts == null  ? t : t.field(EMBED_FONTS_PARAM, embedFonts.toString()))
								.transform((t)->linearizedPDF == null  ? t : t.field(LINEARIZED_PDF_PARAM, linearizedPDF.toString()))
								.transform((t)->locale == null ? t : t.field(LOCALE_PARAM, locale.toString()))
								.transform((t)->retainPDFFormState == null ? t : t.field(RETAIN_PDF_FORM_STATE_PARAM, retainPDFFormState.toString()))
								.transform((t)->retainUnsignedSignatureFields == null ? t : t.field(RETAIN_UNSIGNED_SIGNATURE_FIELDS_PARAM, retainUnsignedSignatureFields.toString()))
								.transform((t)->taggedPDF == null ? t : t.field(TAGGED_PDF_PARAM, taggedPDF.toString()))
								.transform((t)->{
									try {
										return xci == null ? t : t.field(XCI_PARAM, xci.getInlineData(), MediaType.APPLICATION_XML_TYPE);
									} catch (IOException e) {
										// if we encounter an exception, then we just don't add this field.  This should of error shouldn't ever happen.
										return t;
									}
								})
								;

			Response result = postToServer(renderPdfTarget, multipart, APPLICATION_PDF);
			
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String message = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					message += "\n" + inputStreamtoString(entityStream);
				}
				throw new OutputServiceException(message);
			}
			if (!result.hasEntity()) {
				throw new OutputServiceException("Call to server succeeded but server failed to return document.  This should never happen.");
			}

			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			if ( responseContentType == null || !APPLICATION_PDF.isCompatible(MediaType.valueOf(responseContentType))) {
				String msg = "Response from AEM server was not a PDF.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new OutputServiceException(msg);
			}

			Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
			resultDoc.setContentType(APPLICATION_PDF.toString());
			return resultDoc;
		} catch (IOException e) {
			throw new OutputServiceException("I/O Error while generating PDF. (" + baseTarget.getUri().toString() + ").", e);
		} catch (RestServicesServiceException e) {
			throw new OutputServiceException("Error while POSTing to server", e);
		}
	}


	@Override
	public BatchResult generatePDFOutputBatch(Map<String, String> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePrintedOutput(Document template, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePrintedOutput(String urlOrFileName, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchResult generatePrintedOutputBatch(Map<String, String> templates, Map<String, Document> data, PrintedOutputOptions printedOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Creates a Builder object for building a RestServicesFormServiceAdapter object.
	 * 
	 * @return build object
	 */
	public static OutputServiceBuilder builder() {
		return new OutputServiceBuilder();
	}

	public static class OutputServiceBuilder implements Builder {
		private BuilderImpl builder = new BuilderImpl();
		
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
		public OutputServiceBuilder clientFactory(Supplier<Client> clientFactory) {
			builder.clientFactory(clientFactory);
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
		public WebTarget createLocalTarget() {
			return builder.createLocalTarget();
		}
		
		public RestServicesOutputServiceAdapter build() {
			return new RestServicesOutputServiceAdapter(this.createLocalTarget(), this.getCorrelationIdFn());
		}
	}
}
