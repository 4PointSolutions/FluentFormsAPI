package com._4point.aem.docservices.rest_services.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.fluentforms.api.Document;

public class ServletUtils {
	public static final String PAGE_COUNT_HEADER = "com._4point.aem.rest_services.page_count";
	// Private constructor to make sure this class doesn't get instantiated.
	private ServletUtils() {
	}
	
	public static final String SERVICES_PREFIX = "/services";
	
	public static void validateAcceptHeader(String acceptHeaderStr, String generatedContentType) throws NotAcceptableException {
		if ( acceptHeaderStr != null) {
			// If we've been supplied with an accept header, make sure it is correct.
			AcceptHeaders acceptHeaders = new AcceptHeaders(acceptHeaderStr);
	        List<ContentType> acceptableContentTypes = Arrays.asList(ContentType.valueOf(generatedContentType));
	        acceptHeaders.validateResponseContentType(acceptableContentTypes);
		}
	}

	public static void transfer(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		// read bytes from the input stream and store them in buffer
		while ((len = in.read(buffer)) != -1) {
			// write bytes from the buffer into output stream
			out.write(buffer, 0, len);
		}
	}

	public static void transferDocumentToResponse(SlingHttpServletRequest request, SlingHttpServletResponse response,
			Document result, boolean returnLength) throws IOException, NotAcceptableException {
		String contentType = result.getContentType();
		ServletUtils.validateAcceptHeader(request.getHeader(AcceptHeaders.ACCEPT_HEADER_STR), contentType);
		response.setContentType(contentType);
		if (returnLength) { // We don't want to always get length do to an issue with the Adobe code (see FluentFormsAPI Issue #15).
			response.setContentLength((int)result.length());
		}
		result.getPageCount().ifPresent(l->response.setHeader(PAGE_COUNT_HEADER, l.toString()));
		ServletUtils.transfer(result.getInputStream(), response.getOutputStream());
	}


}
