package com._4point.aem.fluentforms.api.output;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;

public interface OutputService {

	Document generatePDFOutput(Document template, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException;

	Document generatePDFOutput(String urlOrFileName, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException;

	BatchResult generatePDFOutputBatch(Map<String, String> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions) throws OutputServiceException;

	Document generatePrintedOutput(Document template, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException;

	Document generatePrintedOutput(String urlOrFileName, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException;

	BatchResult generatePrintedOutputBatch(Map<String, String> templates, Map<String, Document> data, PrintedOutputOptions printedOutputOptions, BatchOptions batchOptions) throws OutputServiceException;

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
}