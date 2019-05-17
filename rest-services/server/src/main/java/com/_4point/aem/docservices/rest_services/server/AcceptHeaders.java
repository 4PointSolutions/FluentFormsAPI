package com._4point.aem.docservices.rest_services.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;

public class AcceptHeaders {
	public static final String ACCEPT_HEADER_STR = "Accept";
	
	private static final String SEPARATOR = ",";
	
	private final List<ContentType> acceptTypes = new ArrayList<>();
	private final String originalAcceptHeaderString;	// Cache it for later reference.
	
	public AcceptHeaders(String acceptHeaderString) {
		this.originalAcceptHeaderString = acceptHeaderString;
		// N.B. We don't currently handle any q values or additional parameters on the content string (like charset).
		String[] contentTypeStrings = acceptHeaderString.split(SEPARATOR);
		for (String contentTypeString : contentTypeStrings) {
			String trimmedString = contentTypeString.trim();
			if (!trimmedString.isEmpty()) {		// ignore empty strings
				acceptTypes.add(ContentType.valueOf(trimmedString));
			}
		}
	}

	public Optional<ContentType> negotiateResponseContentType(List<ContentType> responseTypes) {
		for (ContentType acceptType : acceptTypes) {
			for (ContentType responseType : responseTypes) {
				if (acceptType.isCompatibleWith(responseType)) {
					return Optional.of(responseType);	// Return the first compatible responseType we find
				}
			}
		}
		return Optional.empty();	// Didn't find any compatible responseTypes.
	}
	
	public void validateResponseContentType(List<ContentType> responseTypes) throws NotAcceptableException {
		Optional<ContentType> responseContentType = negotiateResponseContentType(responseTypes);
	    if (!responseContentType.isPresent()) {
	    	StringJoiner sj = new StringJoiner(", ", "Unable to find an acceptable type among '" + this.toString() + "' (based on Accept Header '" + originalAcceptHeaderString + "').  Acceptable types are ", ".");
	    	for ( ContentType act : responseTypes) {
	    		sj.add("'" + act.getContentTypeStr() + "'");
	    	}
	    	String message = sj.toString();
			throw new NotAcceptableException(message);
	    }
	}

	
	@Override
	public String toString() {
		return ACCEPT_HEADER_STR + ": " + acceptTypes.stream().map(ContentType::getContentTypeStr).collect(Collectors.joining(","));
	}

}
