package com._4point.aem.docservices.rest_services.client.assembler;

import java.io.InputStream;

import com._4point.aem.fluentforms.api.assembler.AssemblerResult;

public class AssemblerServiceTestHelper {
	// This is a helper message that makes the package private convertXmlToAssemblerResult() available to the server tests.
	public static AssemblerResult convertXmlToAssemblerResult(InputStream assemblerResultXml) throws Exception {
		return RestServicesDocAssemblerServiceAdapter.convertXmlToAssemblerResult(assemblerResultXml);
	}
}
