package com._4point.aem.fluentforms.testing.generatePDF;

import java.io.IOException;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;
import com._4point.aem.fluentforms.impl.generatePDF.PDFSettings;
import com._4point.aem.fluentforms.impl.generatePDF.SecuritySettings;
import com._4point.aem.fluentforms.impl.generatePDF.TraditionalGeneratePDFService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.assembler.MockTraditionalAssemblerService;

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
			PDFSettings pdfSettings, SecuritySettings  securitySettings, Document settingsDoc, Document xmpDoc)
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

	public static MockTraditionalGeneratePDFService createGeneratePDFMock(CreatePDFResult createPDFResult) {
		return new MockTraditionalGeneratePDFService().setCreatePDFResult(createPDFResult);
	}

	public static class GeneratePDFResultArgs {
		private final Document inputDoc;
		private final String inputFileExtension;
		private final String fileTypeSettings;
		private final PDFSettings pdfSettings;
		private final SecuritySettings securitySettings;
		private final Document settingsDoc;
		private final Document xmpDoc;
		
		public GeneratePDFResultArgs(Document inputDoc, String inputFileExtension, String fileTypeSettings,
				PDFSettings pdfSettings, SecuritySettings securitySettings, Document settingsDoc, Document xmpDoc) {
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
		public PDFSettings getPdfSettings() {
			return pdfSettings;
		}
		public SecuritySettings getSecuritySettings() {
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
