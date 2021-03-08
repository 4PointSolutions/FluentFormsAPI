package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Standard FormsFeeder Replacements
 *
 */
public class StandardFormsFeederUrlFilters {
	private static final String[] replacedUrls = {
			"/etc.clientlibs/",
			"/libs/wcm/",
			"/etc/clientlibs/",
			"/libs/fd/",
			"/content/forms/",
			"/content/xfaforms/",
			"/libs/granite/",
			"/apps/"
	};
	
	private static final String FORMSFEEDER_URL_PREFIX = "/aem";
	
	public static OutputStream replaceAemUrls(OutputStream outputStream) {
		return replaceAemUrls(outputStream, AemServerType.StandardType.OSGI.pathPrefix());
	}

	public static OutputStream replaceAemUrls(OutputStream outputStream, AemServerType aemServerType) {
		return replaceAemUrls(outputStream, aemServerType.pathPrefix());
	}

	public static OutputStream replaceAemUrls(OutputStream outputStream, String prefix) {
		for (String url : replacedUrls) {
			outputStream = new ReplacingOutputStream(outputStream, prefix + url, FORMSFEEDER_URL_PREFIX + prefix + url);
		}
		return outputStream;
	}

	public static InputStream replaceAemUrls(InputStream inputStream) {
		return replaceAemUrls(inputStream, AemServerType.StandardType.OSGI.pathPrefix());
		
	}
	
	public static InputStream replaceAemUrls(InputStream inputStream, AemServerType aemServerType) {
		return replaceAemUrls(inputStream, aemServerType.pathPrefix());
		
	}
	
	public static InputStream replaceAemUrls(InputStream inputStream, String prefix) {
		for (String url : replacedUrls) {
			inputStream = new ReplacingInputStream(inputStream, prefix + url, FORMSFEEDER_URL_PREFIX + prefix + url);
		}
		return inputStream;
	}
}
