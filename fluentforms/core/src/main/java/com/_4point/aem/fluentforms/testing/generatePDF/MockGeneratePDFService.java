package com._4point.aem.fluentforms.testing.generatePDF;

import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.impl.generatePDF.GeneratePDFServiceImpl;
import com._4point.aem.fluentforms.impl.generatePDF.SafeGeneratePDFServiceAdapterWrapper;
import com._4point.aem.fluentforms.impl.generatePDF.TraditionalGeneratePDFService;
import com._4point.aem.fluentforms.testing.assembler.MockAssemblerService;
import com._4point.aem.fluentforms.testing.assembler.MockTraditionalAssemblerService;
import com._4point.aem.fluentforms.testing.generatePDF.MockTraditionalGeneratePDFService.GeneratePDFResultArgs;

/**
 * MockGeneratePDFService can be used to mock calls to the GeneratedPDFService.
 * 
 * Calls to this object's methods can return specific results and the argument passed in those calls
 * are captured for later retrieval.  You can also create a service that always returns an GeneratePDFServiceException
 * in order to test exception handling code.
 *
 * Methods in this object with no JavaDocs rely on methods in other objects that have not yet been implemented.
 */
public class MockGeneratePDFService extends GeneratePDFServiceImpl {

	public MockGeneratePDFService(TraditionalGeneratePDFService generatePdfService) {
		super(generatePdfService);
	}
	
	public MockGeneratePDFService() {
		super(new MockTraditionalGeneratePDFService());
	}
	
	public static MockGeneratePDFService createExceptionalMock(String message) {
		return new MockGeneratePDFService(ExceptionalMockTraditionalGeneratePDFService.create(message)); 
	}
	
	public GeneratePDFResultArgs getGeneratePDFResultArgs() {
		return getMockService().getGeneratePDFResultArgs();
	}
   
	
	private MockTraditionalGeneratePDFService getMockService() {
		return (MockTraditionalGeneratePDFService)((SafeGeneratePDFServiceAdapterWrapper)(this.getAdobeGeneratePDF())).getGeneratePdfService();
	}

	
	public static MockGeneratePDFService createGeneratePDFServiceMock(CreatePDFResult createPDFResult) {
		return new MockGeneratePDFService(new MockTraditionalGeneratePDFService().setCreatePDFResult(createPDFResult)); 
	}
	

}
