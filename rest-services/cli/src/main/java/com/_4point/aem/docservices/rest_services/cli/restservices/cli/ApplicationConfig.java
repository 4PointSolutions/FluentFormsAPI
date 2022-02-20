package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.output.RestServicesOutputServiceAdapter;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;

@Configuration
public class ApplicationConfig {

	// Need to figure out how we will get this information.
	private static final String TEST_MACHINE_NAME = null;
	private static final int TEST_MACHINE_PORT = 0;
	private static final String TEST_USER = null;
	private static final String TEST_USER_PASSWORD = null;
	private static final AemServerType TEST_MACHINE_AEM_TYPE = null;

	public ApplicationConfig() {
		// TODO Auto-generated constructor stub
		
	}

	@Bean
	public RestServicesOutputServiceAdapter restServicesOutputServiceAdapter() {
		return RestServicesOutputServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TEST_MACHINE_AEM_TYPE)
				.build();
	}

	@Bean
	public OutputService outputService(TraditionalOutputService adapter) {
		return new OutputServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}
	
	
}
