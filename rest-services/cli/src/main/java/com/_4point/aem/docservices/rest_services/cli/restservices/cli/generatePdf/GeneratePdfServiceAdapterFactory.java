package com._4point.aem.docservices.rest_services.cli.restservices.cli.generatePdf;

import com._4point.aem.fluentforms.impl.generatePDF.TraditionalGeneratePDFService;

/**
* This class creates a TraditionalGeneratePdfService object.  Depending on the current configuration, this may be a 
* RestServicesServiceAdaptter or CloudServicesServiceAdapter
* 
* For now, it just returns a RestServicesServiceAdapter
*
*/
@FunctionalInterface
public interface GeneratePdfServiceAdapterFactory {
	TraditionalGeneratePDFService getAdapter();
}
