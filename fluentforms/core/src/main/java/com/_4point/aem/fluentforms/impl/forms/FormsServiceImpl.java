package com._4point.aem.fluentforms.impl.forms;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com._4point.aem.fluentforms.api.AbsoluteOrRelativeUrl;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.Xci;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.impl.TemplateValues;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.XciImpl;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.RenderAtClient;

public class FormsServiceImpl implements FormsService {
	
	private final TraditionalFormsService adobeFormsService;
	private final UsageContext usageContext;

	public FormsServiceImpl(TraditionalFormsService adobeFormsService, UsageContext usageContext) {
		super();
		this.adobeFormsService = new SafeFormsServiceAdapterWrapper(adobeFormsService);
		this.usageContext = usageContext;
	}

	@Override
	public Optional<Document> exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException {
		Document result = adobeFormsService.exportData(pdfOrXdp, dataFormat);
		return result.isEmpty() ? Optional.empty() : Optional.of(result);	
	}

	@Override
	public Document importData(Document pdf, Document data) throws FormsServiceException {
		return adobeFormsService.importData(pdf, data);
	}

	@Override
	public Document renderPDFForm(Path filename, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException, FileNotFoundException {
		Objects.requireNonNull(filename, "template cannot be null.");

		return this.renderPDFForm(PathOrUrl.from(filename), data, pdfFormRenderOptions);
	}

	@Override
	public Document renderPDFForm(URL url, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		Objects.requireNonNull(url, "url cannot be null.");
		try {
			return this.renderPDFForm(PathOrUrl.from(url), data, pdfFormRenderOptions);
		} catch (FileNotFoundException e) {
			// This should never happen because the exception is only thrown for Path objects.
			throw new IllegalStateException("determineTemplateValues threw FileNotFoundException for URL.");
		}
	}

	@Override
	public Document renderPDFForm(PathOrUrl template, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException, FileNotFoundException {
		// Fix up the content root and filename.  If the filename has a directory in front, move it to the content root.
		PathOrUrl contentRoot = Objects.requireNonNull(pdfFormRenderOptions, "pdfFormRenderOptions cannot be null!").getContentRoot();
		Optional<TemplateValues> otvs = TemplateValues.determineTemplateValues(template, contentRoot, this.usageContext);
		if (otvs.isPresent()) {
			TemplateValues tvs = otvs.get();
			template = PathOrUrl.from(tvs.getTemplate());
			pdfFormRenderOptions.setContentRoot(tvs.getContentRoot());
		}
		return internalRenderPDFForm(template.toString(), data, pdfFormRenderOptions);
	}

	@Override
	public Document renderPDFForm(Document template, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		Objects.requireNonNull(template, "template Document cannot be null.");
		return this.internalRenderPDFForm(template, data, pdfFormRenderOptions);
	}

	private Document internalRenderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		return adobeFormsService.renderPDFForm(urlOrfilename, data, pdfFormRenderOptions);
	}

	private Document internalRenderPDFForm(Document template, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		return adobeFormsService.renderPDFForm(template, data, pdfFormRenderOptions);
	}

	@Override
	public RenderPDFFormArgumentBuilder renderPDFForm() {
		return new RenderPDFFormArgumentBuilderImpl();
	}

	@Override
	public ValidationResult validate(Path template, Document data, ValidationOptions validationOptions)
			throws FormsServiceException, FileNotFoundException {
		validateTemplatePath(template);
		return adobeFormsService.validate(template.toString(), data, validationOptions);
	}

	@Override
	public ValidateArgumentBuilderImpl validate() {
		return new ValidateArgumentBuilderImpl();
	}


	private void validateTemplatePath(Path filename) throws FormsServiceException, FileNotFoundException {
		Objects.requireNonNull(filename, "template cannot be null.");
		if (this.usageContext == UsageContext.SERVER_SIDE && !(Files.exists(filename) && Files.isRegularFile(filename))) {
			String message = "Unable to find template (" + filename.toString() + ").";
			throw new FileNotFoundException(message);
		}
	}

	public class ValidateArgumentBuilderImpl implements ValidateArgumentBuilder {
		ValidationOptionsBuilder validationOptions = new ValidationOptionsBuilder();
		
		@Override
		public ValidateArgumentBuilder setContentRoot(Path contentRootDir) {
			validationOptions.setContentRoot(contentRootDir);
			return this;
		}

		@Override
		public ValidateArgumentBuilder setDebugDir(Path debugDir) {
			validationOptions.setDebugDir(debugDir);
			return this;
		}
		
		@Override
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
		public RenderPDFFormArgumentBuilder setAcrobatVersion(AcrobatVersion acrobatVersion) {
			options.setAcrobatVersion(acrobatVersion);
			return this;
		}

		@Override
		public RenderPDFFormArgumentBuilder setCacheStrategy(CacheStrategy strategy) {
			options.setCacheStrategy(strategy);
			return this;
		}

		@Override
		public RenderPDFFormArgumentBuilder setContentRoot(PathOrUrl pathOrUrl) {
			options.setContentRoot(pathOrUrl);
			return this;
		}
		
		@Override
		public RenderPDFFormArgumentBuilder setDebugDir(Path debugDir) {
			options.setDebugDir(debugDir);
			return this;
		}

		@Override
		public RenderPDFFormArgumentBuilder setEmbedFonts(boolean embedFonts) {
			options.setEmbedFonts(embedFonts);
			return this;
		}

		@Override
		public RenderPDFFormArgumentBuilder setLocale(Locale locale) {
			options.setLocale(locale);
			return this;
		}

		@Override
		public RenderPDFFormArgumentBuilder setRenderAtClient(RenderAtClient renderAtClient) {
			options.setRenderAtClient(renderAtClient);
			return this;
		}
		
		@Override
		public RenderPDFFormArgumentBuilder setSubmitUrls(List<AbsoluteOrRelativeUrl> urls) {
			options.setSubmitUrls(urls);
			return this;
		}

		@Override
		public RenderPDFFormArgumentBuilder setTaggedPDF(boolean isTagged) {
			options.setTaggedPDF(isTagged);
			return this;
		}

		@Override
		public RenderPDFFormArgumentBuilder setXci(Document xci) {
			options.setXci(xci);
			return this;
		}

		@Override
		public Document executeOn(PathOrUrl template, Document data) throws FormsServiceException, FileNotFoundException {
			if (template.isPath()) {
				return renderPDFForm(template.getPath(), data, options);
			} else if (template.isUrl()) {
				return renderPDFForm(template.getUrl(), data, options);
			} else if (template.isCrxUrl()) {
				return internalRenderPDFForm(template.getCrxUrl(), data, options);
			} else {
				// This should never be thrown.
				throw new IllegalArgumentException("Template must be either Path or URL. (This should never be thrown.)");
			}
		}
		
		@Override
		public Document executeOn(Path template, Document data) throws FormsServiceException, FileNotFoundException {
			return renderPDFForm(template, data, options);
		}
		
		@Override
		public Document executeOn(URL template, Document data) throws FormsServiceException {
			return renderPDFForm(template, data, options);
		}
		
		@Override
		public Document executeOn(Document template, Document data) throws FormsServiceException {
			return renderPDFForm(template, data, options);
		}

		@Override
		public XciArgumentBuilder xci() {
			return new XciArgumentBuilderImpl();
		}
		
		private class XciArgumentBuilderImpl implements XciArgumentBuilder {
			private final Xci.XciBuilder xciBuilder = new XciImpl.XciBuilderImpl();

			@Override
			public XciArgumentBuilder embedFonts(boolean embedFonts) {
				xciBuilder.pdf().embedFonts(embedFonts);
				return this;
			}

			@Override
			public RenderPDFFormArgumentBuilder done() {
				RenderPDFFormArgumentBuilderImpl.this.setXci(xciBuilder.build());
				return RenderPDFFormArgumentBuilderImpl.this;
			}
		}
	}

	protected TraditionalFormsService getAdobeFormsService() {
		return adobeFormsService;
	}
	
}
