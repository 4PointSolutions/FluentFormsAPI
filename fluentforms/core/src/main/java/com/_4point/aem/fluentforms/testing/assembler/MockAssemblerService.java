package com._4point.aem.fluentforms.testing.assembler;

import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com._4point.aem.fluentforms.impl.assembler.SafeAssemblerServiceAdapterWrapper;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com._4point.aem.fluentforms.testing.assembler.MockTraditionalAssemblerService.GenerateAssemblerResultArgs;

/**
 * MockAssemblerService can be used to mock calls to the AssemblerService.
 * 
 * Calls to this object's methods can return specific results and the argument passed in those calls
 * are captured for later retrieval.  You can also create a service that always returns an AssemblerServiceException
 * in order to test exception handling code.
 *
 * Methods in this object with no JavaDocs rely on methods in other objects that have not yet been implemented.
 */
public class MockAssemblerService extends AssemblerServiceImpl {
	
	/**
	 * Create a MockAssemblerService
	 */
	public MockAssemblerService() {
		super(new MockTraditionalAssemblerService(), UsageContext.SERVER_SIDE);
	}
    

	/**
	 * Create a MockAssemblerServiceObject
	 * 
	 * @param adobeAssemblerService
	 */
	public MockAssemblerService(TraditionalDocAssemblerService adobeAssemblerService) {
		super(adobeAssemblerService, UsageContext.SERVER_SIDE);
	}
	
	/**
	 * Creates a MockAssemblerServiceObject that returns a specific result when invoke is called.
	 *
	 * @param assemblerResult
	 * @return
	 */
	public static MockAssemblerService createAssemblerResultMock(AssemblerResult assemblerResult) {
		return new MockAssemblerService(new MockTraditionalAssemblerService().setAssemblerResult(assemblerResult)); 
	}
	

	/**
	 * Creates a MockAssemblerService that throws an AssemblerServiceException with a specific message.
	 * 
	 * @param message
	 * @return
	 */
	public static MockAssemblerService createExceptionalMock(String message) {
		return new MockAssemblerService(ExceptionalMockTraditionalAssemblerService.create(message)); 
	}
	
	
	public GenerateAssemblerResultArgs getGenerateAssemblerResultArgs() {
		return getMockService().getGenerateAssemblerResultArgs();
	}
   
	

	private MockTraditionalAssemblerService getMockService() {
		return (MockTraditionalAssemblerService)((SafeAssemblerServiceAdapterWrapper)(this.getAdobeAssemblerService())).getAssemblerService();
	}


}

