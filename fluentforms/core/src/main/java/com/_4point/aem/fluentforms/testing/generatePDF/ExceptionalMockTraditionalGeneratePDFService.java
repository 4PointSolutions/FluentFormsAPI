package com._4point.aem.fluentforms.testing.generatePDF;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;
import com._4point.aem.fluentforms.impl.generatePDF.PDFSettings;
import com._4point.aem.fluentforms.impl.generatePDF.SecuritySettings;
import com._4point.aem.fluentforms.impl.generatePDF.TraditionalGeneratePDFService;

public class ExceptionalMockTraditionalGeneratePDFService implements TraditionalGeneratePDFService{
	private final String message;
	
	private ExceptionalMockTraditionalGeneratePDFService(String message) {
		super();
		this.message = message;
	}
	@Override
	public CreatePDFResult createPDF2(Document inputDoc, String inputFileExtension, String fileTypeSettings,
			PDFSettings pdfSettings, SecuritySettings securitySettings, Document settingsDoc, Document xmpDoc)
			throws GeneratePDFServiceException {
	    throw new GeneratePDFServiceException(this.message);
	}

	public static ExceptionalMockTraditionalGeneratePDFService create(String message) {
		return new ExceptionalMockTraditionalGeneratePDFService(message);
	}
}
