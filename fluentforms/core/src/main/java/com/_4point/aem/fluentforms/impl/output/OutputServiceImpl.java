package com._4point.aem.fluentforms.impl.output;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;

public class OutputServiceImpl implements OutputService {

	@Override
	public Document generatePDFOutput(Document arg0, Document arg1, PDFOutputOptions arg2) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePDFOutput(String arg0, Document arg1, PDFOutputOptions arg2) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchResult generatePDFOutputBatch(Map<String, String> arg0, Map<String, Document> arg1, PDFOutputOptions arg2, BatchOptions arg3) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePrintedOutput(Document arg0, Document arg1, PrintedOutputOptions arg2) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePrintedOutput(String arg0, Document arg1, PrintedOutputOptions arg2) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchResult generatePrintedOutputBatch(Map<String, String> arg0, Map<String, Document> arg1, PrintedOutputOptions arg2, BatchOptions arg3)
			throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
