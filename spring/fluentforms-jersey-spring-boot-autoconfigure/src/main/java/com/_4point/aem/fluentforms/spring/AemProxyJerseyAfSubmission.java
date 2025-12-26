package com._4point.aem.fluentforms.spring;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import org.glassfish.jersey.client.ChunkedInput;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.util.MultiValueMapAdapter;

import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmissionHandler;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
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
 * invoked in order for each Adaptive Form submission.
 * 
 * If no AfSubmitHandler beans are available, then all Adaptive Form submissions will be forwarded on
 * to the configured AEM instance.
 * 
 * 
 * 
 */
@Path("/aem")
public class AemProxyJerseyAfSubmission {
	private final static Logger logger = LoggerFactory.getLogger(AemProxyJerseyAfSubmission.class);
	private static final String CONTENT_FORMS_AF = "content/forms/af/";
	
	@Autowired
	JerseyAfSubmitProcessor submitProcessor;

	@Path(CONTENT_FORMS_AF + "{remainder : .+}")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.WILDCARD)
    public Response proxySubmitPost(@PathParam("remainder") String remainder, /* @HeaderParam(CorrelationId.CORRELATION_ID_HDR) final String correlationIdHdr,*/ @Context HttpHeaders headers, final FormDataMultiPart inFormData)  {
		logger.atInfo().addArgument(()->submitProcessor != null ?  submitProcessor.getClass().getName() : "null" ).log("Submit proxy called. SubmitProcessor={}");
//		final String correlationId = CorrelationId.generate(correlationIdHdr);
//		ProcessingMetadataBuilder pmBuilder = ProcessingMetadata.start(correlationId);
		return submitProcessor.processRequest(inFormData, headers, remainder);
	}
	
	/**
	 * Transforms a FormDataMultiPart object using a set of provided functions.
	 * 
	 * Accepts incoming form data, in the form of a FormDataMultiPart object and a Map collection of functions.  It walks through the
	 * parts and if it finds a function in the Map with the same name it executes that function on the the data from the corresponding part.
	 * It accumulates and returns the result in another FormDataMultiPart object.
	 * 
	 * @param inFormData	incoming form data
	 * @param fieldFunctions	set of functions that correspond to specific parts
	 * @param logger	logger for logging messages
	 * @return
	 * @throws IOException
	 */
	private static FormDataMultiPart transformFormData(final FormDataMultiPart inFormData, final Map<String, Function<byte[], byte[]>> fieldFunctions, Logger logger) {
		try {
			FormDataMultiPart outFormData = new FormDataMultiPart();
			var fields = inFormData.getFields();
			logger.atDebug().log(()->"Found " + fields.size()  + " fields");
			
			for (var fieldEntry : fields.entrySet()) {
				String fieldName = fieldEntry.getKey();
				for (FormDataBodyPart fieldData : fieldEntry.getValue()) {
					logger.atDebug().log(()->"Copying '" + fieldName  + "' field");
					byte[] fieldBytes = ((BodyPartEntity)fieldData.getEntity()).getInputStream().readAllBytes();
					logger.atTrace().log(()->"Fieldname '" + fieldName + "' is '" + new String(fieldBytes) + "'.");
					var fieldFn = fieldFunctions.getOrDefault(fieldName, Function.identity());	// Look for an entry in fieldFunctions table for this field.  Return the Identity function if we don't find one.
					byte[] modifiedFieldBytes = fieldFn.apply(fieldBytes);
					if (modifiedFieldBytes != null) {	// If the function returned bytes (if not, then remove that part)
						outFormData.field(fieldName, new String(modifiedFieldBytes, StandardCharsets.UTF_8));	// Apply the field function to bytes.
					}
				}
			}
			return outFormData;
		} catch (IOException e) {
			throw new InternalServerErrorException("Error while transforming submission data.", e);
		}
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
	public interface JerseyAfSubmitProcessor {
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
	
	@FunctionalInterface
	public interface AfFormDataTransformer {
		/**
		 * If one or more of these are available in the Spring context, they will be run against the incoming
		 * data before it is processed.
		 * 
		 * This can be useful when used with the AfSubmitAemProxyProcessor to transform the data before it 
		 * is sent to AEM.
		 * 
		 * This can be useful when used with the AfSubmitLocalProcessor to capture data from the initial
		 * Adaptive Form submission that may not normally be passed to the AfSubmitHandler.
		 * 
		 * @param inFormData
		 * 		incoming form data object
		 * @return
		 * 		outgoing form data object
		 */
		FormDataMultiPart transformFormData(final FormDataMultiPart inFormData);
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
	static class AfSubmitAemProxyProcessor implements JerseyAfSubmitProcessor {

		private final AemConfiguration aemConfig;
		private final Client httpClient;
		
		public AfSubmitAemProxyProcessor(AemConfiguration aemConfig, SslBundles sslBundles) {
			this.aemConfig = aemConfig;
	    	this.httpClient = JerseyClientFactory.createClient(sslBundles, aemConfig.sslBundle(), aemConfig.user(), aemConfig.password());
		}

		@Override
		public Response processRequest(FormDataMultiPart formSubmission, HttpHeaders headers, String remainder) {
	    	logger.atTrace().addArgument(()->{	String formData = formSubmission.getField("jcr:data").getEntityAs(String.class); 
	    										return formData != null ? formData : "null"; 
	    									  })
	    					.log("AF Submit Proxy: Data = '{}'");
			
	    	// Transfer to AEM
	    	String contentType = headers.getMediaType().toString();
	    	String cookie = headers.getHeaderString("cookie");
			WebTarget webTarget = httpClient.target(aemConfig.url())
									.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
									.path("/" + CONTENT_FORMS_AF + remainder);

			logger.atDebug().log(()->"Proxying Submit POST request for target '" + webTarget.getUri().toString() + "'.");
			Response result = webTarget.request()
									   .header("cookie", cookie)
									   .post(Entity.entity(formSubmission , contentType));

			logger.atDebug().log(()->"AEM Response = " + result.getStatus());
			logger.atDebug().log(()->"AEM Response Location = " + result.getLocation());

			String aemResponseEncoding = result.getHeaderString("Transfer-Encoding");
			if (aemResponseEncoding != null && aemResponseEncoding.equalsIgnoreCase("chunked")) {
				logger.atDebug().log("Returning chunked response from AEM.");
				return Response.status(result.getStatus()).entity(new ByteArrayInputStream(transferFromAem(result, logger)))
							   .type(result.getMediaType())
//							   .header(CorrelationId.CORRELATION_ID_HDR, correlationId)
							   .build();
			} else  {
				logger.atDebug().log("Returning response from AEM.");
				return Response.fromResponse(result)
//							   .header(CorrelationId.CORRELATION_ID_HDR, correlationId)
							   .build();
			}
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
	 * TODO:  Add configuration variable that becomes enum value for FIRST and ALL.  FIRST = quit after first handler that canHandle
	 *        ALL - process all handlers that canHandle a request.
	 * 
	 */
	static class AfSubmitLocalProcessor implements JerseyAfSubmitProcessor {
		private final static Logger logger = LoggerFactory.getLogger(AfSubmitLocalProcessor.class);
		private static final String REMAINDER_PATH_SUFFIX = "/jcr:content/guideContainer.af.submit.jsp";
		
		// Have to implement an internal interface so that Spring does not think there are two available
		// AfSubmitProcessors.  This wraps an internal AfSubmitAemProxyProcessor that the local processor
		// uses to handle requests it chooses to pass on to AEM.
		@FunctionalInterface
		public interface InternalAfSubmitAemProxyProcessor {
			AfSubmitAemProxyProcessor get();
		}

		private final List<AfSubmissionHandler> submissionHandlers;
		private final AfSubmitAemProxyProcessor aemProxyProcessor;

		AfSubmitLocalProcessor(List<AfSubmissionHandler> submissionHandlers, InternalAfSubmitAemProxyProcessor aemProxyProcessor) {
			this.submissionHandlers = submissionHandlers;
			this.aemProxyProcessor = aemProxyProcessor.get();
			logger.atInfo().addArgument(submissionHandlers.size()).log("Found {} available AfSubmissionHandlers.");
			if(logger.isDebugEnabled()) {
				submissionHandlers.forEach(sh->logger.atDebug().addArgument(sh.getClass().getName()).log("  Found AfSubmissionHandler named '{}'."));
			}
		}

		@Override
		public Response processRequest(FormDataMultiPart inFormData, HttpHeaders headers, String remainder) {
			if (!remainder.endsWith(REMAINDER_PATH_SUFFIX)) {
				// If the submission does not end with the expected submission suffix, then just proxy it AEM.
				return aemProxyProcessor.processRequest(inFormData, headers, remainder);
			}
			String formName = determineFormName(remainder);
			Optional<AfSubmissionHandler> firstHandler = submissionHandlers.stream()
																		   .filter(sh->canHandle(sh, formName))
																		   .findFirst();
			
			return firstHandler.map(h->processSubmission(h, inFormData, headers, formName))
							   .orElseGet(()->errorResponse());
		}

		private Response processSubmission(AfSubmissionHandler handler, FormDataMultiPart inFormData, HttpHeaders headers, String formName) {
			logger.atInfo().addArgument(handler.getClass().getName()).log("Calling AfSubmissionHandler={}");
			return formulateResponse(requireNonNull(handler.processSubmission(formulateSubmission(inFormData, headers, formName)), "SubmissionHandler code cannot return null"));
		}
		
		private String determineFormName(String guideContainerPath) {
			return guideContainerPath.substring(0, guideContainerPath.length() - REMAINDER_PATH_SUFFIX.length());
		}
		
		private boolean canHandle(AfSubmissionHandler sh, String formName) {
			boolean result = sh.canHandle(formName);
			logger.atDebug().addArgument(formName).addArgument(()->sh.getClass().getName()).log("Submission Handler canHandle returned {}. ({})");
			return result;
		}
		
		// Create a AfSubmissionHandler.Submission object from the JAX-RS Request classes.
		private AfSubmissionHandler.Submission formulateSubmission(FormDataMultiPart inFormData, HttpHeaders headers, String formName) {
			class ExtractedData {
				String formData;
				String redirectUrl;
			};
			final ExtractedData extractedData = new ExtractedData();
			// Extract data some of the parts.
			final Map<String, Function<byte[], byte[]>> fieldFunctions = 		// Create a table of functions that will be called to transform specific fields in the incoming AF submission.
	        		Map.of(
	        				":redirect",	(redirect)->{ extractedData.redirectUrl = new String(redirect, StandardCharsets.UTF_8); return null; },
	        				"jcr:data",		(dataBytes)->{ extractedData.formData = new String(dataBytes, StandardCharsets.UTF_8); return null; }
	        			);
			transformFormData(inFormData, fieldFunctions, logger);
			return new AfSubmissionHandler.Submission(requireNonNull(extractedData.formData, "Form data (jcr:data) part not found in Adaptive Form submission."), 
													  formName, 
													  requireNonNull(extractedData.redirectUrl, "Redirect URL (:redirect) part not found in Adaptive Form submission."), 
													  transferHeaders(headers)
													  );
		}
		
		// Transfer headers from JAX-RS construct to Spring construct (in order to keep JAX-RS encapsulated in this class)
		private MultiValueMapAdapter<String, String> transferHeaders(HttpHeaders headers) {
			if (logger.isDebugEnabled()) {
				headers.getRequestHeaders().forEach((k,v)->logger.atDebug().addArgument(k).addArgument(v.size()).log("Found Http header {} with {} values."));
			}
			return new MultiValueMapAdapter<String, String>(headers.getRequestHeaders());
		}
		
		// Convert the SubmitResponse object into a JAX-RS Response object.  
		private Response formulateResponse(AfSubmissionHandler.SubmitResponse submitResponse) {
			if (submitResponse instanceof AfSubmissionHandler.SubmitResponse.Response response) {
				var builder = response.responseBytes().length > 0 ? Response.ok().entity(response.responseBytes()).type(response.mediaType()) 
																  :	Response.noContent();
				return builder.build();
			} else if (submitResponse instanceof AfSubmissionHandler.SubmitResponse.SeeOther redirectFound) {
				return Response.seeOther(redirectFound.redirectUrl()).build();
			} else if (submitResponse instanceof AfSubmissionHandler.SubmitResponse.Redirect redirect) {
				return Response.temporaryRedirect(redirect.redirectUrl()).build();
			} else {
				// This cannot happen, but we need to supply an else until we can turn this code into a switch
				// expression in JDK 21.
				throw new IllegalStateException("Unexpected SubmitResponse class type '%s', this should never happen!".formatted(submitResponse.getClass().getName()));
			}
		}
		
		// Generate an JAX-RS Error response if not AfSubmissionHandler was found.
		private Response errorResponse() {
			logger.atWarn().log("No applicable AfSubmissionHandler found.");
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}