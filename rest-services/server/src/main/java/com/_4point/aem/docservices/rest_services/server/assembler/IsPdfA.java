package com._4point.aem.docservices.rest_services.server.assembler;

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
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AdobeAssemblerServiceAdapter;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;

@SuppressWarnings("serial")
@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=AssemblerService.IsPdfA Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST })
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/AssemblerService/IsPdfA")
public class IsPdfA  extends SlingAllMethodsServlet {
	private static final Logger log = LoggerFactory.getLogger(IsPdfA.class);

	@Reference
	private com.adobe.fd.assembler.service.AssemblerService adobeAssembleService;

	private final Supplier<TraditionalDocAssemblerService> assemblerServiceFactory = this::getAdobeAssemblerService;

	private final DocumentFactory docFactory = AdobeDocumentFactoryImpl.getFactory();	// We know we're running on AEM, so we'll use the AdobeDocumentFactoryImpl.

	private TraditionalDocAssemblerService getAdobeAssemblerService() {
		return new AdobeAssemblerServiceAdapter(adobeAssembleService, docFactory);
	}

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
		} catch (Exception e) { // Some exception we haven't anticipated.
			log.error(e.getMessage() != null ? e.getMessage() : e.getClass().getName(), e); // Make sure this gets into
			// our log.
			throw e;
		}
	}

	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws BadRequestException, InternalServerErrorException, NotAcceptableException {
		AssemblerService assemblerService = new AssemblerServiceImpl(assemblerServiceFactory.get(),	UsageContext.SERVER_SIDE);
		// TODO: Fill this in...
	}
}
