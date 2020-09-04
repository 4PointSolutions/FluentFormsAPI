package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Standard FormsFeeder Replacements
 *
 */
public class StandardFormsFeederUrlFilters {
	public static OutputStream replaceAemUrls(OutputStream outputStream) {
		outputStream = new ReplacingOutputStream(outputStream, "/etc.clientlibs/", "/aem/etc.clientlibs/");
		outputStream = new ReplacingOutputStream(outputStream, "/libs/wcm/", "/aem/libs/wcm/");
		outputStream = new ReplacingOutputStream(outputStream, "/etc/clientlibs/", "/aem/etc/clientlibs/");
		outputStream = new ReplacingOutputStream(outputStream, "/libs/fd/", "/aem/libs/fd/");
		outputStream = new ReplacingOutputStream(outputStream, "/content/forms/", "/aem/content/forms/");
		outputStream = new ReplacingOutputStream(outputStream, "/content/xfaforms/", "/aem/content/xfaforms/");
		outputStream = new ReplacingOutputStream(outputStream, "/libs/granite/", "/aem/libs/granite/");
		outputStream = new ReplacingOutputStream(outputStream, "/apps/", "/aem/apps/");
		return outputStream;
	}

	public static InputStream replaceAemUrls(InputStream inputStream) {
		inputStream = new ReplacingInputStream(inputStream, "/etc.clientlibs/", "/aem/etc.clientlibs/");
		inputStream = new ReplacingInputStream(inputStream, "/libs/wcm/", "/aem/libs/wcm/");
		inputStream = new ReplacingInputStream(inputStream, "/etc/clientlibs/", "/aem/etc/clientlibs/");
		inputStream = new ReplacingInputStream(inputStream, "/libs/fd/", "/aem/libs/fd/");
		inputStream = new ReplacingInputStream(inputStream, "/content/forms/", "/aem/content/forms/");
		inputStream = new ReplacingInputStream(inputStream, "/content/xfaforms/", "/aem/content/xfaforms/");
		inputStream = new ReplacingInputStream(inputStream, "/libs/granite/", "/aem/libs/granite/");
		inputStream = new ReplacingInputStream(inputStream, "/apps/", "/aem/apps/");
		return inputStream;
	}
}
