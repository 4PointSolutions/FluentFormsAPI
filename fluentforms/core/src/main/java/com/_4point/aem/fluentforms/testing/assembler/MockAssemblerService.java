package com._4point.aem.fluentforms.testing.assembler;

import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;

/**
 * MockAssemblerService can be used to mock calls to the OutputService.
 * 
 * Calls to this object's methods can return specific results and the argument passed in those calls
 * are captured for later retrieval.  You can also create a service that always returns an OutputServiceException
 * in order to test exception handling code.
 *
 * Methods in this object with no JavaDocs rely on methods in other objects that have not yet been implemented.
 */
public class MockAssemblerService extends AssemblerServiceImpl {

	public MockAssemblerService(TraditionalDocAssemblerService adobDocAssemblerService, UsageContext usageContext) {
		super(adobDocAssemblerService, usageContext);
		// TODO Auto-generated constructor stub
	}

	
}

