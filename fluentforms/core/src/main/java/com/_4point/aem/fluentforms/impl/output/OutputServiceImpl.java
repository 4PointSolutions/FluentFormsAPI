package com._4point.aem.fluentforms.impl.output;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintConfig;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.impl.TemplateValues;
import com._4point.aem.fluentforms.impl.UsageContext;
import com.adobe.fd.output.api.AcrobatVersion;
import com.adobe.fd.output.api.PaginationOverride;

/**
 * Output Service implementation.
 * 
 * Batch methods are not implemented at this time.
 *
 */
public class OutputServiceImpl implements OutputService {

	private final TraditionalOutputService adobeOutputService;
	private final UsageContext usageContext;

	public OutputServiceImpl(TraditionalOutputService adobeOutputService, UsageContext usageContext) {
		super();
		this.adobeOutputService = new SafeOutputServiceAdapterWrapper(adobeOutputService);
		this.usageContext = usageContext;
	}

	@Override
	public Document generatePDFOutput(Document templateDoc, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		return adobeOutputService.generatePDFOutput(templateDoc, data, pdfOutputOptions);
	}

	@Override
	public Document generatePDFOutput(Path filename, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException, FileNotFoundException {
		Objects.requireNonNull(filename, "template cannot be null.");
		return this.generatePDFOutput(PathOrUrl.from(filename), data, pdfOutputOptions);
	}

	@Override
	public Document generatePDFOutput(URL url, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		Objects.requireNonNull(url, "url cannot be null.");
		try {
			return this.generatePDFOutput(PathOrUrl.from(url), data, pdfOutputOptions);
		} catch (FileNotFoundException e) {
			// This should never happen because the exception is only thrown for Path objects.
			throw new IllegalStateException("determineTemplateValues threw FileNotFoundException for URL.");
		}
	}

	@Override
	public Document generatePDFOutput(PathOrUrl template, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException, FileNotFoundException {
		Objects.requireNonNull(template, "template cannot be null.");
		// Fix up the content root and filename.  If the filename has a directory in front, move it to the content root.
		PathOrUrl contentRoot = Objects.requireNonNull(pdfOutputOptions, "pdfOutputOptions cannot be null!").getContentRoot();
		Optional<TemplateValues> otvs = TemplateValues.determineTemplateValues(template, contentRoot, this.usageContext);
		if (otvs.isPresent()) {
			TemplateValues tvs = otvs.get();
			template = PathOrUrl.from(tvs.getTemplate());
			pdfOutputOptions.setContentRoot(tvs.getContentRoot());
		}
		return internalGeneratePDFOutput(template.toString(), data, pdfOutputOptions);
	}

	private Document internalGeneratePDFOutput(String urlOrFileName, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		return adobeOutputService.generatePDFOutput(urlOrFileName, data, pdfOutputOptions);
	}

	@Override
	public GeneratePdfOutputArgumentBuilder generatePDFOutput() {
		return new GeneratePdfOutputArgumentBuilderImpl();
	}

	@Override
	public BatchResult generatePDFOutputBatch(Map<String, PathOrUrl> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Document generatePrintedOutput(Document templateDoc, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		return this.adobeOutputService.generatePrintedOutput(templateDoc, data, printedOutputOptions);
	}

	@Override
	public Document generatePrintedOutput(Path templateFilename, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException, FileNotFoundException {
		Objects.requireNonNull(templateFilename, "template cannot be null.");

		// Fix up the content root and filename.  If the filename has a directory in front, move it to the content root.
		PathOrUrl contentRoot = Objects.requireNonNull(printedOutputOptions, "pdfOutputOptions cannot be null!").getContentRoot();
//		if (contentRoot != null && !contentRoot.isPath()) {
//			throw new FormsServiceException("Content Root must be Path object if template is a Path. contentRoot='" + contentRoot.toString() + "', template='" + filename + "'.");
//		}
		TemplateValues tvs = TemplateValues.determineTemplateValues(PathOrUrl.from(templateFilename), contentRoot, this.usageContext).get();
		
		PathOrUrl finalContentRoot = tvs.getContentRoot();
		printedOutputOptions.setContentRoot(finalContentRoot != null ? finalContentRoot : null);
		return this.generatePrintedOutput(tvs.getTemplate().toString(), data, printedOutputOptions);
	}

	@Override
	public Document generatePrintedOutput(URL templateUrl, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		Objects.requireNonNull(templateUrl, "url cannot be null.");
		return this.generatePrintedOutput(templateUrl.toString(), data, printedOutputOptions);
	}

	@Override
	public Document generatePrintedOutput(PathOrUrl template, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException, FileNotFoundException {
		if (template.isPath()) {
			return generatePrintedOutput(template.getPath(), data, printedOutputOptions);
		} else if (template.isUrl()) {
			return generatePrintedOutput(template.getUrl(), data, printedOutputOptions);
		} else if (template.isCrxUrl()) {
			return generatePrintedOutput(template.getCrxUrl(), data, printedOutputOptions);
		} else {
			// This should never be thrown.
			throw new IllegalArgumentException("Template must be either Path or URL. (This should never be thrown.)");
		}
	}

	private Document generatePrintedOutput(String urlOrFileName, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		return this.adobeOutputService.generatePrintedOutput(urlOrFileName, data, printedOutputOptions);
	}

	@Override
	public GeneratePrintedOutputArgumentBuilder generatePrintedOutput() {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException("Not implemented yet.");
		return new GeneratePrintedOutputArgumentBuilderImpl();
	}

	@Override
	public BatchResult generatePrintedOutputBatch(Map<String, PathOrUrl> templates, Map<String, Document> data, PrintedOutputOptions printedOutputOptions,
			BatchOptions batchOptions) throws OutputServiceException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	protected TraditionalOutputService getAdobeOutputService() {
		return adobeOutputService;
	}

	private class GeneratePdfOutputArgumentBuilderImpl implements GeneratePdfOutputArgumentBuilder {

		PDFOutputOptions pdfOutputOptions = new PDFOutputOptionsImpl();

		@Override
		public GeneratePdfOutputArgumentBuilder setAcrobatVersion(AcrobatVersion acrobatVersion) {
			this.pdfOutputOptions.setAcrobatVersion(acrobatVersion);
			return this;
		}

		@Override
		public GeneratePdfOutputArgumentBuilder setContentRoot(PathOrUrl contentRoot) {
			this.pdfOutputOptions.setContentRoot(contentRoot);
			return this;
		}

		@Override
		public GeneratePdfOutputArgumentBuilder setDebugDir(Path debugDir) {
			this.pdfOutputOptions.setDebugDir(debugDir);
			return this;
		}

		@Override
		public GeneratePdfOutputArgumentBuilder setEmbedFonts(boolean embedFonts) {
			this.pdfOutputOptions.setEmbedFonts(embedFonts);
			return this;
		}

		@Override
		public GeneratePdfOutputArgumentBuilder setLinearizedPDF(boolean linearizedPDF) {
			this.pdfOutputOptions.setLinearizedPDF(linearizedPDF);
			return this;
		}

		@Override
		public GeneratePdfOutputArgumentBuilder setLocale(Locale locale) {
			this.pdfOutputOptions.setLocale(locale);
			return this;
		}

		@Override
		public GeneratePdfOutputArgumentBuilder setRetainPDFFormState(boolean retainFormState) {
			this.pdfOutputOptions.setRetainPDFFormState(retainFormState);
			return this;
		}

		@Override
		public GeneratePdfOutputArgumentBuilder setRetainUnsignedSignatureFields(boolean retainUnsignedSignatureFields) {
			this.pdfOutputOptions.setRetainUnsignedSignatureFields(retainUnsignedSignatureFields);
			return this;
		}

		@Override
		public GeneratePdfOutputArgumentBuilder setTaggedPDF(boolean isTagged) {
			this.pdfOutputOptions.setTaggedPDF(isTagged);
			return this;
		}

		@Override
		public GeneratePdfOutputArgumentBuilder setXci(Document xci) {
			this.pdfOutputOptions.setXci(xci);
			return this;
		}

		@Override
		public Document executeOn(PathOrUrl template, Document data) throws OutputServiceException, FileNotFoundException {
			return generatePDFOutput(template, data, this.pdfOutputOptions);
		}

		@Override
		public Document executeOn(Path template, Document data) throws OutputServiceException, FileNotFoundException {
			return generatePDFOutput(template, data, this.pdfOutputOptions);
		}

		@Override
		public Document executeOn(URL template, Document data) throws OutputServiceException {
			return generatePDFOutput(template, data, this.pdfOutputOptions);
		}
		
		@Override
		public Document executeOn(Document template, Document data) throws OutputServiceException {
			return generatePDFOutput(template, data, this.pdfOutputOptions);
		}
	}

	private class GeneratePrintedOutputArgumentBuilderImpl implements GeneratePrintedOutputArgumentBuilder {

		PrintedOutputOptions printedOutputOptions = new PrintedOutputOptionsImpl();

		@Override
		public GeneratePrintedOutputArgumentBuilder setContentRoot(PathOrUrl pathOrUrl) {
			this.printedOutputOptions.setContentRoot(pathOrUrl);
			return this;
		}

		@Override
		public GeneratePrintedOutputArgumentBuilder setCopies(int copies) {
			this.printedOutputOptions.setCopies(copies);
			return this;
		}

		@Override
		public GeneratePrintedOutputArgumentBuilder setDebugDir(Path debugDir) {
			this.printedOutputOptions.setDebugDir(debugDir);
			return this;
		}

		@Override
		public GeneratePrintedOutputArgumentBuilder setLocale(Locale locale) {
			this.printedOutputOptions.setLocale(locale);
			return this;
		}

		@Override
		public GeneratePrintedOutputArgumentBuilder setPaginationOverride(PaginationOverride paginationOverride) {
			this.printedOutputOptions.setPaginationOverride(paginationOverride);
			return this;
		}

		@Override
		public GeneratePrintedOutputArgumentBuilder setPrintConfig(PrintConfig printConfig) {
			this.printedOutputOptions.setPrintConfig(printConfig);
			return this;
		}

		@Override
		public GeneratePrintedOutputArgumentBuilder setXci(Document xci) {
			this.printedOutputOptions.setXci(xci);
			return this;
		}

		@Override
		public Document executeOn(PathOrUrl template, Document data)
				throws OutputServiceException, FileNotFoundException {
			return generatePrintedOutput(template, data, this.printedOutputOptions);
		}

		@Override
		public Document executeOn(Path template, Document data) throws OutputServiceException, FileNotFoundException {
			return generatePrintedOutput(template, data, this.printedOutputOptions);
		}

		@Override
		public Document executeOn(URL template, Document data) throws OutputServiceException {
			return generatePrintedOutput(template, data, this.printedOutputOptions);
		}

	}


	/**
	 * This class could (and should) be replaced by private methods in the OutputService.BatchArgumentBuilder interface
	 * however that would require Java 11 and for now we're stuck in Java 8 land.  Hopefully someone will move this class'
	 * code into that interface after the project has dropped Java 8 support.
	 */
	private static abstract class AbstractBatchArgumentBuilder implements OutputService.BatchArgumentBuilder {
		List<Entry<String, PathOrUrl>> templates = new ArrayList<>();
		List<Entry<String, Document>> dataDocs = new ArrayList<>();
		
		@Override
		public BatchArgumentBuilder addTemplate(PathOrUrl template) {
			// TODO Auto-generated method stub
			// Call getName(template) to get name and then add both to the list of templates
			throw new UnsupportedOperationException("Not implemented yet.");
		}

		@Override
		public BatchArgumentBuilder addTemplate(String templateName, PathOrUrl template) {
			// TODO Auto-generated method stub
			// Call getName(template) to get name and then add both to the list of templates
			throw new UnsupportedOperationException("Not implemented yet.");
		}

		@Override
		public BatchArgumentBuilder addTemplates(List<PathOrUrl> templates) {
			// TODO Auto-generated method stub
			// Call getName(template) to get name and then add both to the list of templates
			throw new UnsupportedOperationException("Not implemented yet.");
		}

		@Override
		public BatchArgumentBuilder addTemplateEntries(List<Entry<String, PathOrUrl>> entries) {
			templates.addAll(Objects.requireNonNull(entries, "List of Entries parameter cannot be null."));
			return this;
		}

		@Override
		public BatchArgumentBuilder addData(Document data) {
			// TODO Auto-generated method stub
			// Call getName(template) to get name and then add both to the list of dataDocs
			throw new UnsupportedOperationException("Not implemented yet.");
		}

		@Override
		public BatchArgumentBuilder addData(String dataName, Document data) {
			// TODO Auto-generated method stub
			// Call getName(template) to get name and then add both to the list of dataDocs
			throw new UnsupportedOperationException("Not implemented yet.");
		}

		@Override
		public BatchArgumentBuilder addDataDocuments(List<Document> data) {
			// TODO Auto-generated method stub
			// Call getName(template) to get name and then add both to the list of dataDocs
			throw new UnsupportedOperationException("Not implemented yet.");
		}

		@Override
		public BatchArgumentBuilder addData(List<Entry<String, Document>> entries) {
			dataDocs.addAll(Objects.requireNonNull(entries, "List of Entries parameter cannot be null."));
			return this;
		}
		
		private static String getName(PathOrUrl location) {
			// TODO: Implement getName() for PathOrUrl
			throw new UnsupportedOperationException("Not implemented yet.");
		}
		
		private static String getName(Document doc) {
			// TODO: Implement getName() for Document objects
			throw new UnsupportedOperationException("Not implemented yet.");
		}

	}
}
