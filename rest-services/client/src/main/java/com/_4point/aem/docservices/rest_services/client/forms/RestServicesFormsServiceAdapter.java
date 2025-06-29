package com._4point.aem.docservices.rest_services.client.forms;

import java.io.IOException;
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
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com.adobe.fd.forms.api.DataFormat;

public class RestServicesFormsServiceAdapter extends RestServicesServiceAdapter implements TraditionalFormsService {

	private static final String FORMS_SERVICE_NAME = "FormsService";
	private static final String IMPORT_DATA_METHOD_NAME = "ImportData";
	private static final String RENDER_PDF_FORM_METHOD_NAME = "RenderPdfForm";
	private static final String EXPORT_DATA_METHOD_NAME="ExportData";
	private static final String PDF_PARAM = "pdf";
	private static final String DATA_PARAM = "data";
	private static final String TEMPLATE_PARAM = "template";
	private static final String ACROBAT_VERSION_PARAM = "renderOptions.acrobatVersion";
	private static final String CACHE_STRATEGY_PARAM = "renderOptions.cacheStrategy";
	private static final String CONTENT_ROOT_PARAM = "renderOptions.contentRoot";
	private static final String DEBUG_DIR_PARAM = "renderOptions.debugDir";
	private static final String EMBED_FONTS_PARAM = "renderOptions.embedFonts";
	private static final String LOCALE_PARAM = "renderOptions.locale";
	private static final String RENDER_AT_CLIENT_PARAM = "renderOptions.renderAtClient";
	private static final String SUBMIT_URL_PARAM = "renderOptions.submitUrl";
	private static final String TAGGED_PDF_PARAM = "renderOptions.taggedPdf";
	private static final String XCI_PARAM = "renderOptions.xci";
	private static final String DATA_FORMAT_PARAM = "dataformat";
	private static final String PDF_OR_XDP_PARAM = "pdforxdp";
	
	private final RestClient renderFormRestClient;
    private final RestClient importDataRestClient;
    private final RestClient exportDataRestClient;
    
	// Only callable from Builder
    private RestServicesFormsServiceAdapter(BuilderImpl builder, Supplier<String> correlationIdFn) {
        super(correlationIdFn);
        this.renderFormRestClient = builder.createClient(FORMS_SERVICE_NAME, RENDER_PDF_FORM_METHOD_NAME);
        this.importDataRestClient = builder.createClient(FORMS_SERVICE_NAME, IMPORT_DATA_METHOD_NAME);
        this.exportDataRestClient = builder.createClient(FORMS_SERVICE_NAME, EXPORT_DATA_METHOD_NAME);
    }

    @Override
    public Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException {
        Objects.requireNonNull(pdfOrXdp, "Document cannot be null.");
        Objects.requireNonNull(dataFormat, "Data format cannot be null.");

        try (MultipartPayload payload = exportDataRestClient.multipartPayloadBuilder()
                .add(PDF_OR_XDP_PARAM, pdfOrXdp, ContentType.APPLICATION_PDF)
                .addStringVersion(DATA_FORMAT_PARAM, dataFormat)
                .build()) {

            return payload.postToServer(ContentType.APPLICATION_XML)
            			  .map(RestServicesServiceAdapter::responseToDoc)
            			  .orElse(SimpleDocumentFactoryImpl.emptyDocument());	// If there was no response, return an empty document.
        } catch (IOException e) {
            throw new FormsServiceException("I/O Error while exporting data. (" + exportDataRestClient.target() + ").", e);
        } catch (RestClientException e) {
            throw new FormsServiceException("Error while POSTing to server (" + exportDataRestClient.target() + ").", e);
        }
    }

    @Override
    public Document importData(Document pdf, Document data) throws FormsServiceException {
        Objects.requireNonNull(pdf, "PDF document cannot be null.");
        Objects.requireNonNull(data, "Data document cannot be null.");

        try (MultipartPayload payload = importDataRestClient.multipartPayloadBuilder()
                .add(PDF_PARAM, pdf, ContentType.APPLICATION_PDF)
                .add(DATA_PARAM, data, ContentType.APPLICATION_XML)
                .build()) {

            return payload.postToServer(ContentType.APPLICATION_PDF)
                    .map(RestServicesServiceAdapter::responseToDoc)
                    .orElseThrow();
        } catch (IOException e) {
            throw new FormsServiceException("I/O Error while importing data. (" + importDataRestClient.target() + ").", e);
        } catch (RestClientException e) {
            throw new FormsServiceException("Error while POSTing to server (" + importDataRestClient.target() + ").", e);
        }
    }

    private Document internalRenderPDFForm(String urlOrfilename, Document template, Document data, PDFFormRenderOptions pdfFormRenderOptions) throws FormsServiceException {
    	Objects.requireNonNull(pdfFormRenderOptions, "PdfFormRenderOptions cannot be null.");

    	var xci = pdfFormRenderOptions.getXci();
        try (MultipartPayload payload = renderFormRestClient.multipartPayloadBuilder()
                .addIfNotNull(TEMPLATE_PARAM, urlOrfilename)							// Since this is internal, we know that one of these two will be null
                .addIfNotNull(TEMPLATE_PARAM, template, ContentType.APPLICATION_XDP)
                .addIfNotNull(DATA_PARAM, data, ContentType.APPLICATION_XML)
                .addStringVersion(ACROBAT_VERSION_PARAM, pdfFormRenderOptions.getAcrobatVersion())
                .addStringVersion(CACHE_STRATEGY_PARAM, pdfFormRenderOptions.getCacheStrategy())
                .addStringVersionOfPath(CONTENT_ROOT_PARAM, pdfFormRenderOptions.getContentRoot())
                .addStringVersion(DEBUG_DIR_PARAM, pdfFormRenderOptions.getDebugDir())
                .addStringVersion(EMBED_FONTS_PARAM, pdfFormRenderOptions.getEmbedFonts())
                .addStringVersion(LOCALE_PARAM, pdfFormRenderOptions.getLocale())
                .addStringVersion(RENDER_AT_CLIENT_PARAM, pdfFormRenderOptions.getRenderAtClient())
                .addStringVersion(SUBMIT_URL_PARAM, pdfFormRenderOptions.getSubmitUrls())
                .addStringVersion(TAGGED_PDF_PARAM, pdfFormRenderOptions.getTaggedPDF())
                .addIfNotNull(XCI_PARAM, xci, ContentType.APPLICATION_XML)
                .build()) {

            return payload.postToServer(ContentType.APPLICATION_PDF)
                    .map(RestServicesServiceAdapter::responseToDoc)
                    .orElseThrow();
        } catch (IOException e) {
            throw new FormsServiceException("I/O Error while rendering form. (" + renderFormRestClient.target() + ").", e);
        } catch (RestClientException e) {
            throw new FormsServiceException("Error while POSTing to server (" + renderFormRestClient.target() + ").", e);
        }
    }
    
	@Override
	public Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		return internalRenderPDFForm(Objects.requireNonNull(urlOrfilename, "Template string cannot be null."), null, data, pdfFormRenderOptions);
	}
	@Override
	public Document renderPDFForm(Document template, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		return internalRenderPDFForm(null, Objects.requireNonNull(template, "Template document cannot be null."), data, pdfFormRenderOptions);
	}

	@Override
	public ValidationResult validate(String template, Document data, ValidationOptions validationOptions)
			throws FormsServiceException {
		throw new UnsupportedOperationException("validate has not been implemented yet.");
	}

	/**
	 * Creates a Builder object for building a RestServicesFormServiceAdapter object.
	 * 
	 * @return build object
	 */
	public static FormsServiceBuilder builder(RestClientFactory clientFactory) {
		return new FormsServiceBuilder(clientFactory);
	}
	
	public static class FormsServiceBuilder implements Builder {
		private final BuilderImpl builder;
		
		private FormsServiceBuilder(RestClientFactory clientFactory) {
            this.builder = new BuilderImpl(clientFactory);
        }

		@Override
		public FormsServiceBuilder machineName(String machineName) {
			builder.machineName(machineName);
			return this;
		}

		@Override
		public FormsServiceBuilder port(int port) {
			builder.port(port);
			return this;
		}

		@Override
		public FormsServiceBuilder useSsl(boolean useSsl) {
			builder.useSsl(useSsl);
			return this;
		}

		@Override
		public FormsServiceBuilder basicAuthentication(String username, String password) {
			builder.basicAuthentication(username, password);
			return this;
		}
		
		@Override
		public FormsServiceBuilder correlationId(Supplier<String> correlationIdFn) {
			builder.correlationId(correlationIdFn);
			return this;
		}

		@Override
		public Supplier<String> getCorrelationIdFn() {
			return builder.getCorrelationIdFn();
		}

		@Override
		public FormsServiceBuilder aemServerType(AemServerType serverType) {
			builder.aemServerType(serverType);
			return this;
		}
		
		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}

		public RestServicesFormsServiceAdapter build() {
			return new RestServicesFormsServiceAdapter(builder, this.getCorrelationIdFn());
		}
	}
}