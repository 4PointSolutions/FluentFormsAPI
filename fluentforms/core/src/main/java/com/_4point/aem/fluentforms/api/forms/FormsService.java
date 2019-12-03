package com._4point.aem.fluentforms.api.forms;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import com._4point.aem.fluentforms.api.AbsoluteOrRelativeUrl;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.Transformable;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.RenderAtClient;


public interface FormsService {

	Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException;

	Document importData(Document pdf, Document data) throws FormsServiceException;

	Document renderPDFForm(Path filename, Document data, PDFFormRenderOptions pdfFormRenderOptions) throws FormsServiceException, FileNotFoundException;

	Document renderPDFForm(URL url, Document data, PDFFormRenderOptions pdfFormRenderOptions) throws FormsServiceException;

	Document renderPDFForm(PathOrUrl pathOrUrl, Document data, PDFFormRenderOptions pdfFormRenderOptions) throws FormsServiceException, FileNotFoundException;

	RenderPDFFormArgumentBuilder renderPDFForm();

	ValidationResult validate(Path template, Document data, ValidationOptions validationOptions) throws FormsServiceException, FileNotFoundException;

	ValidateArgumentBuilder validate();

	public static class FormsServiceException extends Exception {

		private static final long serialVersionUID = -9187778886719471016L;

		public FormsServiceException() {
			super();
		}

		public FormsServiceException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public FormsServiceException(String arg0) {
			super(arg0);
		}

		public FormsServiceException(Throwable arg0) {
			super(arg0);
		}
	}
	
	public static interface RenderPDFFormArgumentBuilder extends PDFFormRenderOptionsSetter, Transformable<RenderPDFFormArgumentBuilder> {
		
		@Override
		public RenderPDFFormArgumentBuilder setAcrobatVersion(AcrobatVersion acrobatVersion);

		@Override
		public RenderPDFFormArgumentBuilder setCacheStrategy(CacheStrategy strategy);

		@Override
		public default RenderPDFFormArgumentBuilder setContentRoot(Path url) {
			PDFFormRenderOptionsSetter.super.setContentRoot(url);
			return this;
		}

		@Override
		public default RenderPDFFormArgumentBuilder setContentRoot(URL url) {
			PDFFormRenderOptionsSetter.super.setContentRoot(url);
			return this;
		}

		@Override
		public RenderPDFFormArgumentBuilder setContentRoot(PathOrUrl url);

		@Override
		public RenderPDFFormArgumentBuilder setDebugDir(Path debugDir);

		@Override
		public RenderPDFFormArgumentBuilder setEmbedFonts(boolean embedFonts);

		@Override
		public RenderPDFFormArgumentBuilder setLocale(Locale locale);

		@Override
		public RenderPDFFormArgumentBuilder setRenderAtClient(RenderAtClient renderAtClient);

		@Override
		public default RenderPDFFormArgumentBuilder setSubmitUrl(URL url) {
			PDFFormRenderOptionsSetter.super.setSubmitUrl(url);
			return this;
		}

		@Override
		public default RenderPDFFormArgumentBuilder setSubmitUrlStrings(List<String> stringList) throws MalformedURLException {
			PDFFormRenderOptionsSetter.super.setSubmitUrlStrings(stringList);
			return this;
		}

		@Override
		public default RenderPDFFormArgumentBuilder setSubmitUrlsList(List<URL> urlList) {
			PDFFormRenderOptionsSetter.super.setSubmitUrlsList(urlList);
			return this;
		}

		@Override
		public default RenderPDFFormArgumentBuilder setSubmitUrl(AbsoluteOrRelativeUrl url) {
			PDFFormRenderOptionsSetter.super.setSubmitUrl(url);
			return this;
		}

		@Override
		public default RenderPDFFormArgumentBuilder setSubmitUrlString(String url) throws MalformedURLException {
			PDFFormRenderOptionsSetter.super.setSubmitUrlString(url);
			return this;
		}

		@Override
		public RenderPDFFormArgumentBuilder setSubmitUrls(List<AbsoluteOrRelativeUrl> urls);

		@Override
		public RenderPDFFormArgumentBuilder setTaggedPDF(boolean isTagged);

		@Override
		public RenderPDFFormArgumentBuilder setXci(Document xci);

		public Document executeOn(PathOrUrl template, Document data) throws FormsServiceException, FileNotFoundException;

		public Document executeOn(Path template, Document data) throws FormsServiceException, FileNotFoundException;
		
		public Document executeOn(URL template, Document data) throws FormsServiceException;

	}
	
	public static interface ValidateArgumentBuilder extends ValidationOptionsSetter, Transformable<ValidateArgumentBuilder> {
		
		@Override
		public ValidateArgumentBuilder setContentRoot(Path contentRootDir);

		@Override
		public ValidateArgumentBuilder setDebugDir(Path debugDir);

		public ValidationResult executeOn(Path template, Document data) throws FormsServiceException;
	}

}
