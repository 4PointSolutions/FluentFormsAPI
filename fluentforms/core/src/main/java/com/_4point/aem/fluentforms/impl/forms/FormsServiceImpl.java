package com._4point.aem.fluentforms.impl.forms;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com._4point.aem.fluentforms.api.AbsoluteOrRelativeUrl;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.impl.UsageContext;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

public class FormsServiceImpl implements FormsService {
	
	private final TraditionalFormsService adobeFormsService;
	private final UsageContext usageContext;

	public FormsServiceImpl(TraditionalFormsService adobeFormsService, UsageContext usageContext) {
		super();
		this.adobeFormsService = new SafeFormsServiceAdapterWrapper(adobeFormsService);
		this.usageContext = usageContext;
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
			throws FormsServiceException, FileNotFoundException {
		Objects.requireNonNull(filename, "template cannot be null.");

		// Fix up the content root and filename.  If the filename has a directory in front, move it to the content root.
		String contentRoot = Objects.requireNonNull(pdfFormRenderOptions, "pdfFormRenderOptions cannot be null!").getContentRoot();
		TemplateValues tvs = TemplateValues.determineTemplateValues(filename, (contentRoot != null ? Paths.get(contentRoot) : null), this.usageContext);
		
		Path finalContentRoot = tvs.getContentRoot();
		pdfFormRenderOptions.setContentRoot(finalContentRoot != null ? finalContentRoot.toString() : null);
		return this.renderPDFForm(tvs.getTemplate().toString(), data, pdfFormRenderOptions);
	}

	@Override
	public Document renderPDFForm(URL url, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		Objects.requireNonNull(url, "url cannot be null.");
		return this.renderPDFForm(url.toString(), data, pdfFormRenderOptions);
	}

	@Override
	public Document renderPDFForm(PathOrUrl template, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException, FileNotFoundException {
		if (template.isPath())
			return renderPDFForm(template.getPath(), data, pdfFormRenderOptions);
		else if (template.isUrl()) {
			return renderPDFForm(template.getUrl(), data, pdfFormRenderOptions);
		} else {
			// This should never be thrown.
			throw new IllegalArgumentException("Template must be either Path or URL. (This should never be thrown.)");
		}
	}

	private Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		return adobeFormsService.renderPDFForm(urlOrfilename, data, pdfFormRenderOptions);
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
		public RenderPDFFormArgumentBuilder setContentRoot(Path filePath) {
			options.setContentRoot(filePath);
			return this;
		}
		
		@Override
		public RenderPDFFormArgumentBuilder setContentRoot(URL url) {
			options.setContentRoot(url);
			return this;
		}

		@Override
		public RenderPDFFormArgumentBuilder setDebugDir(Path debugDir) {
			options.setDebugDir(debugDir);
			return this;
		}

		@Override
		public RenderPDFFormArgumentBuilder setLocale(Locale locale) {
			options.setLocale(locale);
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
			if (template.isPath())
				return renderPDFForm(template.getPath(), data, options.toAdobePDFFormRenderOptions());
			else if (template.isUrl()) {
				return renderPDFForm(template.getUrl(), data, options.toAdobePDFFormRenderOptions());
			} else {
				// This should never be thrown.
				throw new IllegalArgumentException("Template must be either Path or URL. (This should never be thrown.)");
			}
		}
		
		@Override
		public Document executeOn(Path template, Document data) throws FormsServiceException, FileNotFoundException {
			return renderPDFForm(template, data, options.toAdobePDFFormRenderOptions());
		}
		
		@Override
		public Document executeOn(URL template, Document data) throws FormsServiceException {
			return renderPDFForm(template, data, options.toAdobePDFFormRenderOptions());
		}
	}

	protected TraditionalFormsService getAdobeFormsService() {
		return adobeFormsService;
	}
	
	// This class is public so that we can run unit tests against it directly.
	public static class TemplateValues {
		private final Path contentRoot;
		private final Path template;
		
		private TemplateValues(String contentRoot, String template) {
			super();
			this.contentRoot = contentRoot != null ? Paths.get(contentRoot) : null;
			this.template = Paths.get(template);
		}

		// Move any parent on the template to the provided content root (i.e. templates dir).  This is because all fragments in a template
		// are relative to that template in Designer but relative to the content root when rendering.  We need to make sure the content root
		// points to the directory where the template resides so that fragments are found.
		public static TemplateValues determineTemplateValues(Path template, Path templatesDir, UsageContext usageContext) throws FileNotFoundException {
			Path templateParentDir = template.getParent();
			String contentRoot;
			if (templatesDir == null && templateParentDir != null) {
				// No templatesDir but there's a parent dir on the template
				// use the parent dir as the content root
				contentRoot = templateParentDir.toString();
			} else if (templatesDir != null && templateParentDir == null) {
				// There's a templatesDir but no parent dir on the template
				// so just use the templatesDir as the content root
				contentRoot = templatesDir.toString();
			} else if (templatesDir != null && templateParentDir != null) {
				// There's a templatesDir and there's a parent dir on the template
				// append the parent dir onto the templates dir to create the content root
				contentRoot = templatesDir.resolve(templateParentDir).toString();
			} else {	// templatesDir == null && templateParentDir == null
				// No templatesDir and no parent dir on the template, so no content root
				contentRoot = null;
			}
			String templateFileName = template.getFileName().toString();
			Path formsPath = contentRoot != null ? Paths.get(contentRoot, templateFileName) : Paths.get(templateFileName);
			if (usageContext == UsageContext.SERVER_SIDE && (Files.notExists(formsPath) || !Files.isRegularFile(formsPath))) {
				throw new FileNotFoundException("Unable to find template (" + formsPath.toString() + ").");
			}
			return new TemplateValues(contentRoot, templateFileName);
		}

		public Path getContentRoot() {
			return contentRoot;
		}

		public Path getTemplate() {
			return template;
		}
	}

}
