package com._4point.aem.fluentforms.api.forms;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;

public interface PDFFormRenderOptionsSetter {

	PDFFormRenderOptionsSetter setAcrobatVersion(AcrobatVersion acrobatVersion);

	PDFFormRenderOptionsSetter setCacheStrategy(CacheStrategy strategy);

	PDFFormRenderOptionsSetter setContentRoot(Path path);

	PDFFormRenderOptionsSetter setContentRoot(URL url);

	default PDFFormRenderOptionsSetter setContentRoot(PathOrUrl pathOrUrl) {
		Objects.requireNonNull(pathOrUrl, "contentRoot cannot be null.");
		if (pathOrUrl.isPath()) {
			this.setContentRoot(pathOrUrl.getPath());
		} else if (pathOrUrl.isUrl()) {
			this.setContentRoot(pathOrUrl.getUrl());
		} else {
			throw new IllegalStateException("contentRoot PathOrUrl object was neither Path nor URL.");
		}
		return this;
	}

	PDFFormRenderOptionsSetter setDebugDir(Path debugDir);

	PDFFormRenderOptionsSetter setLocale(Locale locale);

	PDFFormRenderOptionsSetter setSubmitUrls(List<URL> urls);

	default PDFFormRenderOptionsSetter setSubmitUrlStrings(List<String> urlStrings) throws MalformedURLException {
		int listLen = Objects.requireNonNull(urlStrings, "Submit URL Strings cannot be null.").size();
		List<URL> urls = new ArrayList<>(listLen);
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