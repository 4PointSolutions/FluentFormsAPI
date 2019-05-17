package com._4point.aem.docservices.rest_services.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;

public class ServletUtils {

	// Private constructor to make sure this class doesn't get instantiated.
	private ServletUtils() {
	}
	
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

}
