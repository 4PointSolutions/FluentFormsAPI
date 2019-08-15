package com._4point.aem.fluentforms.api.forms;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com._4point.aem.fluentforms.api.AbsoluteOrRelativeUrl;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.forms.FormsService.RenderPDFFormArgumentBuilder;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.RenderAtClient;

public interface PDFFormRenderOptionsSetter {

	PDFFormRenderOptionsSetter setAcrobatVersion(AcrobatVersion acrobatVersion);

	PDFFormRenderOptionsSetter setCacheStrategy(CacheStrategy strategy);

	default PDFFormRenderOptionsSetter setContentRoot(Path path) {
		return setContentRoot(new PathOrUrl(path));
	}

	default PDFFormRenderOptionsSetter setContentRoot(URL url) {
		return setContentRoot(new PathOrUrl(url));
	}

	PDFFormRenderOptionsSetter setContentRoot(PathOrUrl pathOrUrl);

	PDFFormRenderOptionsSetter setDebugDir(Path debugDir);

	PDFFormRenderOptionsSetter setLocale(Locale locale);

	PDFFormRenderOptionsSetter setRenderAtClient(RenderAtClient renderAtClient);

	PDFFormRenderOptionsSetter setSubmitUrls(List<AbsoluteOrRelativeUrl> urls);

	default PDFFormRenderOptionsSetter setSubmitUrlStrings(List<String> urlStrings) throws MalformedURLException {
		int listLen = Objects.requireNonNull(urlStrings, "Submit URL Strings cannot be null.").size();
		List<AbsoluteOrRelativeUrl> urls = new ArrayList<>(listLen);
		for(String str : urlStrings) {
			urls.add(AbsoluteOrRelativeUrl.fromString(str));
		}
		setSubmitUrls(urls);
		return this;
	}

	default PDFFormRenderOptionsSetter setSubmitUrlsList(List<URL> urlList) {
		int listLen = Objects.requireNonNull(urlList, "Submit URL Strings cannot be null.").size();
		List<AbsoluteOrRelativeUrl> urls = new ArrayList<>(listLen);
		for(URL url : urlList) {
			urls.add(AbsoluteOrRelativeUrl.fromUrl(url));
		}
		setSubmitUrls(urls);
		return this;
	}

	default PDFFormRenderOptionsSetter setSubmitUrl(AbsoluteOrRelativeUrl url) {
		setSubmitUrls(Arrays.asList(url));
		return this;
	}

	default PDFFormRenderOptionsSetter setSubmitUrlString(String urlString) throws MalformedURLException {
		setSubmitUrls(Arrays.asList(AbsoluteOrRelativeUrl.fromString(urlString)));
		return this;
	}

	default PDFFormRenderOptionsSetter setSubmitUrl(URL url) {
		setSubmitUrls(Arrays.asList(AbsoluteOrRelativeUrl.fromUrl(url)));
		return this;
	}

	PDFFormRenderOptionsSetter setTaggedPDF(boolean isTagged);

	PDFFormRenderOptionsSetter setXci(Document xci);

}