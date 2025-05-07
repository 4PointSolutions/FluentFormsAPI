package com._4point.aem.docservices.rest_services.client.generatePDF;

import java.io.InputStream;

import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;

public class GeneratePDFServiceTestHelper {
	public static CreatePDFResult convertXmlToCreatePDFResult(InputStream createPDFResultXml) throws Exception {
		// This is a helper message that makes the package private convertXmlToCreatePDFResult() available to the server tests.
		return RestServicesGeneratePDFServiceAdapter.convertXmlToCreatePDFResult(createPDFResultXml);
	}
}
