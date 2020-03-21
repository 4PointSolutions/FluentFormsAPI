package com._4point.aem.fluentforms.testing.output;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;
import com._4point.aem.fluentforms.impl.output.SafeOutputServiceAdapterWrapper;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService.GeneratePdfArgs;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService.GeneratePdfBatchArgs;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService.GeneratePrintedOutputArgs;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService.GeneratePrintedOutputBatchArgs;

/**
 * MockOutputService can be used to mock calls to the OutputService.
 * 
 * Calls to this object's methods can return specific results and the argument passed in those calls
 * are captured for later retrieval.  You can also create a service that always returns an OutputServiceException
 * in order to test exception handling code.
 *
 * Methods in this object with no JavaDocs rely on methods in other objects that have not yet been implemented.
 */
public class MockOutputService extends OutputServiceImpl {

	/**
	 * Create a MockOutputServiceObject
	 */
	public MockOutputService() {
		super(new MockTraditionalOutputService(), UsageContext.SERVER_SIDE);
	}

	/**
	 * Create a MockOutputServiceObject
	 * 
	 * @param documentFactory
	 */
	public MockOutputService(DocumentFactory documentFactory) {
		super(new MockTraditionalOutputService(documentFactory), UsageContext.SERVER_SIDE);
	}

	/**
	 * Create a MockOutputServiceObject
	 * 
	 * @param adobeOutputService
	 */
	public MockOutputService(TraditionalOutputService adobeOutputService) {
		super(adobeOutputService, UsageContext.SERVER_SIDE);
	}
	
	/**
	 * Creates a MockOutputService that returns a specific result when generatePDFOutput is called.
	 *
	 * @param generatePdfOutputResult
	 * @return
	 */
	public static MockOutputService createGeneratePdfOutputMock(Document generatePdfOutputResult) {
		return new MockOutputService(new MockTraditionalOutputService().setResult(generatePdfOutputResult)); 
	}

	public static MockOutputService createGeneratePdfOutputBatchMock(BatchResult generatePdfOutputBatchResult) {
		return new MockOutputService(new MockTraditionalOutputService().setBatchResult(generatePdfOutputBatchResult)); 
	}

	public static MockOutputService createGeneratePrintedOutputMock(Document generatePrintedOutputResult) {
		return new MockOutputService(new MockTraditionalOutputService().setResult(generatePrintedOutputResult)); 
	}

	public static MockOutputService createGeneratePrintedOutputBatchMock(BatchResult generatePrintedOutputBatchResult) {
		return new MockOutputService(new MockTraditionalOutputService().setBatchResult(generatePrintedOutputBatchResult)); 
	}

	/**
	 * Creates a MockOutputService that throws an OutputServiceException with a specific message.
	 * 
	 * @param message
	 * @return
	 */
	public static MockOutputService createExceptionalMock(String message) {
		return new MockOutputService(ExceptionalMockTraditionalOutputService.create(message)); 
	}

	/**
	 * Set the result of the generatePdfOutput operation.
	 * 
	 * @param result
	 * @return
	 */
	public MockOutputService setGeneratePdfOutputResults(Document result) {
		getMockService().setResult(result);
		return this;
	}

	public MockOutputService setGeneratePdfOutputBatchResults(BatchResult result) {
		getMockService().setBatchResult(result);
		return this;
	}
	
	/**
	 * Get the result of the generatePdfOutput operation.
	 * 
	 * @return
	 */
	public GeneratePdfArgs getGeneratePdfArgs() {
		return getMockService().getGeneratePdfArgs();
	}

	public GeneratePdfBatchArgs getGeneratePdfBatchArgs() {
		return getMockService().getGeneratePdfBatchArgs();
	}

	public GeneratePrintedOutputArgs getGeneratePrintedOutputArgs() {
		return getMockService().getGeneratePrintedOutputArgs();
	}

	public GeneratePrintedOutputBatchArgs getGeneratePrintedOutputBatchArgs() {
		return getMockService().getGeneratePrintedOutputBatchArgs();
	}

	private MockTraditionalOutputService getMockService() {
		return (MockTraditionalOutputService)((SafeOutputServiceAdapterWrapper)(this.getAdobeOutputService())).getOutputService();
	}

}

