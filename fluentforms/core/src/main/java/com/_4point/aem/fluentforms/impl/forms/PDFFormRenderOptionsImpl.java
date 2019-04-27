package com._4point.aem.fluentforms.impl.forms;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptionsSetter;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;

public class PDFFormRenderOptionsImpl implements PDFFormRenderOptionsSetter {

	private AcrobatVersion acrobatVersion = null;
	private CacheStrategy cacheStrategy = null;
	private Path contentRoot = null;
	private Path debugDir = null;
	private Locale locale = null;
	private List<URL> submitUrls = null;
	private Boolean taggedPDF = null;
	private Document xci = null;

	public AcrobatVersion getAcrobatVersion() {
		return acrobatVersion;
	}

	@Override
	public PDFFormRenderOptionsImpl setAcrobatVersion(AcrobatVersion acrobatVersion) {
		this.acrobatVersion = acrobatVersion;
		return this;
	}

	public CacheStrategy getCacheStrategy() {
		return cacheStrategy;
	}

	@Override
	public PDFFormRenderOptionsImpl setCacheStrategy(CacheStrategy cacheStrategy) {
		this.cacheStrategy = cacheStrategy;
		return this;
	}

	public Path getContentRoot() {
		return contentRoot;
	}

	@Override
	public PDFFormRenderOptionsImpl setContentRoot(Path contentRoot) {
		this.contentRoot = contentRoot;
		return this;
	}

	public Path getDebugDir() {
		return debugDir;
	}

	@Override
	public PDFFormRenderOptionsImpl setDebugDir(Path debugDir) {
		this.debugDir = debugDir;
		return this;
	}

	public Locale getLocale() {
		return locale;
	}

	@Override
	public PDFFormRenderOptionsImpl setLocale(Locale locale) {
		this.locale = locale;
		return this;
	}

	public List<URL> getSubmitUrls() {
		return submitUrls;
	}

	@Override
	public PDFFormRenderOptionsImpl setSubmitUrls(List<URL> submitUrls) {
		this.submitUrls = submitUrls;
		return this;
	}

	public boolean isTaggedPDF() {
		return taggedPDF;
	}

	@Override
	public PDFFormRenderOptionsImpl setTaggedPDF(boolean taggedPDF) {
		this.taggedPDF = taggedPDF;
		return this;
	}

	public Document getXci() {
		return xci;
	}

	@Override
	public PDFFormRenderOptionsImpl setXci(Document xci) {
		this.xci = xci;
		return this;
	}

	public com.adobe.fd.forms.api.PDFFormRenderOptions toAdobePDFFormRenderOptions() {
		com.adobe.fd.forms.api.PDFFormRenderOptions adobeOptions = new com.adobe.fd.forms.api.PDFFormRenderOptions();
		setIfNotNull(adobeOptions::setAcrobatVersion,this.acrobatVersion);
		setIfNotNull(adobeOptions::setCacheStrategy, this.cacheStrategy);
		setIfNotNull((cr)->adobeOptions.setContentRoot(cr.toString()), this.contentRoot);
		setIfNotNull((dd)->adobeOptions.setDebugDir(dd.toString()), this.debugDir);
		setIfNotNull((l)->adobeOptions.setLocale(l.toString()), this.locale);
		setIfNotNull(adobeOptions::setSubmitUrls, mapToStrings(this.submitUrls));
		setIfNotNull(adobeOptions::setTaggedPDF, this.taggedPDF);
		setIfNotNull((ad)->adobeOptions.setXci(ad.getAdobeDocument()), this.xci);
		return adobeOptions;
	}

	private static List<String> mapToStrings(List<URL> urls) {
		return urls == null ? null : urls.stream().map(URL::toString).collect(Collectors.toList());
	}

}
