package com._4point.aem.fluentforms.sampleapp.resources;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService;
import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService.AdaptiveFormsServiceException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.spring.FluentFormsConfiguration;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path(FluentFormsResources.RESOURCE_PATH)
public class FluentFormsResources {
	private final static Logger log = LoggerFactory.getLogger(FluentFormsResources.class);

	private static final String APPLICATION_PDF = "application/pdf";
	private static final MediaType APPLICATION_PDF_TYPE = MediaType.valueOf(APPLICATION_PDF);

	protected static final String RESOURCE_PATH = "/FluentForms";
	
	@Autowired
	OutputService outputService;
	
	@Path("/OutputServiceGeneratePdf")
	@GET
	@Produces({APPLICATION_PDF, "*/*;qs=0.8"})	// Will be selected if user requests PDF or nothing at all.
	public Response outputServiceGeneratePdf(@QueryParam("form") String templateName) throws OutputServiceException, IOException {
		if (outputService == null) return Response.serverError().build();
		
		Document result = outputService.generatePDFOutput()
									   .executeOn(java.nio.file.Path.of(templateName));
		
		
		return Response.ok().entity(result.getInputStream()).type(result.getContentType()).build();
	}

	@Autowired
	AdaptiveFormsService adaptiveFormsService;
	
	@Path("/AdaptiveFormsServiceRenderAdaptiveForm")
	@GET
	@Produces({MediaType.TEXT_HTML, "*/*;qs=0.8"})	// Will be selected if user requests HTML or nothing at all.
	public Response adaptiveFormsServiceRenderAdaptiveForm(@QueryParam("form") String templateName) throws AdaptiveFormsServiceException, IOException {
		Document result = adaptiveFormsService.renderAdaptiveForm(templateName);
		
		return Response.ok().entity(result.getInputStream()).type(result.getContentType()).build();
	}

	
}
