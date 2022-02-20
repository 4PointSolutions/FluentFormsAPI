package com._4point.aem.docservices.cloud_services.client.output;

import java.util.Map;

import com._4point.aem.docservices.cloud_services.client.helper.CloudServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;

public class CloudServicesOutputServiceAdapter extends CloudServicesServiceAdapter  implements TraditionalOutputService {

	@Override
	public Document generatePDFOutput(Document template, Document data, PDFOutputOptions pdfOutputOptions)
			throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePDFOutput(String urlOrFileName, Document data, PDFOutputOptions pdfOutputOptions)
			throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchResult generatePDFOutputBatch(Map<String, String> templates, Map<String, Document> data,
			PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePrintedOutput(Document template, Document data, PrintedOutputOptions printedOutputOptions)
			throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePrintedOutput(String urlOrFileName, Document data,
			PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchResult generatePrintedOutputBatch(Map<String, String> templates, Map<String, Document> data,
			PrintedOutputOptions printedOutputOptions, BatchOptions batchOptions) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
