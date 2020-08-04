package com._4point.aem.fluentforms.impl.generatePDF;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService;

public class GeneratePDFServiceImpl implements GeneratePDFService {

	private final TraditionalGeneratePDFService adobeGeneratePDF;

	public GeneratePDFServiceImpl(TraditionalGeneratePDFService generatePdfService) {
		super();
		this.adobeGeneratePDF = new SafeGeneratePDFServiceAdapterWrapper(generatePdfService);
	}
	
	@Override
	public CreatePDFResult createPDF2(Document inputDoc, String inputFileExtension, String fileTypeSettings,
			PDFSettings pdfSettings, SecuritySettings securitySettings, Document settingsDoc, Document xmpDoc)
			throws GeneratePDFServiceException {
		return adobeGeneratePDF.createPDF2(inputDoc, inputFileExtension, fileTypeSettings, pdfSettings, securitySettings, settingsDoc, xmpDoc);
	}
	
	@Override
	public CreatePDFResultArgumentBuilder createPDF() {
		return new CreatePDFResultArgumentBuilderImpl();
	}

	public TraditionalGeneratePDFService getAdobeGeneratePDF() {
		return adobeGeneratePDF;
	}
	
	private class CreatePDFResultArgumentBuilderImpl implements CreatePDFResultArgumentBuilder {
		
		String fileTypeSettings;
		PDFSettings pdfSettings;
		SecuritySettings securitySettings;
		Document settingsDoc;
		Document xmpDoc;

		@Override
		public CreatePDFResultArgumentBuilder setFileTypeSettings(String fileTypeSettings) {
			this.fileTypeSettings = fileTypeSettings;
			return this;
		}

		@Override
		public CreatePDFResultArgumentBuilder setPdfSetting(PDFSettings pdfSettings) {
			this.pdfSettings = pdfSettings;
			return this;
		}

		@Override
		public CreatePDFResultArgumentBuilder setSecuritySetting(SecuritySettings securitySettings) {
			this.securitySettings = securitySettings;
			return this;
		}

		@Override
		public CreatePDFResultArgumentBuilder setSettingDoc(Document settingsDoc) {
			this.settingsDoc = settingsDoc;
			return this;
		}

		@Override
		public CreatePDFResultArgumentBuilder setxmpDoc(Document xmpDoc) {
			this.xmpDoc = xmpDoc;
			return this;
		}

		@Override
		public CreatePDFResult executeOn(Document inputDoc, String inputFileExtension) throws GeneratePDFServiceException {
			return createPDF2(inputDoc, inputFileExtension, fileTypeSettings, pdfSettings, securitySettings, settingsDoc, xmpDoc);
		}

	}

	
}
