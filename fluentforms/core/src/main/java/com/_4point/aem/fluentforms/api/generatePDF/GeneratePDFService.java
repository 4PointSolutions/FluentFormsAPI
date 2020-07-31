package com._4point.aem.fluentforms.api.generatePDF;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.Transformable;

public interface GeneratePDFService {

	CreatePDFResult createPDF2(Document inputDoc, String inputFileExtension, String fileTypeSettings,
			String pdfSettings, String securitySettings, Document settingsDoc, Document xmpDoc) throws GeneratePDFServiceException;

	/*
	 * ExportPDFResult exportPDF2(Document inputDoc, String inputFileExtension,
	 * String formatType, Document settingsDoc);
	 * 
	 * HtmlToPdfResult htmlFileToPdf(Document inputDoc, String fileTypeSettingsName,
	 * String securitySettingsName, Document settingsDoc, Document xmpDoc);
	 * 
	 * HtmlToPdfResult htmlToPdf2(String inputUrl, String fileTypeSettingsName,
	 * String securitySettingsName, Document settingsDoc, Document xmpDoc);
	 * 
	 * OptimizePDFResult optimizePDF(Document inputDoc, String fileTypeSettings,
	 * Document settingsDoc);
	 * 
	 * void updateGeneralConfig(Map<String,String[]> generalConfig);
	 */

	CreatePDFResultArgumentBuilder createPDF();

	@SuppressWarnings("serial")
	public static class GeneratePDFServiceException extends Exception {

		public GeneratePDFServiceException() {
			super();
		}

		public GeneratePDFServiceException(String message, Throwable cause) {
			super(message, cause);
		}

		public GeneratePDFServiceException(String message) {
			super(message);
		}

		public GeneratePDFServiceException(Throwable cause) {
			super(cause);
		}

	}

	public static interface CreatePDFResultArgumentBuilder extends Transformable<CreatePDFResultArgumentBuilder> {
		CreatePDFResultArgumentBuilder setFileTypeSettings(String fileTypeSettings);
		CreatePDFResultArgumentBuilder setPdfSetting(String pdfSettings);
		CreatePDFResultArgumentBuilder setSecuritySetting(String securitySettings);
		CreatePDFResultArgumentBuilder setSettingDoc(Document settingsDoc);
		CreatePDFResultArgumentBuilder setxmpDoc(Document xmpDoc);
		public CreatePDFResult executeOn(Document inputDoc, String inputFileExtension) throws GeneratePDFServiceException;
	}

}
