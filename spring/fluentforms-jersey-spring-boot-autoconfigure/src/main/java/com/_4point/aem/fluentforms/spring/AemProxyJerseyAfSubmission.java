package com._4point.aem.fluentforms.spring;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.glassfish.jersey.client.ChunkedInput;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

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
	AfSubmitProcessor submitProcessor;

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
	static class AfSubmitAemProxyProcessor implements AfSubmitProcessor {

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
	 * Implement this interface in order to provide code that will handle an Adaptive Form submission
	 * 
	 */
	public interface AfSubmissionHandler {
		/**
		 * Object that contains the data submitted by the Adaptive Form
		 */
		public record Submission(String formData, String formName, String redirectUrl, MultiValueMap<String, String> headers) {};
		
		/**
		 * Interface that is a tagging interface for the different types of response.
		 */
		public sealed interface SubmitResponse permits SubmitResponse.Response, SubmitResponse.SeeOther, SubmitResponse.Redirect {
			/**
			 * A Normal response with a 200 HTTP status code (204 if the responseBytes variable is empty)
			 */
			public record Response(byte[] responseBytes, String mediaType) implements SubmitResponse {
				/**
				 * Creates a text response from a String
				 * 
				 * @param text
				 * 		Text to go into the response.
				 * @return
				 * 		Response object with a media type of "text/plain"
				 */
				public static Response text(String text) { return new Response(text.getBytes(StandardCharsets.UTF_8), MediaType.TEXT_PLAIN); }
				/**
				 * Creates an HTML response from a String
				 * 
				 * @param html
				 * 		String containing HTML.  No checking is done to ensure that this is valid HTML.
				 * @return
				 * 		Response object with a media type of "text/html"
				 */
				public static Response html(String html) { return new Response(html.getBytes(StandardCharsets.UTF_8), MediaType.TEXT_HTML); }
				/**
				 * Creates an JSON response from a String
				 * 
				 * @param json
				 * 		String containing JSON.  No checking is done to ensure that this is valid JSON.
				 * @return
				 * 		Response object with a media type of "application/html"
				 */
				public static Response json(String json) { return new Response(json.getBytes(StandardCharsets.UTF_8), MediaType.APPLICATION_JSON); }
				/**
				 * Creates an XML response from a String
				 * 
				 * @param xml
				 * 		String containing XML.  No checking is done to ensure that this is valid XML.
				 * @return
				 * 		Response object with a media type of "application/xml"
				 */
				public static Response xml(String xml) { return new Response(xml.getBytes(StandardCharsets.UTF_8), MediaType.APPLICATION_XML); }
			};
			/**
			 * A Temporary Redirect (302 HTTP status code) response
			 */
			public record SeeOther(URI redirectUrl) implements SubmitResponse {};
			/**
			 * A Temporary Redirect (307 HTTP status code) response
			 */
			public record Redirect(URI redirectUrl) implements SubmitResponse {};
		}
		/**
		 * Called to determine if this handler can handle a submission from this form.
		 * 
		 * The first handler that can handle a form submission (i.e. for which canHandle() returns true) will be selected.
		 * 
		 * @param formName
		 * 		Adaptive Form name (full path under formsanddocuments)
		 * @return
		 * 		true indicates the this handler can handle the submission, false indicates that it cannot
		 */
		boolean canHandle(String formName);
		
		/**
		 * Called to process the submission
		 * 
		 * The incoming submission is parsed into a Submission object and then the first handler that indicates
		 * that is can handle the submission will have its processSubmission method called.
		 * 
		 * @param submission
		 * 		Submission object containing the form submission data
		 * @return
		 * 		a SubmitResponse object that will be turned into an HTTP response to the submission.
		 */
		SubmitResponse processSubmission(Submission submission);
		
		/**
		 * Creates an AfSubmissionHandler that handles form submissions from a specific form
		 * 
		 * @param formName
		 * 		name of the form (so the form name under FormsAndDocuments)
		 * @param handlerLogic
		 * 		function that will be called when a form submission for occurs from that form
		 * @return
		 * 		an AfSubmissionHandler object
		 */
		public static AfSubmissionHandler canHandleFormNameEquals(String formName, Function<Submission, SubmitResponse> handlerLogic) {
			Objects.requireNonNull(formName, "Form Name for submission handler cannot be null.");
			return new AfSubmissionHandler() {

				@Override
				public boolean canHandle(String formNameIn) {
					return formName.equals(formNameIn);
				}

				@Override
				public SubmitResponse processSubmission(Submission submission) {
					return handlerLogic.apply(submission);
				}
			};
		}
		
		/**
		 * Creates an AfSubmissionHandler that handles form submissions from one or more specific forms
		 * 
		 * @param handlerLogic
		 * 		function that will be called when a form submission for occurs from that form
		 * @param formNames
		 * 		one or more form names (can be zero, but then this handler will never be called)
		 * @return
		 * 		an AfSubmissionHandler object
		 */
		public static AfSubmissionHandler canHandleFormNameAnyOf(Function<Submission, SubmitResponse> handlerLogic, String... formNames) {
			if (formNames.length < 1) {
				logger.atWarn().log("No form names were supplied, so this handler will never be called.");
			}
			return new AfSubmissionHandler() {

				@Override
				public boolean canHandle(String formNameIn) {
					for (String formName : formNames) {
						if (Objects.equals(formName, formNameIn)) {
							return true;
						}
					}
					return false;
				}

				@Override
				public SubmitResponse processSubmission(Submission submission) {
					return handlerLogic.apply(submission);
				}
			};
		}
		
		/**
		 * Creates an AfSubmissionHandler that handles form submissions from one or more specific forms
		 * 
		 * @param formNames
		 * 		list of one or more form names (can be empty, but then this handler will never be called)
		 * @param handlerLogic
		 * 		function that will be called when a form submission for occurs from that form
		 * @return
		 * 		an AfSubmissionHandler object
		 */
		public static AfSubmissionHandler canHandleFormNameAnyOf(List<String> formNames, Function<Submission, SubmitResponse> handlerLogic) {
			if (formNames.size() < 1) {
				logger.atWarn().log("No form names were supplied, so this handler will never be called.");
			}
			return new AfSubmissionHandler() {

				@Override
				public boolean canHandle(String formNameIn) {
					for (String formName : formNames) {
						if (Objects.equals(formName, formNameIn)) {
							return true;
						}
					}
					return false;
				}

				@Override
				public SubmitResponse processSubmission(Submission submission) {
					return handlerLogic.apply(submission);
				}
			};
		}
		
 		/**
		 * Creates an AfSubmissionHandler that handles form submissions from one or more specific forms
		 * 
 		 * @param formNameRegEx
		 * 		a regex that will be applied to the name of the form, if it matches, then the handler will apply
 		 * @param handlerLogic
		 * 		function that will be called when a form submission for occurs from that form
 		 * @return
		 * 		an AfSubmissionHandler object
 		 */
 		public static AfSubmissionHandler canHandleFormNameMatchesRegex(String formNameRegEx, Function<Submission, SubmitResponse> handlerLogic) {
			final var pattern = Pattern.compile(Objects.requireNonNull(formNameRegEx, "Form Name RegEx for submission handler cannot be null."));

			return new AfSubmissionHandler() {

				@Override
				public boolean canHandle(String formNameIn) {
					return pattern.matcher(formNameIn).matches();
				}

				@Override
				public SubmitResponse processSubmission(Submission submission) {
					return handlerLogic.apply(submission);
				}
			};
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
	static class AfSubmitLocalProcessor implements AfSubmitProcessor {
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
			return formulateResponse(handler.processSubmission(formulateSubmission(inFormData, headers, formName)));
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
			return new AfSubmissionHandler.Submission(extractedData.formData, 
													  formName, 
													  extractedData.redirectUrl, 
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