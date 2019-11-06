package com._4point.aem.fluentforms.api.output;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.Transformable;
import com.adobe.fd.output.api.AcrobatVersion;
import com.adobe.fd.output.api.PaginationOverride;

public interface OutputService {

	Document generatePDFOutput(Document template, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException;

	Document generatePDFOutput(Path templateFileName, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException, FileNotFoundException;

	Document generatePDFOutput(URL templateUrl, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException;

	Document generatePDFOutput(PathOrUrl urlOrFileName, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException, FileNotFoundException;
	
	GeneratePdfOutputArgumentBuilder generatePDFOutput();

	// TODO:  Generate overloaded methods
	BatchResult generatePDFOutputBatch(Map<String, PathOrUrl> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions) throws OutputServiceException;

	Document generatePrintedOutput(Document template, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException;

	Document generatePrintedOutput(Path templateFilename, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException, FileNotFoundException;

	Document generatePrintedOutput(URL templateUrl, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException;

	Document generatePrintedOutput(PathOrUrl urlOrFileName, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException, FileNotFoundException;

	GeneratePrintedOutputArgumentBuilder generatePrintedOutput();
	
	// TODO:  Generate overloaded methods
	BatchResult generatePrintedOutputBatch(Map<String, PathOrUrl> templates, Map<String, Document> data, PrintedOutputOptions printedOutputOptions, BatchOptions batchOptions) throws OutputServiceException;

	@SuppressWarnings("serial")
	public static class OutputServiceException extends Exception {

		public OutputServiceException() {
			super();
		}

		public OutputServiceException(String message, Throwable cause) {
			super(message, cause);
		}

		public OutputServiceException(String message) {
			super(message);
		}

		public OutputServiceException(Throwable cause) {
			super(cause);
		}
	}

	public static interface GeneratePdfOutputArgumentBuilder extends PDFOutputOptionsSetter, Transformable<GeneratePdfOutputArgumentBuilder> {

		@Override
		GeneratePdfOutputArgumentBuilder setAcrobatVersion(AcrobatVersion acrobatVersion);

		@Override
		GeneratePdfOutputArgumentBuilder setContentRoot(PathOrUrl contentRoot);

		@Override
		default GeneratePdfOutputArgumentBuilder setContentRoot(Path contentRoot) {
			PDFOutputOptionsSetter.super.setContentRoot(contentRoot);
			return this;
		}

		@Override
		default GeneratePdfOutputArgumentBuilder setContentRoot(URL contentRoot) {
			PDFOutputOptionsSetter.super.setContentRoot(contentRoot);
			return this;
		}

		@Override
		GeneratePdfOutputArgumentBuilder setDebugDir(Path debugDir);

		@Override
		GeneratePdfOutputArgumentBuilder setEmbedFonts(boolean embedFonts);

		@Override
		GeneratePdfOutputArgumentBuilder setLinearizedPDF(boolean linearizedPDF);

		@Override
		GeneratePdfOutputArgumentBuilder setLocale(Locale locale);

		@Override
		GeneratePdfOutputArgumentBuilder setRetainPDFFormState(boolean retainFormState);

		@Override
		GeneratePdfOutputArgumentBuilder setRetainUnsignedSignatureFields(boolean retainUnsignedSignatureFields);

		@Override
		GeneratePdfOutputArgumentBuilder setTaggedPDF(boolean isTagged);

		@Override
		GeneratePdfOutputArgumentBuilder setXci(Document xci);
		
		public Document executeOn(PathOrUrl template, Document data) throws OutputServiceException, FileNotFoundException;

		public Document executeOn(Path template, Document data) throws OutputServiceException, FileNotFoundException;
		
		public Document executeOn(URL template, Document data) throws OutputServiceException;
	}

	public static interface GeneratePrintedOutputArgumentBuilder extends PrintedOutputOptionsSetter, Transformable<GeneratePrintedOutputArgumentBuilder> {

		@Override
		GeneratePrintedOutputArgumentBuilder setContentRoot(PathOrUrl pathOrUrl);

		@Override
		default GeneratePrintedOutputArgumentBuilder setContentRoot(Path path) {
			PrintedOutputOptionsSetter.super.setContentRoot(path);
			return this;
		}

		@Override
		default GeneratePrintedOutputArgumentBuilder setContentRoot(URL url) {
			PrintedOutputOptionsSetter.super.setContentRoot(url);
			return this;
		}

		@Override
		GeneratePrintedOutputArgumentBuilder setCopies(int copies);

		@Override
		GeneratePrintedOutputArgumentBuilder setDebugDir(Path debugDir);

		@Override
		GeneratePrintedOutputArgumentBuilder setLocale(Locale locale);

		@Override
		GeneratePrintedOutputArgumentBuilder setPaginationOverride(PaginationOverride paginationOverride);

		@Override
		GeneratePrintedOutputArgumentBuilder setPrintConfig(PrintConfig printConfig);

		@Override
		GeneratePrintedOutputArgumentBuilder setXci(Document xci);
		
		public Document executeOn(PathOrUrl template, Document data) throws OutputServiceException, FileNotFoundException;

		public Document executeOn(Path template, Document data) throws OutputServiceException, FileNotFoundException;
		
		public Document executeOn(URL template, Document data) throws OutputServiceException;
	}

	public static interface GeneratePdfOutputBatchArgumentBuilder extends PDFOutputOptionsSetter, BatchArgumentBuilder, Transformable<GeneratePdfOutputArgumentBuilder> {

		@Override
		GeneratePdfOutputBatchArgumentBuilder setAcrobatVersion(AcrobatVersion acrobatVersion);

		@Override
		GeneratePdfOutputBatchArgumentBuilder setContentRoot(PathOrUrl contentRoot);

		@Override
		default GeneratePdfOutputBatchArgumentBuilder setContentRoot(Path contentRoot) {
			PDFOutputOptionsSetter.super.setContentRoot(contentRoot);
			return this;
		}

		@Override
		default GeneratePdfOutputBatchArgumentBuilder setContentRoot(URL contentRoot) {
			PDFOutputOptionsSetter.super.setContentRoot(contentRoot);
			return this;
		}

		@Override
		GeneratePdfOutputBatchArgumentBuilder setDebugDir(Path debugDir);

		@Override
		GeneratePdfOutputBatchArgumentBuilder setEmbedFonts(boolean embedFonts);

		@Override
		GeneratePdfOutputBatchArgumentBuilder setLinearizedPDF(boolean linearizedPDF);

		@Override
		GeneratePdfOutputBatchArgumentBuilder setLocale(Locale locale);

		@Override
		GeneratePdfOutputBatchArgumentBuilder setRetainPDFFormState(boolean retainFormState);

		@Override
		GeneratePdfOutputBatchArgumentBuilder setRetainUnsignedSignatureFields(boolean retainUnsignedSignatureFields);

		@Override
		GeneratePdfOutputBatchArgumentBuilder setTaggedPDF(boolean isTagged);

		@Override
		GeneratePdfOutputBatchArgumentBuilder setXci(Document xci);
		
		// TODO:  Fix up the executeOns to overload the options.
		public BatchResult executeOn(PathOrUrl template, Document data) throws OutputServiceException, FileNotFoundException;

		public BatchResult executeOn(Path template, Document data) throws OutputServiceException, FileNotFoundException;
		
		public BatchResult executeOn(URL template, Document data) throws OutputServiceException;
	}

	public static interface GeneratePrintedOutputBatchArgumentBuilder extends PrintedOutputOptionsSetter, BatchArgumentBuilder, Transformable<GeneratePrintedOutputArgumentBuilder> {

		@Override
		GeneratePrintedOutputBatchArgumentBuilder setContentRoot(PathOrUrl pathOrUrl);

		@Override
		default GeneratePrintedOutputBatchArgumentBuilder setContentRoot(Path path) {
			PrintedOutputOptionsSetter.super.setContentRoot(path);
			return this;
		}

		@Override
		default GeneratePrintedOutputBatchArgumentBuilder setContentRoot(URL url) {
			PrintedOutputOptionsSetter.super.setContentRoot(url);
			return this;
		}

		@Override
		GeneratePrintedOutputBatchArgumentBuilder setCopies(int copies);

		@Override
		GeneratePrintedOutputBatchArgumentBuilder setDebugDir(Path debugDir);

		@Override
		GeneratePrintedOutputBatchArgumentBuilder setLocale(Locale locale);

		@Override
		GeneratePrintedOutputBatchArgumentBuilder setPaginationOverride(PaginationOverride paginationOverride);

		@Override
		GeneratePrintedOutputBatchArgumentBuilder setPrintConfig(PrintConfig printConfig);

		@Override
		GeneratePrintedOutputBatchArgumentBuilder setXci(Document xci);
		
		// TODO:  Fix up the executeOns to overload the options.
		public BatchResult executeOn(PathOrUrl template, Document data) throws OutputServiceException, FileNotFoundException;

		public BatchResult executeOn(Path template, Document data) throws OutputServiceException, FileNotFoundException;
		
		public BatchResult executeOn(URL template, Document data) throws OutputServiceException;
	}

	public static interface BatchArgumentBuilder {
		public BatchArgumentBuilder addTemplate(PathOrUrl template);

		public default BatchArgumentBuilder addTemplate(Path template) {
			return this.addTemplate(new PathOrUrl(template));
		}

		public default BatchArgumentBuilder addTemplate(URL template) {
			return this.addTemplate(new PathOrUrl(template));
		}

		public BatchArgumentBuilder addTemplate(String templateName, PathOrUrl template);

		public BatchArgumentBuilder addTemplates(List<PathOrUrl> templates);

		public default BatchArgumentBuilder addTemplatePaths(List<Path> templates) {
			return this.addTemplates(templates.stream().map(PathOrUrl::new).collect(Collectors.toList()));
		}

		public default BatchArgumentBuilder addTemplateUrls(List<URL> templates) {
			return this.addTemplates(templates.stream().map(PathOrUrl::new).collect(Collectors.toList()));
		}

		public BatchArgumentBuilder addTemplateEntries(List<Map.Entry<String, PathOrUrl>> entries);

		public default BatchArgumentBuilder addTemplatePathEntries(List<Map.Entry<String, Path>> entries) {
			// This is a good candidate for moving into a private interface method when we move to Java 11
			return this.addTemplateEntries(
					entries.stream().map(
							(e)->new AbstractMap.SimpleEntry<String, PathOrUrl>(e.getKey(), new PathOrUrl(e.getValue()))
						).collect(Collectors.toList())
					);
		}

		public default BatchArgumentBuilder addTemplateUrlEntries(List<Map.Entry<String, URL>> entries) {
			// This is a duplicate of the previous method (except for arguments) so they can probably be consolidated
			// into a single private interface method.
			return this.addTemplateEntries(
					entries.stream().map(
							(e)->new AbstractMap.SimpleEntry<String, PathOrUrl>(e.getKey(), new PathOrUrl(e.getValue()))
						).collect(Collectors.toList())
					);
		}

		public BatchArgumentBuilder addData(Document data);

		public BatchArgumentBuilder addData(String dataName, Document data);

		public BatchArgumentBuilder addDataDocuments(List<Document> data);

		public BatchArgumentBuilder addData(List<Map.Entry<String, Document>> entries);

	}
}