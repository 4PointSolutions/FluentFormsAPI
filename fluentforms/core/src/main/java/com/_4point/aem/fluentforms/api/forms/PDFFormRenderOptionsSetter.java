package com._4point.aem.fluentforms.api.forms;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com._4point.aem.fluentforms.api.Document;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;

public interface PDFFormRenderOptionsSetter {

	PDFFormRenderOptionsSetter setAcrobatVersion(AcrobatVersion acrobatVersion);

	PDFFormRenderOptionsSetter setCacheStrategy(CacheStrategy strategy);

	PDFFormRenderOptionsSetter setContentRoot(Path url);

	PDFFormRenderOptionsSetter setDebugDir(Path debugDir);

	PDFFormRenderOptionsSetter setLocale(Locale locale);

	PDFFormRenderOptionsSetter setSubmitUrls(List<URL> urls);

	default PDFFormRenderOptionsSetter setSubmitUrlStrings(List<String> urlStrings) throws MalformedURLException {
		List<URL> urls = new ArrayList<>(urlStrings.size());
		for(String str : urlStrings) {
			urls.add(new URL(str));
		}
		setSubmitUrls(urls);
		return this;
	}

	default PDFFormRenderOptionsSetter setSubmitUrl(URL url) {
		setSubmitUrls(Arrays.asList(url));
		return this;
	}

	default PDFFormRenderOptionsSetter setSubmitUrlString(String urlString) throws MalformedURLException {
		setSubmitUrls(Arrays.asList(new URL(urlString)));
		return this;
	}

	PDFFormRenderOptionsSetter setTaggedPDF(boolean isTagged);

	PDFFormRenderOptionsSetter setXci(Document xci);

}