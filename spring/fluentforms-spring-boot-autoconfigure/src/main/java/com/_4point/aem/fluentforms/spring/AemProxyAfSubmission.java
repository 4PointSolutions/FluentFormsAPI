package com._4point.aem.fluentforms.spring;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;

import org.glassfish.jersey.client.ChunkedInput;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

/**
 * Class that handles Adaptive Form Submissions.
 * 
 * This class sets up an endpoint that receives all Adaptive Forms submissions.  The processing of these
 * submissions can be configured based on the available beans available within the Spring context.
 * 
 * If a bean is provided that implements the AfSubmitProcessor interface, then that bean will be called
 * for every Adaptive Form submission.
 * 
 * In the absence of an AfSubmitProcessor bean, then if one or more AfSubmitHandler beans are available, these will
 * invoked in order for each Adapitive Form submission.
 * 
 * If no AfSubmitHandler beans are available, then all Adaptive Form submissions will be forwarded on
 * to the configured AEM instance.
 * 
 */
@Path("/aem")
public class AemProxyAfSubmission {
	private final static Logger logger = LoggerFactory.getLogger(AemProxyAfSubmission.class);
	private static final String CONTENT_FORMS_AF = "content/forms/af/";
	
	@Autowired
	AfSubmitProcessor submitProcessor;

	@Path(CONTENT_FORMS_AF + "{remainder : .+}")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response proxySubmitPost(@PathParam("remainder") String remainder, /* @HeaderParam(CorrelationId.CORRELATION_ID_HDR) final String correlationIdHdr,*/ @Context HttpHeaders headers, final FormDataMultiPart inFormData)  {
		logger.atInfo().addArgument(()->submitProcessor != null ?  submitProcessor.getClass().getName() : "null" ).log("Submit proxy called. SubmitProcessor={}");
//		final String correlationId = CorrelationId.generate(correlationIdHdr);
//		ProcessingMetadataBuilder pmBuilder = ProcessingMetadata.start(correlationId);
		return submitProcessor.processRequest(inFormData, headers, remainder);
	}
	
	/**
	 * Interface that classes that want to perform low-level processing of all Adaptive Forms submissions.
	 * 
	 * All Adaptive Form submissions will pass through an AfSubmitProcessor singleton found within the Spring
	 * context.  Normally, this will be one of the provided AfSubmitProcessors (like AfSubmitLocalProcessor or
	 * AfSubmitAemProxyProcessor), but can be replaced by a user supplied implementation.
	 * 
	 */
	@FunctionalInterface
	public interface AfSubmitProcessor {
		/**
		 * Processor to process incoming Adaptive Forms submit.
		 * 
		 * @param inFormData
		 * 		incoming form data
		 * @param headers
		 * 		incoming HTTP headers
		 * @param remainder
		 * 		Adaptive Forms location path (relative to /content/forms/af/)
		 * @return
		 */
		Response processRequest(final FormDataMultiPart inFormData, HttpHeaders headers, String remainder);
	}
	
	/**
	 * This processor forwards the Adaptive Form submissions on to AEM for processing by the AEM instance.
	 * 
	 * This is typically used if the AEM Forms Data model will be used for processing the submission.
	 * 
	 * This is the default submit processor if no other type of submit processing is configured in the
	 * Spring context.  
	 * 
	 */
	public static class AfSubmitAemProxyProcessor implements AfSubmitProcessor {

		private final AemConfiguration aemConfig;
		private final Client httpClient;
		
		public AfSubmitAemProxyProcessor(AemConfiguration aemConfig) {
			this.aemConfig = aemConfig;
	    	this.httpClient = ClientBuilder.newClient().register(HttpAuthenticationFeature.basic(aemConfig.user(), aemConfig.password())).register(MultiPartFeature.class);
		}

		@Override
		public Response processRequest(FormDataMultiPart formSubmission, HttpHeaders headers, String remainder) {
//			// TODO: Convert this to a fluent logger call using a lambda.
//			String formData = formSubmission.getField("jcr:data").getEntityAs(String.class);
//	    	logger.trace("AF Submit Proxy: Data = '{}'", formData != null ? formData : "null");
//			
//	    	// Transfer to AEM
//	    	String contentType = headers.getMediaType().toString();
//	    	String cookie = headers.getHeaderString("cookie");
//			WebTarget webTarget = httpClient.target(aemConfig.url())
//									.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
//									.path("/" + CONTENT_FORMS_AF + remainder);
//
//			logger.atDebug().log(()->"Proxying Submit POST request for target '" + webTarget.getUri().toString() + "'.");
//			Response result = webTarget.request()
//				.header("cookie", cookie)
//				.post(Entity.entity(formSubmission , contentType));
//
//			logger.atDebug().log(()->"AEM Response = " + result.getStatus());
//			logger.atDebug().log(()->"AEM Response Location = " + result.getLocation());
//
//			String aemResponseEncoding = result.getHeaderString("Transfer-Encoding");
//			if (aemResponseEncoding != null && aemResponseEncoding.equalsIgnoreCase("chunked")) {
//				logger.debug("Returning chunked response from AEM.");
//				return Response.status(result.getStatus()).entity(new ByteArrayInputStream(transferFromAem(result, logger)))
//							   .type(result.getMediaType())
////							   .header(CorrelationId.CORRELATION_ID_HDR, correlationId)
//							   .build();
//			} else  {
//				logger.debug("Returning response from AEM.");
//				return Response.fromResponse(result)
////							   .header(CorrelationId.CORRELATION_ID_HDR, correlationId)
//							   .build();
//			}
			return Response.ok().entity("AfSubmitAemProxyProcessor Response").build();
		}
		
		/**
		 * Transfers a response from AEM and returns it in a byte array.  It handles chunked responses.
		 * 
		 * @param result	Response object from AEM
		 * @param logger	Logger for logging any errors/warnings/etc.
		 * @return
		 * @throws IOException
		 */
		private static byte[] transferFromAem(Response result, Logger logger) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("AEM Response Mediatype=" + (result.getMediaType() != null ? result.getMediaType().toString(): "null"));
					MultivaluedMap<String, Object> headers = result.getHeaders();
					for(Entry<String, List<Object>> entry : headers.entrySet()) {
						String msgLine = "For header '" + entry.getKey() + "', ";
						for (Object value : entry.getValue()) { 
							msgLine += "'" + value.toString() + "' ";
						}
						logger.debug(msgLine);
					}
				}
				
				String aemResponseEncoding = result.getHeaderString("Transfer-Encoding");
				if (aemResponseEncoding != null && aemResponseEncoding.equalsIgnoreCase("chunked")) {
					// They've sent back chunked response.
					logger.debug("Found a chunked encoding.");
					final ChunkedInput<byte[]> chunkedInput = result.readEntity(new GenericType<ChunkedInput<byte[]>>() {});
					byte[] chunk;
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					try (buffer) {
						while ((chunk = chunkedInput.read()) != null) {
							buffer.writeBytes(chunk);
							logger.debug("Read chunk from AEM response.");
						}
					}
					
					return buffer.toByteArray();
				} else {
					return ((InputStream)result.getEntity()).readAllBytes();
				}
			} catch (IllegalStateException | IOException e) {
				throw new InternalServerErrorException("Error while processing transferring result from AEM.", e);
			}
		}
	    

	}

	/**
	 * This processor will process Adaptive Forms submissions locally without sending anything to AEM.
	 * 
	 * It will invoke one or more AfSubmitHandlers that have been configured in the Spring context.
	 * 
	 */
	public static class AfSubmitLocalProcessor implements AfSubmitProcessor {

		@Override
		public Response processRequest(FormDataMultiPart inFormData, HttpHeaders headers, String remainder) {
			return Response.ok().entity("AfSubmitLocalProcessor Response").build();
		}
		
	}
}
