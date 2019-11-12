package com._4point.aem.fluentforms.testing.output;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;

public class MockTraditionalOutputService implements TraditionalOutputService {
	private final DocumentFactory documentFactory;
	private final Document DUMMY_DOCUMENT;
	private final BatchResult DUMMY_BATCH_RESULT;

	Document result;
	BatchResult batchResult;
	
	GeneratePdfArgs generatePdfArgs;
	GeneratePdfBatchArgs generatePdfBatchArgs;
	GeneratePrintedOutputArgs generatePrintedOutputArgs;
	GeneratePrintedOutputBatchArgs generatePrintedOutputBatchArgs;
	
	private MockTraditionalOutputService() {
		super();
		this.documentFactory = new MockDocumentFactory();
		this.DUMMY_DOCUMENT = documentFactory.create(new byte[0]);
		this.DUMMY_BATCH_RESULT = new DummyBatchResult();
	}

	private MockTraditionalOutputService(DocumentFactory documentFactory) {
		super();
		this.documentFactory = documentFactory;
		this.DUMMY_DOCUMENT = documentFactory.create(new byte[0]);
		this.DUMMY_BATCH_RESULT = new DummyBatchResult();
	}

	public static MockTraditionalOutputService createDocumentMock(Document renderPDFFormResult) {
		return new MockTraditionalOutputService().setResult(renderPDFFormResult);
	}

	public static MockTraditionalOutputService createDocumentMock(DocumentFactory documentFactory, Document renderPDFFormResult) {
		return new MockTraditionalOutputService(documentFactory).setResult(renderPDFFormResult);
	}

	public static MockTraditionalOutputService createBatchMock(BatchResult renderPDFFormResult) {
		return new MockTraditionalOutputService().setBatchResult(renderPDFFormResult);
	}

	public static MockTraditionalOutputService createBatchMock(DocumentFactory documentFactory, BatchResult renderPDFFormResult) {
		return new MockTraditionalOutputService(documentFactory).setBatchResult(renderPDFFormResult);
	}

	@Override
	public Document generatePDFOutput(Document template, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		this.generatePdfArgs = new GeneratePdfArgs(template, data, pdfOutputOptions);
		return this.result == null ? DUMMY_DOCUMENT : this.result;
	}

	@Override
	public Document generatePDFOutput(String urlOrFileName, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		this.generatePdfArgs = new GeneratePdfArgs(urlOrFileName, data, pdfOutputOptions);
		return this.result == null ? DUMMY_DOCUMENT : this.result;
	}

	@Override
	public BatchResult generatePDFOutputBatch(Map<String, String> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		this.generatePdfBatchArgs = new GeneratePdfBatchArgs(templates, data, pdfOutputOptions, batchOptions);
		return this.batchResult == null ? DUMMY_BATCH_RESULT : this.batchResult;
	}

	@Override
	public Document generatePrintedOutput(Document template, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		this.generatePrintedOutputArgs = new GeneratePrintedOutputArgs(template, data, printedOutputOptions);
		return this.result == null ? DUMMY_DOCUMENT : this.result;
	}

	@Override
	public Document generatePrintedOutput(String urlOrFileName, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		this.generatePrintedOutputArgs = new GeneratePrintedOutputArgs(urlOrFileName, data, printedOutputOptions);
		return this.result == null ? DUMMY_DOCUMENT : this.result;
	}

	@Override
	public BatchResult generatePrintedOutputBatch(Map<String, String> templates, Map<String, Document> data, PrintedOutputOptions printedOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		this.generatePrintedOutputBatchArgs = new GeneratePrintedOutputBatchArgs(templates, data, printedOutputOptions, batchOptions);
		return this.batchResult == null ? DUMMY_BATCH_RESULT : this.batchResult;
	}
	
	
	public GeneratePdfArgs getGeneratePdfArgs() {
		return generatePdfArgs;
	}

	public GeneratePdfBatchArgs getGeneratePdfBatchArgs() {
		return generatePdfBatchArgs;
	}

	public GeneratePrintedOutputArgs getGeneratePrintedOutputArgs() {
		return generatePrintedOutputArgs;
	}

	public GeneratePrintedOutputBatchArgs getGeneratePrintedOutputBatchArgs() {
		return generatePrintedOutputBatchArgs;
	}

	public MockTraditionalOutputService setResult(Document result) {
		this.result = result;
		return this;
	}

	public MockTraditionalOutputService setBatchResult(BatchResult batchResult) {
		this.batchResult = batchResult;
		return this;
	}

	
	public static class GeneratePdfArgs {
		private final Document template;
		private final String urlOrFilename;
		private final Document data;
		private final PDFOutputOptions pdfOutputOptions;
		
		private GeneratePdfArgs(Document template, Document data, PDFOutputOptions pdfOutputOptions) {
			super();
			this.urlOrFilename = null;
			this.template = Objects.requireNonNull(template);
			this.data = data;
			this.pdfOutputOptions = Objects.requireNonNull(pdfOutputOptions);
		}
		
		private GeneratePdfArgs(String urlOrFilename, Document data, PDFOutputOptions pdfOutputOptions) {
			super();
			this.urlOrFilename = Objects.requireNonNull(urlOrFilename);
			this.template = null;
			this.data = data;
			this.pdfOutputOptions = Objects.requireNonNull(pdfOutputOptions);
		}

		public Document getTemplate() {
			return template;
		}

		public String getUrlOrFilename() {
			return urlOrFilename;
		}

		public Document getData() {
			return data;
		}

		public PDFOutputOptions getPdfOutputOptions() {
			return pdfOutputOptions;
		}
	}
	
	public static class GeneratePdfBatchArgs {
		private final Map<String, String> templates;
		private final Map<String, Document> data;
		private final PDFOutputOptions pdfOutputOptions;
		private final BatchOptions batchOptions;
		
		private GeneratePdfBatchArgs(Map<String, String> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions) {
			super();
			this.templates = Objects.requireNonNull(templates);
			this.data = data;
			this.pdfOutputOptions = Objects.requireNonNull(pdfOutputOptions);
			this.batchOptions = Objects.requireNonNull(batchOptions);
		}

		public Map<String, String> getTemplates() {
			return templates;
		}

		public Map<String, Document> getData() {
			return data;
		}

		public PDFOutputOptions getPdfOutputOptions() {
			return pdfOutputOptions;
		}

		public BatchOptions getBatchOptions() {
			return batchOptions;
		}
	}
	
	public static class GeneratePrintedOutputArgs {
		private final Document template;
		private final String urlOrFilename;
		private final Document data;
		private final PrintedOutputOptions printedOutputOptions;
		
		private GeneratePrintedOutputArgs(Document template, Document data, PrintedOutputOptions printedOutputOptions) {
			super();
			this.urlOrFilename = null;
			this.template = Objects.requireNonNull(template);
			this.data = data;
			this.printedOutputOptions = Objects.requireNonNull(printedOutputOptions);
		}
		
		private GeneratePrintedOutputArgs(String urlOrFilename, Document data, PrintedOutputOptions printedOutputOptions) {
			super();
			this.urlOrFilename = Objects.requireNonNull(urlOrFilename);
			this.template = null;
			this.data = data;
			this.printedOutputOptions = Objects.requireNonNull(printedOutputOptions);
		}

		public Document getTemplate() {
			return template;
		}

		public String getUrlOrFilename() {
			return urlOrFilename;
		}

		public Document getData() {
			return data;
		}

		public PrintedOutputOptions getPrintedOutputOptions() {
			return printedOutputOptions;
		}
	}
	
	public static class GeneratePrintedOutputBatchArgs {
		private final Map<String, String> templates;
		private final Map<String, Document> data;
		private final PrintedOutputOptions printedOutputOptions;
		private final BatchOptions batchOptions;
		
		private GeneratePrintedOutputBatchArgs(Map<String, String> templates, Map<String, Document> data, PrintedOutputOptions printedOutputOptions, BatchOptions batchOptions) {
			super();
			this.templates = Objects.requireNonNull(templates);
			this.data = data;
			this.printedOutputOptions = Objects.requireNonNull(printedOutputOptions);
			this.batchOptions = Objects.requireNonNull(batchOptions);
		}

		public Map<String, String> getTemplates() {
			return templates;
		}

		public Map<String, Document> getData() {
			return data;
		}

		public PrintedOutputOptions getprintedOutputOptions() {
			return printedOutputOptions;
		}

		public BatchOptions getBatchOptions() {
			return batchOptions;
		}
	}

	private class DummyBatchResult implements BatchResult {

		@Override
		public List<Document> getGeneratedDocs() {
			return Collections.emptyList();
		}

		@Override
		public Document getMetaDataDoc() {
			return DUMMY_DOCUMENT;
		}
	}
}
