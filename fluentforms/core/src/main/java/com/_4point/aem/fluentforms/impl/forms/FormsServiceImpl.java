package com._4point.aem.fluentforms.impl.forms;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptionsSetter;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationOptionsSetter;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

public class FormsServiceImpl implements FormsService {
	
	private final TraditionalFormsService adobeFormsService;

	public FormsServiceImpl() {
		super();
		this.adobeFormsService = new SafeFormsServiceAdapterWrapper(new AdobeFormsServiceAdapter());
	}

	public FormsServiceImpl(DocumentFactory documentFactory) {
		super();
		this.adobeFormsService = new SafeFormsServiceAdapterWrapper(new AdobeFormsServiceAdapter(documentFactory));
	}

	public FormsServiceImpl(TraditionalFormsService adobeFormsService) {
		super();
		this.adobeFormsService = new SafeFormsServiceAdapterWrapper(adobeFormsService);
	}

	@Override
	public Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException {
		return adobeFormsService.exportData(pdfOrXdp, dataFormat);
	}

	@Override
	public Document importData(Document pdf, Document data) throws FormsServiceException {
		return adobeFormsService.importData(pdf, data);
	}

	@Override
	public Document renderPDFForm(Path filename, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		Objects.requireNonNull(filename, "filename cannot be null.");
		return this.renderPDFForm(filename.toString(), data, pdfFormRenderOptions);
	}

	@Override
	public Document renderPDFForm(URL url, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		Objects.requireNonNull(url, "url cannot be null.");
		return this.renderPDFForm(url.toString(), data, pdfFormRenderOptions);
	}

	private Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		return adobeFormsService.renderPDFForm(urlOrfilename, data, pdfFormRenderOptions);
	}

	@Override
	public RenderPDFFormArgumentBuilder renderPDFForm() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValidationResult validate(Path template, Document data, ValidationOptions validationOptions)
			throws FormsServiceException {
		if (!template.toFile().exists()) { throw new FormsServiceException(new FileNotFoundException("Template file must exist.")); }
		return adobeFormsService.validate(template.toString(), data, validationOptions);
	}

	@Override
	public ValidateArgumentBuilder validate() {
		return new ValidateArgumentBuilderImpl();
	}

	public class ValidateArgumentBuilderImpl implements ValidateArgumentBuilder {
		ValidationOptionsBuilder validationOptions = new ValidationOptionsBuilder();
		
		@Override
		public ValidationOptionsSetter setContentRoot(Path contentRootDir) {
			validationOptions.setContentRoot(contentRootDir);
			return this;
		}

		@Override
		public ValidationOptionsSetter setDebugDir(Path debugDir) {
			validationOptions.setDebugDir(debugDir);
			return this;
		}
		
		public ValidationResult executeOn(Path template, Document data) throws FormsServiceException {
			try {
				return adobeFormsService.validate(template.toString(), data, validationOptions.build());
			} catch (FileNotFoundException e) {
				throw new FormsServiceException(e);
			}
		}
	}
	
	public class RenderPDFFormArgumentBuilderImpl implements RenderPDFFormArgumentBuilder {

		PDFFormRenderOptionsImpl options = new PDFFormRenderOptionsImpl();
		
		@Override
		public PDFFormRenderOptionsSetter setAcrobatVersion(AcrobatVersion acrobatVersion) {
			options.setAcrobatVersion(acrobatVersion);
			return this;
		}

		@Override
		public PDFFormRenderOptionsSetter setCacheStrategy(CacheStrategy strategy) {
			options.setCacheStrategy(strategy);
			return this;
		}

		@Override
		public PDFFormRenderOptionsSetter setContentRoot(Path url) {
			options.setContentRoot(url);
			return this;
		}

		@Override
		public PDFFormRenderOptionsSetter setDebugDir(Path debugDir) {
			options.setDebugDir(debugDir);
			return this;
		}

		@Override
		public PDFFormRenderOptionsSetter setLocale(Locale locale) {
			options.setLocale(locale);
			return this;
		}

		@Override
		public PDFFormRenderOptionsSetter setSubmitUrls(List<URL> urls) {
			options.setSubmitUrls(urls);
			return this;
		}

		@Override
		public PDFFormRenderOptionsSetter setTaggedPDF(boolean isTagged) {
			options.setTaggedPDF(isTagged);
			return this;
		}

		@Override
		public PDFFormRenderOptionsSetter setXci(Document xci) {
			options.setXci(xci);
			return this;
		}

		@Override
		public Document executeOn(Path template, Document data) throws FormsServiceException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	protected TraditionalFormsService getAdobeFormsService() {
		return adobeFormsService;
	}
}
