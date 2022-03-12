package com._4point.aem.docservices.rest_services.cli.restservices.cli.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com._4point.aem.docservices.rest_services.cli.restservices.cli.AemConfig;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.AemConfigProperties;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.AemConfig.Protocol;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.output.RestServicesOutputServiceAdapter;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;

/**
 * This class creates a TraditionalOutputService object.  Depending on the current configuration, this may be a 
 * RestServicesServiceAdaptter or CloudServicesServiceAdapter
 * 
 * For now, it just returns a RestServicesServiceAdapter
 *
 */
@Configuration
public class OutputServiceAdapterFactoryImpl implements OutputServiceAdapterFactory {
	private final static Logger log = LoggerFactory.getLogger(OutputServiceAdapterFactoryImpl.class);
	private final AemConfig aemConfig;
	
	public OutputServiceAdapterFactoryImpl(AemConfig aemConfig) {
		this.aemConfig = aemConfig;
	}

	public TraditionalOutputService getAdapter() {
		log.info("Creating OutputService adapter for '" + aemConfig.url() + "'.");
		return RestServicesOutputServiceAdapter.builder()
				.machineName(aemConfig.host())
				.port(aemConfig.port())
				.basicAuthentication(aemConfig.username(), aemConfig.secret())
				.useSsl(aemConfig.protocol() == Protocol.HTTPS)
				.aemServerType(AemServerType.StandardType.from(aemConfig.serverType().toString()).orElse(AemServerType.StandardType.OSGI))
				.build();
	}
}
