package com._4point.aem.docservices.rest_services.server.workflow;

import java.io.IOException;
import java.util.function.Supplier;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.output.GeneratePdfOutput;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.impl.workflow.WorkflowServiceAdapter;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=WorkflowService.StartWorkflow Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/WorkflowService/StartWorkflow")
public class StartWorkflow extends SlingAllMethodsServlet {
	private static final Logger log = LoggerFactory.getLogger(GeneratePdfOutput.class);

	private final DocumentFactory docFactory = DocumentFactory.getDefault();
	private final Supplier<WorkflowServiceAdapter> workflowServiceFactory = this::getAdobeWorkflowService;
	
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.processInput(request, response);
		} catch (BadRequestException br) {
			log.warn("Bad Request from the user", br);
			response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, br.getMessage());
		} catch (InternalServerErrorException ise) {
			log.error("Internal server error", ise);
			response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, ise.getMessage());
		} catch (NotAcceptableException nae) {
			log.error("NotAcceptable error", nae);
			response.sendError(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, nae.getMessage());
		} catch (Exception e) {  			// Some exception we haven't anticipated.
			log.error(e.getMessage() != null ? e.getMessage() : e.getClass().getName() , e);	// Make sure this gets into our log.
			throw e;
		}
	}

	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response) throws BadRequestException, InternalServerErrorException, NotAcceptableException {
		// TODO:  Fill this in.
	}
	
	private WorkflowServiceAdapter getAdobeWorkflowService() {
		// TODO: Fill this in.
		return null;
	}
}
