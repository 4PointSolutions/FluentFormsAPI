package com._4point.aem.fluentforms.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;

import com._4point.aem.fluentforms.api.output.OutputService;

class AutoConfigurationTest {

	private final AutoConfigurations autoConfigClasses = AutoConfigurations.of(FluentFormsAutoConfiguration.class, AemProxyAutoConfiguration.class);

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		    .withConfiguration(autoConfigClasses);

	private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
		    .withConfiguration(autoConfigClasses);

	// Only the services that do not require a web server should be started.
	@Test
	void nonWebContext_StartNonWebServices() {
	    this.contextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password")
	    		.run((context) -> {
	    			assertAll(
	    					()->assertThat(context).hasSingleBean(FluentFormsAutoConfiguration.class),
	    					()->assertThat(context).getBean("fluentFormsAutoConfiguration").isSameAs(context.getBean(FluentFormsAutoConfiguration.class)),
	    					()->assertThat(context).hasSingleBean(OutputService.class),
	    					()->assertThat(context).getBean("outputService").isNotNull(),
	    					()->assertThat(context).doesNotHaveBean(AemProxyAutoConfiguration.class),
	    					()->assertThat(context).doesNotHaveBean(ResourceConfigCustomizer.class)
	    					);
	    });
	}
	
	// All services should start when a proper web context has been initialized.
	@Test
	void webContext_StartAllServices() {
	    this.webContextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password")
	    		.run((context) -> {
	    			assertAll(
	    					()->assertThat(context).hasSingleBean(FluentFormsAutoConfiguration.class),
	    					()->assertThat(context).getBean("fluentFormsAutoConfiguration").isSameAs(context.getBean(FluentFormsAutoConfiguration.class)),
	    					()->assertThat(context).hasSingleBean(OutputService.class),
	    					()->assertThat(context).getBean("outputService").isNotNull(),
	    					()->assertThat(context).hasSingleBean(AemProxyAutoConfiguration.class),
	    					()->assertThat(context).getBean("aemProxyAutoConfiguration").isSameAs(context.getBean(AemProxyAutoConfiguration.class)),
	    					()->assertThat(context).hasSingleBean(ResourceConfigCustomizer.class),
	    					()->assertThat(context).getBean("afProxyConfigurer").isNotNull()
	    					);
	    });
	}
}
