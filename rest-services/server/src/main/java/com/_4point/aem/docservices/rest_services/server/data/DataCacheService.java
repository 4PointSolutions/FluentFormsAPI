package com._4point.aem.docservices.rest_services.server.data;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.FormParameters;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.docservices.rest_services.server.data.DataCache.Entry;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=DataServices.DataCache Service",
											"sling.servlet.methods=" + HttpConstants.METHOD_GET})
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/DataServices/DataCache")
public class DataCacheService extends SlingAllMethodsServlet {
	private static final Logger log = LoggerFactory.getLogger(DataCacheService.class);

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.processGet(request, response);
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

	private void processGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws BadRequestException, InternalServerErrorException, NotAcceptableException {
		DataCacheGetParameters dataCacheParameters = DataCacheGetParameters.from(request);
		
		Optional<Entry> dataFromCache = DataCache.getDataFromCache(dataCacheParameters.dataKey());
		try {
			if (dataFromCache.isPresent()) {
				Entry data = dataFromCache.get();
				response.setContentType(data.contentType());
				response.setContentLength(data.data().length);
				response.getOutputStream().write(data.data());
			} else {
				response.sendError(SlingHttpServletResponse.SC_NOT_FOUND, "Unable to find data associated with key '" + dataCacheParameters.dataKey() + "'.");
			}
		} catch (IOException e) {
			throw new InternalServerErrorException("Error while writing response to response output stream.", e);
		}
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.processPost(request, response);
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

	private void processPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws BadRequestException, InternalServerErrorException, NotAcceptableException {
		DataCachePostParameters dataCacheParameters = DataCachePostParameters.from(request);

		String dataKey = DataCache.addDataToCache(dataCacheParameters.getData(), dataCacheParameters.getContentType());
		try {
			response.setContentType("text/plain");
			response.setContentLength(dataKey.length());
			response.getWriter().write(dataKey);
		} catch (IOException e) {
			throw new InternalServerErrorException("Error while writing response to response output stream.", e);
		}
	}

	private static class DataCacheGetParameters {
		private static final String DATA_KEY_PARAM = "DataKey";
		// At some point, I want to introduce the idea of a correlation id to rest-services services.  When that happens, we'll
		// need to pass the correlation id in as a query parameter and decode it here.
		// private static final String CORRELATION_ID_PARAM = CorrelationId.CORRELATION_ID_HDR;
		
		private final String dataKey;
		
		public DataCacheGetParameters(String dataKey) {
			super();
			this.dataKey = dataKey;
		}

		public String dataKey() {
			return dataKey;
		}

		private static DataCacheGetParameters from(SlingHttpServletRequest request) throws BadRequestException {
			RequestParameter dataKeyParam = FormParameters.getMandatoryParameter(request, DATA_KEY_PARAM);
			return new DataCacheGetParameters(dataKeyParam.getString());
		}
	}

	private static class DataCachePostParameters {
		private static final String DATA_PARAM = "Data";
		// At some point, I want to introduce the idea of a correlation id to rest-services services.  When that happens, we'll
		// need to pass the correlation id in as a query parameter and decode it here.
		// private static final String CORRELATION_ID_PARAM = CorrelationId.CORRELATION_ID_HDR;
		
		private final byte[] data;
		private final String contentType;
		
		public DataCachePostParameters(byte[] data, String contentType) {
			super();
			this.data = data;
			this.contentType = contentType;
		}

		public byte[] getData() {
			return data;
		}

		public String getContentType() {
			return contentType;
		}

		private static DataCachePostParameters from(SlingHttpServletRequest request) throws BadRequestException {
			RequestParameter dataKeyParam = FormParameters.getMandatoryParameter(request, DATA_PARAM);
			return new DataCachePostParameters(dataKeyParam.get(), dataKeyParam.getContentType());
		}
	}
}
