package com._4point.aem.fluentforms.testing.generatePDF;

import java.io.IOException;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;
import com._4point.aem.fluentforms.impl.generatePDF.TraditionalGeneratePDFService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;

public class MockTraditionalGeneratePDFService implements TraditionalGeneratePDFService {
	private final CreatePDFResult dummyCreatePDFResult;
	private CreatePDFResult createPDFResult;
	private final Document dummyDocument;
	private final DocumentFactory documentFactory;
	private GeneratePDFResultArgs generatePDFResultArgs;
		
	MockTraditionalGeneratePDFService() {	
		super();
		this.dummyCreatePDFResult = new DummyCreatePDFResult();
		this.documentFactory = new MockDocumentFactory();
		this.dummyDocument = documentFactory.create(new byte[0]);
	}


	@Override
	public CreatePDFResult createPDF2(Document inputDoc, String inputFileExtension, String fileTypeSettings,
			String pdfSettings, String securitySettings, Document settingsDoc, Document xmpDoc)
			throws GeneratePDFServiceException {
	   this.generatePDFResultArgs = new GeneratePDFResultArgs(inputDoc, inputFileExtension, fileTypeSettings, pdfSettings,
                      securitySettings, settingsDoc, xmpDoc);
		return this.createPDFResult == null ? dummyCreatePDFResult: this.createPDFResult;
	}
	
	

	public CreatePDFResult getDummyCreatePDFResult() {
		return dummyCreatePDFResult;
	}


	public CreatePDFResult getCreatePDFResult() {
		return createPDFResult;
	}


	public Document getDummyDocument() {
		return dummyDocument;
	}


	public DocumentFactory getDocumentFactory() {
		return documentFactory;
	}


	public GeneratePDFResultArgs getGeneratePDFResultArgs() {
		return generatePDFResultArgs;
	}
	
	

	public MockTraditionalGeneratePDFService setCreatePDFResult(CreatePDFResult createPDFResult) {
		this.createPDFResult = createPDFResult;
		return this;
	}


	public MockTraditionalGeneratePDFService setGeneratePDFResultArgs(GeneratePDFResultArgs generatePDFResultArgs) {
		this.generatePDFResultArgs = generatePDFResultArgs;
		return this;
	}



	public static class GeneratePDFResultArgs {
		private final Document inputDoc;
		private final String inputFileExtension;
		private final String fileTypeSettings;
		private final String pdfSettings;
		private final String securitySettings;
		private final Document settingsDoc;
		private final Document xmpDoc;
		
		public GeneratePDFResultArgs(Document inputDoc, String inputFileExtension, String fileTypeSettings,
				String pdfSettings, String securitySettings, Document settingsDoc, Document xmpDoc) {
			super();
			this.inputDoc = inputDoc;
			this.inputFileExtension = inputFileExtension;
			this.fileTypeSettings = fileTypeSettings;
			this.pdfSettings = pdfSettings;
			this.securitySettings = securitySettings;
			this.settingsDoc = settingsDoc;
			this.xmpDoc = xmpDoc;
		}
		
		public Document getInputDoc() {
			return inputDoc;
		}
		public String getInputFileExtension() {
			return inputFileExtension;
		}
		public String getFileTypeSettings() {
			return fileTypeSettings;
		}
		public String getPdfSettings() {
			return pdfSettings;
		}
		public String getSecuritySettings() {
			return securitySettings;
		}
		public Document getSettingsDoc() {
			return settingsDoc;
		}
		public Document getXmpDoc() {
			return xmpDoc;
		}
				
	}
	
	
	private class DummyCreatePDFResult implements CreatePDFResult {

		@Override
		public void close() throws IOException {			
		}

		@Override
		public Document getCreatedDocument() {
			return dummyDocument;
		}

		@Override
		public Document getLogDocument() {		
			return dummyDocument;
		}
		
	}


}
