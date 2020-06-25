package com._4point.aem.docservices.rest_services.client.assembler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.assembler.AssemblerResultImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com.adobe.fd.assembler.client.OperationException;

public class RestServicesDocAseemblerServiceAdapter extends RestServicesServiceAdapter implements TraditionalDocAssemblerService {

	private static final String ASSEMBLE_DOCUMENT_PATH = "/services/AssemblerService/AssembleDocuments";
	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";
	private static final String IS_FAIL_ON_ERROR = "isFailOnError";
	// Only callable from Builder
	private RestServicesDocAseemblerServiceAdapter(WebTarget target) {
		super(target);
	}

	// Only callable from Builder
	private RestServicesDocAseemblerServiceAdapter(WebTarget target, Supplier<String> correlationId) {
		super(target, correlationId);
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException {
		WebTarget assembleDocTarget = baseTarget.path(ASSEMBLE_DOCUMENT_PATH);
		Boolean isFailOnError = adobAssemblerOptionSpec.isFailOnError();
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			
			if (ddx != null) {
				multipart.field(TEMPLATE_PARAM, ddx.getInputStream(), APPLICATION_PDF);
			} else {
				throw new NullPointerException("ddx can not be null");
			}
			
			if (inputs != null) {
				multipart.field(DATA_PARAM, inputs, MediaType.MULTIPART_FORM_DATA_TYPE);
			} else {
				throw new NullPointerException("inputs can not be null");
			}
			
			if(adobAssemblerOptionSpec != null) {
		        MultipartTransformer.create(multipart).transform((t) -> isFailOnError == null?t : t.field(IS_FAIL_ON_ERROR, isFailOnError.toString()));
			}
            Response result = postToServer(assembleDocTarget, multipart, APPLICATION_PDF);
			
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String msg = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					msg += "\n" + inputStreamtoString(entityStream);
				}
				throw new  AssemblerServiceException(msg);
			}
			
			if (!result.hasEntity()) {
				throw new AssemblerServiceException("Call to server succeeded but server failed to return document.  This should never happen.");
			}
			
			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			if ( responseContentType == null || !APPLICATION_PDF.isCompatible(MediaType.valueOf(responseContentType))) {
				String msg = "Response from AEM server was not a PDF.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new AssemblerServiceException(msg);
			}
			
			Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
			resultDoc.setContentType(APPLICATION_PDF.toString());
			
			Map<String, Document> getDocuments = new HashMap<String, Document>();
			getDocuments.put("concatenatedPDF.pdf", resultDoc);
			AssemblerResult assemblerResult = new AssemblerResultImpl(getDocuments);
			return assemblerResult;
		
		}catch (IOException e) {
			throw new AssemblerServiceException("I/O Error while reader merging document. (" + baseTarget.getUri().toString() + ").", e);
		} catch (RestServicesServiceException e) {
			throw new AssemblerServiceException("Error while POSTing to server", e);
		}
		
	}

	public static AssemblerServiceBuilder builder() {
		return new AssemblerServiceBuilder();
	}
    
	
	public static class AssemblerServiceBuilder implements Builder {
		private BuilderImpl builder = new BuilderImpl();
		
		@Override
		public AssemblerServiceBuilder machineName(String machineName) {
			builder.machineName(machineName);
			return this;
		}

		@Override
		public AssemblerServiceBuilder port(int port) {
			builder.port(port);
			return this;
		}

		@Override
		public AssemblerServiceBuilder useSsl(boolean useSsl) {
			builder.useSsl(useSsl);
			return this;
		}

		@Override
		public AssemblerServiceBuilder clientFactory(Supplier<Client> clientFactory) {
			builder.clientFactory(clientFactory);
			return this;
		}

		@Override
		public AssemblerServiceBuilder basicAuthentication(String username, String password) {
			builder.basicAuthentication(username, password);
			return this;
		}

		@Override
		public AssemblerServiceBuilder correlationId(Supplier<String> correlationIdFn) {
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
		
		public RestServicesDocAseemblerServiceAdapter build() {
			return new RestServicesDocAseemblerServiceAdapter(this.createLocalTarget(), this.getCorrelationIdFn());
		}
	}


	
}
