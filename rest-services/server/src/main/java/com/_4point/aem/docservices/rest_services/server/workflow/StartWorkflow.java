package com._4point.aem.docservices.rest_services.server.workflow;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
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
import com._4point.aem.fluentforms.api.workflow.Workflow;
import com._4point.aem.fluentforms.api.workflow.WorkflowService;
import com._4point.aem.fluentforms.api.workflow.WorkflowService.WorkflowServiceException;
import com._4point.aem.fluentforms.impl.workflow.AdobeWorkflowServiceAdapter;
import com._4point.aem.fluentforms.impl.workflow.WorkflowServiceAdapter;
import com._4point.aem.fluentforms.impl.workflow.WorkflowServiceImpl;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=WorkflowService.StartWorkflow Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/WorkflowService/StartWorkflow")
public class StartWorkflow extends SlingAllMethodsServlet {
	private static final Logger log = LoggerFactory.getLogger(GeneratePdfOutput.class);

	private final DocumentFactory docFactory = DocumentFactory.getDefault();
	private final Function<SlingHttpServletRequest, WorkflowServiceAdapter> workflowServiceFactory = this::getAdobeWorkflowService;
	
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
		StartWorkflowParameters<?, ?> startWorkflowParameters = StartWorkflowParameters.readWorkflowParameters(request);
		// TODO: Initialize the variables below from startWorkflowParameters
		String modelPath = null;
		String payloadType = null;
		Object payload = null;
		Map metadata = null;
		Class resultClass = null;
		try {
			WorkflowService workflowService = new WorkflowServiceImpl(workflowServiceFactory.apply(request));
			Workflow workflow = workflowService.startWorkflow(modelPath, payloadType, payload, metadata, resultClass);
			// Return the ID to the client
		} catch (WorkflowServiceException e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	private WorkflowServiceAdapter getAdobeWorkflowService(SlingHttpServletRequest request) {
		return AdobeWorkflowServiceAdapter.from(request.getResourceResolver());
	}
	
	static class StartWorkflowParameters<T, R> {
		private final String MODEL_PATH_PARAM = "modelPath";
		private final String PAYLOAD_TYPE_PARAM = "payloadType";
		private final String PAYLOAD_PARAM = "payload";
		private final String PAYLOAD_CLASS_PARAM = "payloadClass";
		private final String METADATA_PARAM = "metadata";
		private final String RESULT_CLASS_PARAM = "resultClass";
		
		private final String modelPath;
		private final String payloadType;
		private final T payload;
		private final Map<String, ?> metadata;
		private final Class<R> resultClass;

		private StartWorkflowParameters(String modelPath, String payloadType, T payload, Map<String, ?> metadata, Class<R> resultClass) {
			this.modelPath = modelPath;
			this.payloadType = payloadType;
			this.payload = payload;
			this.metadata = metadata;
			this.resultClass = resultClass;
		}
		
		public static StartWorkflowParameters<?, ?> readWorkflowParameters(SlingHttpServletRequest request) throws BadRequestException {
			return null;
		}
	}
}
