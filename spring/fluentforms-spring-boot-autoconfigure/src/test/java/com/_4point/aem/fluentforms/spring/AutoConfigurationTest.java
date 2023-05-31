package com._4point.aem.fluentforms.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;

import com._4point.aem.fluentforms.api.output.OutputService;

class AutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		    .withConfiguration(AutoConfigurations.of(FluentFormsAutoConfiguration.class));

	@Test
	void defaultServiceBacksOff() {
	    this.contextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password")
	    		.run((context) -> {
	        assertThat(context).hasSingleBean(FluentFormsAutoConfiguration.class);
	        assertThat(context).getBean("fluentFormsConfiguration").isSameAs(context.getBean(FluentFormsAutoConfiguration.class));
	        assertThat(context).hasSingleBean(OutputService.class);
	        assertThat(context).getBean("outputService").isNotNull();
	        assertThat(context).hasSingleBean(ResourceConfigCustomizer.class);
	        assertThat(context).getBean("afProxyConfigurer").isNotNull();
	    });
	}
}
