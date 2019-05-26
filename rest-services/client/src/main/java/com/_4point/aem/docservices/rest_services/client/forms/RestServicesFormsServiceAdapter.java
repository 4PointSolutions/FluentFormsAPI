package com._4point.aem.docservices.rest_services.client.forms;

import java.io.IOException;
import java.io.InputStream;
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
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

public class RestServicesFormsServiceAdapter implements TraditionalFormsService {

	private static final String PDF_PARAM_NAME = "pdf";
	private static final String DATA_PARAM_NAME = "data";
	private static final String IMPORT_DATA_PATH = "/services/FormsService/ImportData";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	
	private final WebTarget target;

	// Only callable from Builder
	private RestServicesFormsServiceAdapter(WebTarget target) {
		super();
		this.target = target;
	}

	@Override
	public Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document importData(Document pdf, Document data) throws FormsServiceException {
		WebTarget importDataTarget = target.path(IMPORT_DATA_PATH);
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM_NAME, data.getInputStream(), MediaType.APPLICATION_XML_TYPE)
					 .field(PDF_PARAM_NAME, pdf.getInputStream(), APPLICATION_PDF);

			Response result = importDataTarget.request()
								    .accept(APPLICATION_PDF)
								    .post(Entity.entity(multipart, multipart.getMediaType()));
			
			// TODO: Check the result to make sure everything is OK.
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String message = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					//  TODO: Transfer the entity into the message.
				}
				throw new FormsServiceException(message);
			}
			
			if (!result.hasEntity()) {
				throw new FormsServiceException("Call to server succeeded but server failed to return document.  This should never happen.");
			}
			
			return SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
			
		} catch (IOException e) {
			throw new FormsServiceException("I/O Error while importing data. (" + target.getUri().toString() + ").", e);
		}
	}

	@Override
	public Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValidationResult validate(String template, Document data, ValidationOptions validationOptions)
			throws FormsServiceException {
		// TODO Auto-generated method stub
		return null;
	}

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
		
		public RestServicesFormsServiceAdapter build() {
			Client client = clientFactory.get();
			client.register(MultiPartFeature.class);
			if (this.authFeature != null) {
				client.register(authFeature);
			}
			WebTarget localTarget = client.target("http" + (useSsl ? "s" : "") + "://" + machineName + ":" + Integer.toString(port));
			return new RestServicesFormsServiceAdapter(localTarget);
		}
	}
}
