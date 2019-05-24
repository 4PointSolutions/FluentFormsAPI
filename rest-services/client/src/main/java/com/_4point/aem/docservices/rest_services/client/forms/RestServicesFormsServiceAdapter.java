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

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

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
	
	private final static Supplier<Client> defaultClientFactory = ()->ClientBuilder.newClient();
	
	private final WebTarget target;

	public RestServicesFormsServiceAdapter(String machineName, int port, boolean useSsl) {
		super();
		Client client = defaultClientFactory.get();
		target = initTarget(machineName, port, useSsl, client);
	}

	/*
	 *  For unit testing purposes only.
	 */
	protected RestServicesFormsServiceAdapter(String machineName, int port, boolean useSsl, Supplier<Client> clientFactory) {
		super();
		Client client = clientFactory.get();
		target = initTarget(machineName, port, useSsl, client);
	}

	@Override
	public Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document importData(Document pdf, Document data) throws FormsServiceException {
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM_NAME, data.getInputStream(), MediaType.APPLICATION_XML_TYPE)
					 .field(PDF_PARAM_NAME, pdf.getInputStream(), APPLICATION_PDF);

			Response result = target.request()
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

	private static WebTarget initTarget(String machineName, int port, boolean useSsl, Client client) {
		return client.target("http" + (useSsl ? "s" : "") + "://" + machineName + ":" + Integer.toString(port)).path(IMPORT_DATA_PATH);
	}

}
