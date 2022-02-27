package com._4point.aem.docservices.rest_services.cli.restservices.cli.output;

import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;

/**
 * This class creates a TraditionalOutputService object.  Depending on the current configuration, this may be a 
 * RestServicesServiceAdaptter or CloudServicesServiceAdapter
 * 
 * For now, it just returns a RestServicesServiceAdapter
 *
 */
@FunctionalInterface
public interface OutputServiceAdapterFactory {
	TraditionalOutputService getAdapter();
}
