package com._4point.aem.docservices.rest_services.cli.restservices.cli.generatePdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com._4point.aem.docservices.rest_services.cli.restservices.cli.AemConfig;
import com._4point.aem.docservices.rest_services.client.generatePDF.RestServicesGeneratePDFServiceAdapter;
import com._4point.aem.fluentforms.impl.generatePDF.TraditionalGeneratePDFService;

/**
 * This class creates a TraditionalGeneratePDFService object.  Depending on the current configuration, this may be a 
 * RestServicesServiceAdaptter or CloudServicesServiceAdapter
 * 
 * For now, it just returns a RestServicesServiceAdapter
 *
 */
@Configuration
public class GeneratePdfServiceAdapterFactoryImpl implements GeneratePdfServiceAdapterFactory {
	private final static Logger log = LoggerFactory.getLogger(GeneratePdfServiceAdapterFactoryImpl.class);
	private final AemConfig aemConfig;
	
	public GeneratePdfServiceAdapterFactoryImpl(AemConfig aemConfig) {
		this.aemConfig = aemConfig;
	}

	@Override
	public TraditionalGeneratePDFService getAdapter() {
		log.info("Creating GeneratePdfService adapter for '" + aemConfig.url() + "'.");
		return RestServicesGeneratePDFServiceAdapter.builder()
													.machineName(aemConfig.host())
													.port(aemConfig.port())
													.basicAuthentication(aemConfig.username(), aemConfig.secret())
													.useSsl(false)
													.build();
	}

	
}
