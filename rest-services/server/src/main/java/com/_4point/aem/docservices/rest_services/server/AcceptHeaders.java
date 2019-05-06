package com._4point.aem.docservices.rest_services.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AcceptHeaders {
	public static final String ACCEPT_HEADER_STR = "Accept";
	
	private static final String SEPARATOR = ",";
	
	private List<ContentType> acceptTypes = new ArrayList<>();
	
	public AcceptHeaders(String acceptHeaderString) {
		// N.B. We don't currently handle any q values or additional parameters on the content string (like charset).
		String[] contentTypeStrings = acceptHeaderString.split(SEPARATOR);
		for (String contentTypeString : contentTypeStrings) {
			String trimmedString = contentTypeString.trim();
			if (!trimmedString.isEmpty()) {		// ignore empty strings
				acceptTypes.add(new ContentType(trimmedString));
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
	
	@Override
	public String toString() {
		return ACCEPT_HEADER_STR + ": " + acceptTypes.stream().map(ContentType::getContentTypeStr).collect(Collectors.joining(","));
	}

}
