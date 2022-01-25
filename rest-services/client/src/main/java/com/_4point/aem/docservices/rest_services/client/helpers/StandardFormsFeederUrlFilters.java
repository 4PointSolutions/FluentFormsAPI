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
	
	public static OutputStream replaceAemUrls(OutputStream outputStream) {
		return replaceAemUrls(outputStream, AemServerType.StandardType.OSGI.pathPrefix());
	}

	public static OutputStream replaceAemUrls(OutputStream outputStream, AemServerType aemServerType) {
		return replaceAemUrls(outputStream, aemServerType.pathPrefix());
	}

	public static OutputStream replaceAemUrls(OutputStream outputStream, String prefix) {
		return getUrlFilterBuilder().appPrefix(prefix)
									.buildOutputStreamFn()
									.apply(outputStream);
	}

	public static InputStream replaceAemUrls(InputStream inputStream) {
		return replaceAemUrls(inputStream, AemServerType.StandardType.OSGI.pathPrefix());
		
	}
	
	public static InputStream replaceAemUrls(InputStream inputStream, AemServerType aemServerType) {
		return replaceAemUrls(inputStream, aemServerType.pathPrefix());
		
	}
	
	public static InputStream replaceAemUrls(InputStream inputStream, String prefix) {
		return getUrlFilterBuilder().appPrefix(prefix)
				.buildInputStreamFn()
				.apply(inputStream);
	}

	public static FormsFeederUrlFilterBuilder getUrlFilterBuilder() {
		return new FormsFeederUrlFilterBuilder(replacedUrls);
	}
	
	public static FormsFeederUrlFilterBuilder getUrlFilterBuilder(AemServerType aemServerType) {
		return new FormsFeederUrlFilterBuilder(replacedUrls).appPrefix(aemServerType.pathPrefix());
	}

	public static FormsFeederUrlFilterBuilder getUrlFilterBuilder(String prefix) {
		return new FormsFeederUrlFilterBuilder(replacedUrls).appPrefix(prefix);
	}
}
