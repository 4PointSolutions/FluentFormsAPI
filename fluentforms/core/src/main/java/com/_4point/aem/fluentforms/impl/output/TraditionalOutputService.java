package com._4point.aem.fluentforms.impl.output;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;

public interface TraditionalOutputService {

	Document generatePDFOutput(Document template, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException;

	Document generatePDFOutput(String urlOrFileName, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException;

	BatchResult generatePDFOutputBatch(Map<String, String> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions) throws OutputServiceException;

	Document generatePrintedOutput(Document template, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException;

	Document generatePrintedOutput(String urlOrFileName, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException;

	BatchResult generatePrintedOutputBatch(Map<String, String> templates, Map<String, Document> data, PrintedOutputOptions printedOutputOptions, BatchOptions batchOptions) throws OutputServiceException;
}