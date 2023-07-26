package com._4point.aem.fluentforms.spring;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/aem")
public class AemProxyAfSubmission {
	private final static Logger logger = LoggerFactory.getLogger(AemProxyAfSubmission.class);
	
	@Autowired
	AfSubmitProcessor submitProcessor;

	@Path("content/forms/af/{remainder : .+}")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response proxySubmitPost(@PathParam("remainder") String remainder, /* @HeaderParam(CorrelationId.CORRELATION_ID_HDR) final String correlationIdHdr,*/ @Context HttpHeaders headers, final FormDataMultiPart inFormData)  {
		logger.atInfo().addArgument(()->submitProcessor != null ?  submitProcessor.getClass().getName() : "null" ).log("Submit proxy called. SubmitProcessor={}");
//		final String correlationId = CorrelationId.generate(correlationIdHdr);
//		ProcessingMetadataBuilder pmBuilder = ProcessingMetadata.start(correlationId);
		return submitProcessor.processRequest(inFormData, headers, remainder);
	}
	
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
}
