package com._4point.aem.fluentforms.testing.pdfUtility;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.impl.pdfUtility.PdfUtilityServiceImpl;
import com._4point.aem.fluentforms.impl.pdfUtility.TraditionalPdfUtilityService;

public class MockPdfUtilityService extends PdfUtilityServiceImpl {

	/**
	 * Create a MockOutputServiceObject
	 */
	public MockPdfUtilityService() {
		super(new MockTraditionalPdfUtilityService());
	}

	/**
	 * Create a MockOutputServiceObject
	 * 
	 * @param docFactory
	 */
	public MockPdfUtilityService(DocumentFactory docFactory) {
		super(new MockTraditionalPdfUtilityService(docFactory));
	}

	/**
	 * Create a MockOutputServiceObject
	 * 
	 * @param adobePdfUtilityService
	 */
	public MockPdfUtilityService(TraditionalPdfUtilityService adobePdfUtilityService) {
		super(adobePdfUtilityService);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a MockPdfUtilityService that returns a specific result when clone() is called.
	 * 
	 * @param result
	 * @return
	 */
	public static MockPdfUtilityService createCloneMock(Document result) {
		return new MockPdfUtilityService(MockTraditionalPdfUtilityService.createDocumentMock(result));
	}

	/**
	 * Creates a MockPdfUtilityService that returns a specific result when convertPDFtoXDP() is called.
	 * 
	 * @param result
	 * @return
	 */
	public static MockPdfUtilityService createConvertPDFtoXDPMock(Document result) {
		return new MockPdfUtilityService(MockTraditionalPdfUtilityService.createDocumentMock(result));
	}
	
	/**
	 * Creates a MockPdfUtilityService that throws an PdfUtilityException with a specific message.
	 * 
	 * @param message
	 * @return
	 */
	public static MockPdfUtilityService createExceptionalMock(String message) {
		return new MockPdfUtilityService(ExceptionalMockTraditionalPdfUtilityService.create(message));
	}
	
	public MockPdfUtilityService setResult(Document document) {
		getService().setResult(document);
		return this;
	}
	
	public Document getInputDoc() {
		return getService().getInputDoc();
	}

	private MockTraditionalPdfUtilityService getService() {
		return (MockTraditionalPdfUtilityService)this.getAdobePdfUtilityService();
	}
}
