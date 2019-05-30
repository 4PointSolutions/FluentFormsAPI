package com._4point.aem.docservices.rest_services.client.forms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.Transformable;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

public class RestServicesFormsServiceAdapter implements TraditionalFormsService {

	private static final String PDF_PARAM = "pdf";
	private static final String DATA_PARAM = "data";
	private static final String TEMPLATE_PARAM = "template";
	private static final String ACROBAT_VERSION_PARAM = "renderOptions.acrobatVersion";
	private static final String CACHE_STRATEGY_PARAM = "renderOptions.cacheStrategy";
	private static final String CONTENT_ROOT_PARAM = "renderOptions.contentRoot";
	private static final String DEBUG_DIR_PARAM = "renderOptions.debugDir";
	private static final String LOCALE_PARAM = "renderOptions.locale";
	private static final String SUBMIT_URL_PARAM = "renderOptions.submitUrl";
	private static final String TAGGED_PDF_PARAM = "renderOptions.taggedPdf";
	private static final String XCI_PARAM = "renderOptions.xci";
	
	
	private static final String IMPORT_DATA_PATH = "/services/FormsService/ImportData";
	private static final String RENDER_PDF_FORM_PATH = "/services/FormsService/RenderPdfForm";
	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	
	private final WebTarget baseTarget;
	private final Supplier<String> correlationIdFn;

	// Only callable from Builder
	private RestServicesFormsServiceAdapter(WebTarget target) {
		super();
		this.baseTarget = target;
		this.correlationIdFn = null;
	}

	// Only callable from Builder
	private RestServicesFormsServiceAdapter(WebTarget target, Supplier<String> correlationId) {
		super();
		this.baseTarget = target;
		this.correlationIdFn = correlationId;
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

			Response result = postToServer(importDataTarget, multipart);
			
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
			
			return SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
			
		} catch (IOException e) {
			throw new FormsServiceException("I/O Error while importing data. (" + baseTarget.getUri().toString() + ").", e);
		}
	}

	@Override
	public Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions) throws FormsServiceException {
		WebTarget renderPdfTarget = baseTarget.path(RENDER_PDF_FORM_PATH);
		
		AcrobatVersion acrobatVersion = pdfFormRenderOptions.getAcrobatVersion();
		CacheStrategy cacheStrategy = pdfFormRenderOptions.getCacheStrategy();
		String contentRoot = pdfFormRenderOptions.getContentRoot();
		String debugDir = pdfFormRenderOptions.getDebugDir();
		String locale = pdfFormRenderOptions.getLocale();
		List<String> submitUrls = pdfFormRenderOptions.getSubmitUrls();
		boolean taggedPDF = pdfFormRenderOptions.getTaggedPDF();
		com.adobe.aemfd.docmanager.Document xci = pdfFormRenderOptions.getXci();
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, data.getInputStream(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, urlOrfilename)
					 .field(TAGGED_PDF_PARAM, Boolean.toString(taggedPDF))
					 ;
					 
			// This code sets the individual fields if they are not null. 
			MultipartTransformer.create(multipart)
								.transform((t)->acrobatVersion == null ? t : t.field(ACROBAT_VERSION_PARAM, acrobatVersion.toString()))
								.transform((t)->cacheStrategy == null  ? t : t.field(CACHE_STRATEGY_PARAM, cacheStrategy.toString()))
								.transform((t)->contentRoot == null ? t : t.field(CONTENT_ROOT_PARAM, contentRoot))
								.transform((t)->debugDir == null ? t : t.field(DEBUG_DIR_PARAM, debugDir))
								.transform((t)->locale == null ? t : t.field(LOCALE_PARAM, locale))
//								.transform((t)->submitUrls == null ? t : t.field(SUBMIT_URL_PARAM, submitUrls))
//								.transform((t)->xci == null ? t : t.field(XCI_PARAM, xci.getInputStream(), MediaType.APPLICATION_XML_TYPE))
								;

			Response result = postToServer(renderPdfTarget, multipart);
			
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
			
			return SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
		} catch (IOException e) {
			throw new FormsServiceException("I/O Error while rendering PDF. (" + baseTarget.getUri().toString() + ").", e);
		}
		
	}

	@Override
	public ValidationResult validate(String template, Document data, ValidationOptions validationOptions)
			throws FormsServiceException {
		throw new UnsupportedOperationException("validate has not been implemented yet.");
	}

	private Response postToServer(WebTarget localTarget, final FormDataMultiPart multipart) {
		javax.ws.rs.client.Invocation.Builder invokeBuilder = localTarget.request().accept(APPLICATION_PDF);
		if (this.correlationIdFn != null) {
			invokeBuilder.header(CORRELATION_ID_HTTP_HDR, this.correlationIdFn.get());
		}
		Response result = invokeBuilder.post(Entity.entity(multipart, multipart.getMediaType()));
		return result;
	}

	/**
	 * This class is to add a transform() method on the Multipart object so that we can make
	 * the "set if not null" operations more succinct. 
	 *
	 */
	private static class MultipartTransformer implements Transformable<MultipartTransformer> {
		private final FormDataMultiPart m;

		private MultipartTransformer(FormDataMultiPart m) {
			super();
			this.m = m;
		}
		
		public static MultipartTransformer create(FormDataMultiPart fdm) { return new MultipartTransformer(fdm); }

		public FormDataMultiPart get() {
			return m;
		}

		public MultipartTransformer field(String name, String value) {
			m.field(name, value);
			return this;
		}

		public MultipartTransformer field(String name, Object entity, MediaType mediaType) {
			m.field(name, entity, mediaType);
			return this;
		}
		
		
	}
	
	/**
	 * Creates a Builder object for building a RestServicesFormServiceAdapter object.
	 * 
	 * @return build object
	 */
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private final static Supplier<Client> defaultClientFactory = ()->ClientBuilder.newClient();
		
		private String machineName = "localhost";
		private int port = 4502;
		private HttpAuthenticationFeature authFeature = null;
		private boolean useSsl = false;
		private Supplier<Client> clientFactory = defaultClientFactory;
		private Supplier<String> correlationIdFn = null;
		
		// Only callable from the containing class.
		private Builder() {
			super();
		}

		public Builder machineName(String machineName) {
			this.machineName = machineName;
			return this;
		}

		public Builder port(int port) {
			this.port = port;
			return this;
		}

		public Builder useSsl(boolean useSsl) {
			this.useSsl = useSsl;
			return this;
		}

		public Builder clientFactory(Supplier<Client> clientFactory) {
			this.clientFactory = clientFactory;
			return this;
		}

		public Builder basicAuthentication(String username, String password) {
			this.authFeature = HttpAuthenticationFeature.basic(username, password);
			return this;
		}
		
		public Builder correlationId(Supplier<String> correlationIdFn) {
			this.correlationIdFn = correlationIdFn;
			return this;
		}

		public RestServicesFormsServiceAdapter build() {
			Client client = clientFactory.get();
			client.register(MultiPartFeature.class);
			if (this.authFeature != null) {
				client.register(authFeature);
			}
			WebTarget localTarget = client.target("http" + (useSsl ? "s" : "") + "://" + machineName + ":" + Integer.toString(port));
			return new RestServicesFormsServiceAdapter(localTarget, this.correlationIdFn);
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
