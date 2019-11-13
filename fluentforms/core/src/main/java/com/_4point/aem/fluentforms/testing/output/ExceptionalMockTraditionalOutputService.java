package com._4point.aem.fluentforms.testing.output;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;

public class ExceptionalMockTraditionalOutputService implements TraditionalOutputService {
	private final String message;
	
	private ExceptionalMockTraditionalOutputService(String message) {
		super();
		this.message = message;
	}

	@Override
	public Document generatePDFOutput(Document template, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		throw new OutputServiceException(this.message);
	}

	@Override
	public Document generatePDFOutput(String urlOrFileName, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		throw new OutputServiceException(this.message);
	}

	@Override
	public BatchResult generatePDFOutputBatch(Map<String, String> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		throw new OutputServiceException(this.message);
	}

	@Override
	public Document generatePrintedOutput(Document template, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		throw new OutputServiceException(this.message);
	}

	@Override
	public Document generatePrintedOutput(String urlOrFileName, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		throw new OutputServiceException(this.message);
	}

	@Override
	public BatchResult generatePrintedOutputBatch(Map<String, String> templates, Map<String, Document> data, PrintedOutputOptions printedOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		throw new OutputServiceException(this.message);
	}

	public static ExceptionalMockTraditionalOutputService create(String message) {
		return new ExceptionalMockTraditionalOutputService(message);
	}

}
