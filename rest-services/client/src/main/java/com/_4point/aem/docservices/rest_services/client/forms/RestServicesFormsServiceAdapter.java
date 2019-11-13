package com._4point.aem.docservices.rest_services.client.forms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
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
import com._4point.aem.fluentforms.api.AbsoluteOrRelativeUrl;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.RenderAtClient;

public class RestServicesFormsServiceAdapter extends RestServicesServiceAdapter implements TraditionalFormsService {

	private static final String PDF_PARAM = "pdf";
	private static final String DATA_PARAM = "data";
	private static final String TEMPLATE_PARAM = "template";
	private static final String ACROBAT_VERSION_PARAM = "renderOptions.acrobatVersion";
	private static final String CACHE_STRATEGY_PARAM = "renderOptions.cacheStrategy";
	private static final String CONTENT_ROOT_PARAM = "renderOptions.contentRoot";
	private static final String DEBUG_DIR_PARAM = "renderOptions.debugDir";
	private static final String LOCALE_PARAM = "renderOptions.locale";
	private static final String RENDER_AT_CLIENT_PARAM = "renderOptions.renderAtClient";
	private static final String SUBMIT_URL_PARAM = "renderOptions.submitUrl";
	private static final String TAGGED_PDF_PARAM = "renderOptions.taggedPdf";
	private static final String XCI_PARAM = "renderOptions.xci";
	
	
	private static final String IMPORT_DATA_PATH = "/services/FormsService/ImportData";
	private static final String RENDER_PDF_FORM_PATH = "/services/FormsService/RenderPdfForm";
	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	
	// Only callable from Builder
	private RestServicesFormsServiceAdapter(WebTarget target) {
		super(target);
	}

	// Only callable from Builder
	private RestServicesFormsServiceAdapter(WebTarget target, Supplier<String> correlationId) {
		super(target, correlationId);
	}

	@Override
	public Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException {
		throw new UnsupportedOperationException("exportData has not been implemented yet.");
	}

	@Override
	public Document importData(Document pdf, Document data) throws FormsServiceException {
		WebTarget importDataTarget = baseTarget.path(IMPORT_DATA_PATH);
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, data.getInputStream(), MediaType.APPLICATION_XML_TYPE)
					 .field(PDF_PARAM, pdf.getInputStream(), APPLICATION_PDF);

			Response result = postToServer(importDataTarget, multipart, APPLICATION_PDF);
			
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String message = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					message += "\n" + toString(entityStream);
				}
				throw new FormsServiceException(message);
			}
			
			if (!result.hasEntity()) {
				throw new FormsServiceException("Call to server succeeded but server failed to return document.  This should never happen.");
			}

			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			if ( responseContentType == null || !APPLICATION_PDF.isCompatible(MediaType.valueOf(responseContentType))) {
				String msg = "Response from AEM server was not a PDF.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + toString(entityStream);
				throw new FormsServiceException(msg);
			}

			Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
			resultDoc.setContentType(APPLICATION_PDF.toString());
			return resultDoc;
			
		} catch (IOException e) {
			throw new FormsServiceException("I/O Error while importing data. (" + baseTarget.getUri().toString() + ").", e);
		}
	}

	@Override
	public Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions) throws FormsServiceException {
		WebTarget renderPdfTarget = baseTarget.path(RENDER_PDF_FORM_PATH);
		
		AcrobatVersion acrobatVersion = pdfFormRenderOptions.getAcrobatVersion();
		CacheStrategy cacheStrategy = pdfFormRenderOptions.getCacheStrategy();
		PathOrUrl contentRoot = pdfFormRenderOptions.getContentRoot();
		Path debugDir = pdfFormRenderOptions.getDebugDir();
		Locale locale = pdfFormRenderOptions.getLocale();
		RenderAtClient renderAtClient = pdfFormRenderOptions.getRenderAtClient();
		List<AbsoluteOrRelativeUrl> submitUrls = pdfFormRenderOptions.getSubmitUrls();
		Boolean taggedPDF = pdfFormRenderOptions.getTaggedPDF();
		Document xci = pdfFormRenderOptions.getXci();
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			if (data != null) {
				multipart.field(DATA_PARAM, data.getInputStream(), MediaType.APPLICATION_XML_TYPE);
			}
			multipart.field(TEMPLATE_PARAM, urlOrfilename);
					 
			// This code sets the individual fields if they are not null. 
			MultipartTransformer.create(multipart)
								.transform((t)->acrobatVersion == null ? t : t.field(ACROBAT_VERSION_PARAM, acrobatVersion.toString()))
								.transform((t)->cacheStrategy == null  ? t : t.field(CACHE_STRATEGY_PARAM, cacheStrategy.toString()))
								.transform((t)->contentRoot == null ? t : t.field(CONTENT_ROOT_PARAM, contentRoot.toString()))
								.transform((t)->debugDir == null ? t : t.field(DEBUG_DIR_PARAM, debugDir.toString()))
								.transform((t)->locale == null ? t : t.field(LOCALE_PARAM, locale.toString()))
								.transform((t)->renderAtClient == null ? t : t.field(RENDER_AT_CLIENT_PARAM, renderAtClient.toString()))
								.transform((t)->taggedPDF == null ? t : t.field(TAGGED_PDF_PARAM, taggedPDF.toString()))
//								.transform((t)->submitUrls == null ? t : t.field(SUBMIT_URL_PARAM, submitUrls))
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
					message += "\n" + toString(entityStream);
				}
				throw new FormsServiceException(message);
			}
			if (!result.hasEntity()) {
				throw new FormsServiceException("Call to server succeeded but server failed to return document.  This should never happen.");
			}

			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			if ( responseContentType == null || !APPLICATION_PDF.isCompatible(MediaType.valueOf(responseContentType))) {
				String msg = "Response from AEM server was not a PDF.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + toString(entityStream);
				throw new FormsServiceException(msg);
			}

			Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
			resultDoc.setContentType(APPLICATION_PDF.toString());
			return resultDoc;
		} catch (IOException e) {
			throw new FormsServiceException("I/O Error while rendering PDF. (" + baseTarget.getUri().toString() + ").", e);
		}
		
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
	public static FormsServiceBuilder builder() {
		return new FormsServiceBuilder();
	}
	
	public static class FormsServiceBuilder implements Builder {
		private BuilderImpl builder = new BuilderImpl();
//		private final static Supplier<Client> defaultClientFactory = ()->ClientBuilder.newClient();
		
		private FormsServiceBuilder() {
			super();
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
		public FormsServiceBuilder clientFactory(Supplier<Client> clientFactory) {
			builder.clientFactory(clientFactory);
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
		public WebTarget createLocalTarget() {
			return builder.createLocalTarget();
		}

		public RestServicesFormsServiceAdapter build() {
			return new RestServicesFormsServiceAdapter(this.createLocalTarget(), this.getCorrelationIdFn());
		}
	}
	
	private static String toString(InputStream inputStream) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		return result.toString(StandardCharsets.UTF_8.name());
	}
}
