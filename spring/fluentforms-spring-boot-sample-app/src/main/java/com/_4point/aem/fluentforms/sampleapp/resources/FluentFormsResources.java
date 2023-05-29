package com._4point.aem.fluentforms.sampleapp.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.spring.FluentFormsConfiguration;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
	@Produces({APPLICATION_PDF, "*/*;qs=0.8"})	// Will be selected if user requests JSON or nothing at all.
	public Response outputServiceGeneratePdf() {
		if (outputService == null) return Response.serverError().build();
		return Response.ok().build();
	}

	
}
