package com._4point.aem.fluentforms.impl.output;

import java.util.Map;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;

public class SafeOutputServiceAdapterWrapper implements TraditionalOutputService {

	private final TraditionalOutputService outputService;
	
	public SafeOutputServiceAdapterWrapper(TraditionalOutputService outputService) {
		super();
		this.outputService = outputService;
	}

	@Override
	public Document generatePDFOutput(Document template, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		Objects.requireNonNull(template, "Template parameter cannot be null.");
		Objects.requireNonNull(pdfOutputOptions, "PDFOutputOptions parameter cannot be null.");
		return outputService.generatePDFOutput(template, data, pdfOutputOptions);
	}

	@Override
	public Document generatePDFOutput(String urlOrFileName, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		Objects.requireNonNull(urlOrFileName, "Template parameter cannot be null.");
		Objects.requireNonNull(pdfOutputOptions, "PDFOutputOptions parameter cannot be null.");
		return outputService.generatePDFOutput(urlOrFileName, data, pdfOutputOptions);
	}

	@Override
	public BatchResult generatePDFOutputBatch(Map<String, String> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		Objects.requireNonNull(templates, "Template map parameter cannot be null.");
		Objects.requireNonNull(pdfOutputOptions, "PDFOutputOptions parameter cannot be null.");
		Objects.requireNonNull(batchOptions, "BatchOptions parameter cannot be null.");
		return outputService.generatePDFOutputBatch(templates, data, pdfOutputOptions, batchOptions);
	}

	@Override
	public Document generatePrintedOutput(Document template, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		Objects.requireNonNull(template, "Template parameter cannot be null.");
		Objects.requireNonNull(printedOutputOptions, "PrintedOutputOptions parameter cannot be null.");
		return outputService.generatePrintedOutput(template, data, printedOutputOptions);
	}

	@Override
	public Document generatePrintedOutput(String urlOrFileName, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		Objects.requireNonNull(urlOrFileName, "Template parameter cannot be null.");
		Objects.requireNonNull(printedOutputOptions, "PrintedOutputOptions parameter cannot be null.");
		return outputService.generatePrintedOutput(urlOrFileName, data, printedOutputOptions);
	}

	@Override
	public BatchResult generatePrintedOutputBatch(Map<String, String> templates, Map<String, Document> data, PrintedOutputOptions printedOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		Objects.requireNonNull(templates, "Template parameter cannot be null.");
		Objects.requireNonNull(printedOutputOptions, "PrintedOutputOptions parameter cannot be null.");
		Objects.requireNonNull(batchOptions, "BatchOptions parameter cannot be null.");
		return outputService.generatePrintedOutputBatch(templates, data, printedOutputOptions, batchOptions);
	}

}
