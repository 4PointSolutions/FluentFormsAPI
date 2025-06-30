package com._4point.aem.fluentforms.api.output;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.Transformable;
import com._4point.aem.fluentforms.api.Xci;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
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

	/**
	 * @deprecated Use {@link #generatePrintedOutput(PrintConfig)} instead.
	 * 
	 * @return
	 */
	@Deprecated
	GeneratePrintedOutputArgumentBuilder generatePrintedOutput();
	
	GeneratePrintedOutputArgumentBuilder2 generatePrintedOutput(PrintConfig printConfig);
	
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
		
		@Override
		default GeneratePdfOutputArgumentBuilder setXci(Xci xci) {
			PDFOutputOptionsSetter.super.setXci(xci);
			return this;
		}

		public XciArgumentBuilder xci();

		public Document executeOn(PathOrUrl template, Document data) throws OutputServiceException, FileNotFoundException;

		public Document executeOn(Path template, Document data) throws OutputServiceException, FileNotFoundException;
		
		public Document executeOn(URL template, Document data) throws OutputServiceException;

		public Document executeOn(Document template, Document data) throws OutputServiceException;
		
		default public Document executeOn(PathOrUrl template, byte[] data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Path template, byte[] data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};
		
		default public Document executeOn(URL template, byte[] data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Document template, byte[] data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(byte[] template, byte[] data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};

		default public Document executeOn(InputStream template, byte[] data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};

		default public Document executeOn(PathOrUrl template, InputStream data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Path template, InputStream data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};
		
		default public Document executeOn(URL template, InputStream data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Document template, InputStream data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(byte[] template, InputStream data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};

		default public Document executeOn(InputStream template, InputStream data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};
		
		default public Document executeOn(PathOrUrl template) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, (Document)null);
		};

		default public Document executeOn(Path template) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, (Document)null);
		};
		
		default public Document executeOn(URL template) throws OutputServiceException {
			return executeOn(template, (Document)null);
		};

		default public Document executeOn(Document template) throws OutputServiceException {
			return executeOn(template, (Document)null);
		};
		
		public interface XciArgumentBuilder {
			XciArgumentBuilder embedFonts(boolean embedFonts);
			GeneratePdfOutputArgumentBuilder done();
		}
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

		@Override
		default GeneratePrintedOutputArgumentBuilder setXci(Xci xci) {
            PrintedOutputOptionsSetter.super.setXci(xci);
            return this;
        }
		
		public XciArgumentBuilder xci();

		/**
		 * Merges the provided template with the provided data and returns the generated
		 * output.
		 * 
		 * @param template The template to merge data into.
		 * @param data     The data to merge with the template.
		 * @return The generated output document.
		 * @throws OutputServiceException If an error occurs during processing.
		 * @throws FileNotFoundException  If the template file is not found.
		 */
		public Document executeOn(PathOrUrl template, Document data) throws OutputServiceException, FileNotFoundException;

		/**
		 * Merges the provided template with the provided data and returns the generated
		 * output.
		 * 
		 * @param template The template to merge data into.
		 * @param data     The data to merge with the template.
		 * @return The generated output document.
		 * @throws OutputServiceException If an error occurs during processing.
		 * @throws FileNotFoundException  If the template file is not found.
		 */
		public Document executeOn(Path template, Document data) throws OutputServiceException, FileNotFoundException;
		
		/**
		 * Merges the provided template with the provided data and returns the generated
		 * output.
		 * 
		 * @param template The template to merge data into.
		 * @param data     The data to merge with the template.
		 * @return The generated output document.
		 * @throws OutputServiceException If an error occurs during processing.
		 */
		public Document executeOn(URL template, Document data) throws OutputServiceException;

		/**
		 * Merges the provided template with the provided data and returns the generated
		 * output.
		 * 
		 * @param template The template to merge data into.
		 * @param data     The data to merge with the template.
		 * @return The generated output document.
		 * @throws FileNotFoundException  If the template file is not found.
		 */
		public Document executeOn(Document template, Document data) throws OutputServiceException;
		
		default public Document executeOn(PathOrUrl template, byte[] data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Path template, byte[] data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};
		
		default public Document executeOn(URL template, byte[] data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Document template, byte[] data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(byte[] template, byte[] data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};

		default public Document executeOn(InputStream template, byte[] data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};

		default public Document executeOn(PathOrUrl template, InputStream data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Path template, InputStream data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};
		
		default public Document executeOn(URL template, InputStream data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Document template, InputStream data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(byte[] template, InputStream data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};

		default public Document executeOn(InputStream template, InputStream data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};

		default public Document executeOn(PathOrUrl template) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, (Document)null);
		};

		default public Document executeOn(Path template) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, (Document)null);
		};
		
		default public Document executeOn(URL template) throws OutputServiceException {
			return executeOn(template, (Document)null);
		};

		default public Document executeOn(Document template) throws OutputServiceException {
			return executeOn(template, (Document)null);
		};

		public interface XciArgumentBuilder {
			XciArgumentBuilder embedPclFonts(boolean embedFonts);
			XciArgumentBuilder embedPsFonts(boolean embedFonts);
			GeneratePrintedOutputArgumentBuilder done();
		}
	}

	public static interface GeneratePrintedOutputArgumentBuilder2 extends PrintedOutputOptionsSetter2, Transformable<GeneratePrintedOutputArgumentBuilder> {

		@Override
		GeneratePrintedOutputArgumentBuilder2 setContentRoot(PathOrUrl pathOrUrl);

		@Override
		default GeneratePrintedOutputArgumentBuilder2 setContentRoot(Path path) {
			PrintedOutputOptionsSetter2.super.setContentRoot(path);
			return this;
		}

		@Override
		default GeneratePrintedOutputArgumentBuilder2 setContentRoot(URL url) {
			PrintedOutputOptionsSetter2.super.setContentRoot(url);
			return this;
		}

		@Override
		GeneratePrintedOutputArgumentBuilder2 setCopies(int copies);

		@Override
		GeneratePrintedOutputArgumentBuilder2 setDebugDir(Path debugDir);

		@Override
		GeneratePrintedOutputArgumentBuilder2 setLocale(Locale locale);

		@Override
		GeneratePrintedOutputArgumentBuilder2 setPaginationOverride(PaginationOverride paginationOverride);

		@Override
		GeneratePrintedOutputArgumentBuilder2 setXci(Document xci);

		@Override
		default GeneratePrintedOutputArgumentBuilder2 setXci(Xci xci) {
			PrintedOutputOptionsSetter2.super.setXci(xci);
            return this;
        }
		
		public XciArgumentBuilder xci();

		/**
		 * Merges the provided template with the provided data and returns the generated
		 * output.
		 * 
		 * @param template The template to merge data into.
		 * @param data     The data to merge with the template.
		 * @return The generated output document.
		 * @throws OutputServiceException If an error occurs during processing.
		 * @throws FileNotFoundException  If the template file is not found.
		 */
		public Document executeOn(PathOrUrl template, Document data) throws OutputServiceException, FileNotFoundException;

		/**
		 * Merges the provided template with the provided data and returns the generated
		 * output.
		 * 
		 * @param template The template to merge data into.
		 * @param data     The data to merge with the template.
		 * @return The generated output document.
		 * @throws OutputServiceException If an error occurs during processing.
		 * @throws FileNotFoundException  If the template file is not found.
		 */
		public Document executeOn(Path template, Document data) throws OutputServiceException, FileNotFoundException;
		
		/**
		 * Merges the provided template with the provided data and returns the generated
		 * output.
		 * 
		 * @param template The template to merge data into.
		 * @param data     The data to merge with the template.
		 * @return The generated output document.
		 * @throws OutputServiceException If an error occurs during processing.
		 */
		public Document executeOn(URL template, Document data) throws OutputServiceException;

		/**
		 * Merges the provided template with the provided data and returns the generated
		 * output.
		 * 
		 * @param template The template to merge data into.
		 * @param data     The data to merge with the template.
		 * @return The generated output document.
		 * @throws FileNotFoundException  If the template file is not found.
		 */
		public Document executeOn(Document template, Document data) throws OutputServiceException;
		
		default public Document executeOn(PathOrUrl template, byte[] data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Path template, byte[] data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};
		
		default public Document executeOn(URL template, byte[] data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Document template, byte[] data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(byte[] template, byte[] data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};

		default public Document executeOn(InputStream template, byte[] data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};

		default public Document executeOn(PathOrUrl template, InputStream data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Path template, InputStream data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};
		
		default public Document executeOn(URL template, InputStream data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(Document template, InputStream data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public Document executeOn(byte[] template, InputStream data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};

		default public Document executeOn(InputStream template, InputStream data) throws OutputServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(template), factory.create(data));
		};

		default public Document executeOn(PathOrUrl template) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, (Document)null);
		};

		default public Document executeOn(Path template) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, (Document)null);
		};
		
		default public Document executeOn(URL template) throws OutputServiceException {
			return executeOn(template, (Document)null);
		};

		default public Document executeOn(Document template) throws OutputServiceException {
			return executeOn(template, (Document)null);
		};

		public interface XciArgumentBuilder {
			XciArgumentBuilder embedPclFonts(boolean embedFonts);
			XciArgumentBuilder embedPsFonts(boolean embedFonts);
			GeneratePrintedOutputArgumentBuilder2 done();
		}
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
		
		@Override
		default GeneratePdfOutputBatchArgumentBuilder setXci(Xci xci) {
			PDFOutputOptionsSetter.super.setXci(xci);
			return this;
		}

		public XciArgumentBuilder xci();

	// TODO:  Fix up the executeOns to overload the options.
		public BatchResult executeOn(PathOrUrl template, Document data) throws OutputServiceException, FileNotFoundException;

		public BatchResult executeOn(Path template, Document data) throws OutputServiceException, FileNotFoundException;
		
		public BatchResult executeOn(URL template, Document data) throws OutputServiceException;
		
		default public BatchResult executeOn(PathOrUrl template, byte[] data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public BatchResult executeOn(Path template, byte[] data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};
		
		default public BatchResult executeOn(URL template, byte[] data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public BatchResult executeOn(PathOrUrl template, InputStream data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public BatchResult executeOn(Path template, InputStream data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};
		
		default public BatchResult executeOn(URL template, InputStream data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public BatchResult executeOn(PathOrUrl template) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, (Document) null);
		};

		default public BatchResult executeOn(Path template) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, (Document) null);
		};
		
		default public BatchResult executeOn(URL template) throws OutputServiceException {
			return executeOn(template, (Document) null);
		};

		public interface XciArgumentBuilder {
			XciArgumentBuilder embedFonts(boolean embedFonts);
			GeneratePdfOutputBatchArgumentBuilder done();
		}
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
		
		@Override
		default GeneratePrintedOutputBatchArgumentBuilder setXci(Xci xci) {
			PrintedOutputOptionsSetter.super.setXci(xci);
			return this;
		}

		public XciArgumentBuilder xci();

	// TODO:  Fix up the executeOns to overload the options.
		public BatchResult executeOn(PathOrUrl template, Document data) throws OutputServiceException, FileNotFoundException;

		public BatchResult executeOn(Path template, Document data) throws OutputServiceException, FileNotFoundException;
		
		public BatchResult executeOn(URL template, Document data) throws OutputServiceException;

		default public BatchResult executeOn(PathOrUrl template, byte[] data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public BatchResult executeOn(Path template, byte[] data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};
		
		default public BatchResult executeOn(URL template, byte[] data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public BatchResult executeOn(PathOrUrl template, InputStream data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public BatchResult executeOn(Path template, InputStream data) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};
		
		default public BatchResult executeOn(URL template, InputStream data) throws OutputServiceException {
			return executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(data));
		};

		default public BatchResult executeOn(PathOrUrl template) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, (Document)null);
		};

		default public BatchResult executeOn(Path template) throws OutputServiceException, FileNotFoundException {
			return executeOn(template, (Document)null);
		};
		
		default public BatchResult executeOn(URL template) throws OutputServiceException {
			return executeOn(template, (Document)null);
		};
		
		public interface XciArgumentBuilder {
			XciArgumentBuilder embedPclFonts(boolean embedFonts);
			XciArgumentBuilder embedPsFonts(boolean embedFonts);
			GeneratePrintedOutputBatchArgumentBuilder done();
		}
	}

	public static interface BatchArgumentBuilder {
		public BatchArgumentBuilder addTemplate(PathOrUrl template);	// TODO: Add code to call addTemplate(String templateName, PathOrUrl template) 

		public default BatchArgumentBuilder addTemplate(Path template) {
			return this.addTemplate(template.getFileName().toString(), PathOrUrl.from(template));
		}

		public default BatchArgumentBuilder addTemplate(URL template) { 	// TODO: Add code to call addTemplate(String templateName, PathOrUrl template)
			return this.addTemplate(PathOrUrl.from(template));
		}

		public BatchArgumentBuilder addTemplate(String templateName, PathOrUrl template);

		public BatchArgumentBuilder addTemplates(List<PathOrUrl> templates); 	// TODO: Add code to call addTemplate(String templateName, PathOrUrl template)

		public default BatchArgumentBuilder addTemplatePaths(List<Path> templates) {
			return this.addTemplates(templates.stream().map(PathOrUrl::from).collect(Collectors.toList())); 	// TODO: Add code to call addTemplate(String templateName, PathOrUrl template)
		}

		public default BatchArgumentBuilder addTemplateUrls(List<URL> templates) {
			return this.addTemplates(templates.stream().map(PathOrUrl::from).collect(Collectors.toList())); 	// TODO: Add code to call addTemplate(String templateName, PathOrUrl template)
		}

		public BatchArgumentBuilder addTemplateEntries(List<Map.Entry<String, PathOrUrl>> entries);

		public default BatchArgumentBuilder addTemplatePathEntries(List<Map.Entry<String, Path>> entries) { 	// TODO: Add code to call addTemplate(String templateName, PathOrUrl template)
			// This is a good candidate for moving into a private interface method when we move to Java 11
			return this.addTemplateEntries(
					entries.stream().map(
							(e)->new AbstractMap.SimpleEntry<String, PathOrUrl>(e.getKey(), PathOrUrl.from(e.getValue()))
						).collect(Collectors.toList())
					);
		}

		public default BatchArgumentBuilder addTemplateUrlEntries(List<Map.Entry<String, URL>> entries) {		// TODO: Add code to call addTemplate(String templateName, PathOrUrl template)
			// This is a duplicate of the previous method (except for arguments) so they can probably be consolidated
			// into a single private interface method.
			return this.addTemplateEntries(
					entries.stream().map(
							(e)->new AbstractMap.SimpleEntry<String, PathOrUrl>(e.getKey(), PathOrUrl.from(e.getValue()))
						).collect(Collectors.toList())
					);
		}

		public BatchArgumentBuilder addData(Document data);

		public BatchArgumentBuilder addData(String dataName, Document data);

		public BatchArgumentBuilder addDataDocuments(List<Document> data);

		public BatchArgumentBuilder addData(List<Map.Entry<String, Document>> entries);

	}
}