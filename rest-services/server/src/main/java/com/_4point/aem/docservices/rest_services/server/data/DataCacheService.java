package com._4point.aem.docservices.rest_services.server.data;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.FormParameters;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=DataServices.DataCache Service",
											"sling.servlet.methods=" + HttpConstants.METHOD_GET})
@SlingServletPaths("/services/DataServices/DataCache")
public class DataCacheService extends SlingSafeMethodsServlet {
	private static final Logger log = LoggerFactory.getLogger(DataCacheService.class);

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.processInput(request, response);
		} catch (BadRequestException br) {
			log.warn("Bad Request from the user.", br);
			response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, br.getMessage());
		} catch (InternalServerErrorException ise) {
			log.error("Internal server error.", ise);
			response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, ise.getMessage());
		} catch (NotAcceptableException nae) {
			log.error("NotAcceptable error.", nae);
			response.sendError(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, nae.getMessage());
		} catch (Exception e) {  			// Some exception we haven't anticipated.
			log.error(e.getMessage() != null ? e.getMessage() : e.getClass().getName() , e);	// Make sure this gets into our log.
			throw e;
		}
	}

	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response) throws BadRequestException, InternalServerErrorException, NotAcceptableException {
		DataCacheParameters dataCacheParameters = DataCacheParameters.from(request);
		
		DataCache.Entry dataFromCache = DataCache.getDataFromCache(dataCacheParameters.dataKey());
		try {
			response.setContentType(dataFromCache.contentType());
			response.setContentLength(dataFromCache.data().length);
			response.getOutputStream().write(dataFromCache.data());
		} catch (IOException e) {
			throw new InternalServerErrorException("Error while writing response to response output stream.", e);
		}
	}

	private static class DataCacheParameters {
		private static final String DATA_KEY_PARAM = "DataKey";
		// At some point, I want to introduce the idea of a correlation id to rest-services services.  When that happens, we'll
		// need to pass the correlation id in as a query parameter and decode it here.
		// private static final String CORRELATION_ID_PARAM = CorrelationId.CORRELATION_ID_HDR;
		
		private final String dataKey;
		
		public DataCacheParameters(String dataKey) {
			super();
			this.dataKey = dataKey;
		}

		public String dataKey() {
			return dataKey;
		}

		private static DataCacheParameters from(SlingHttpServletRequest request) throws BadRequestException {
			RequestParameter dataKeyParam = FormParameters.getMandatoryParameter(request, DATA_KEY_PARAM);
			return new DataCacheParameters(dataKeyParam.getString());
		}
	}
}
