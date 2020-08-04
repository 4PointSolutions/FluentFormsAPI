package com._4point.aem.fluentforms.impl.generatePDF;

import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;

public class SafeGeneratePDFServiceAdapterWrapper implements TraditionalGeneratePDFService {
	private final TraditionalGeneratePDFService generatePdfService;

	public SafeGeneratePDFServiceAdapterWrapper(TraditionalGeneratePDFService generatePdfService) {
		super();
		this.generatePdfService = generatePdfService;
	}

	@Override
	public CreatePDFResult createPDF2(Document inputDoc, String inputFileExtension, String fileTypeSettings,
			PDFSettings pdfSettings, SecuritySettings securitySettings, Document settingsDoc, Document xmpDoc) throws GeneratePDFServiceException {
		Objects.requireNonNull(inputDoc, "inputDoc can not be null");
		Objects.requireNonNull(inputFileExtension, "inputFileExtension can not be null");
		return generatePdfService.createPDF2(inputDoc, inputFileExtension, fileTypeSettings, pdfSettings,
				securitySettings, settingsDoc, xmpDoc);
	}

	// This is required by the mock services.
	public TraditionalGeneratePDFService getGeneratePdfService() {
		return generatePdfService;
	}
}
