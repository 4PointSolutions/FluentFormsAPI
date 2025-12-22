package com._4point.aem.fluentforms.spring;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ContextConsumer;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmissionHandler;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.SpringAfSubmitProcessor;
import com._4point.aem.fluentforms.spring.AemProxyJerseyAfSubmission.AfSubmitAemProxyProcessor;
import com._4point.aem.fluentforms.spring.AemProxyJerseyAfSubmission.AfSubmitLocalProcessor;
import com._4point.aem.fluentforms.spring.AemProxyJerseyAfSubmission.JerseyAfSubmitProcessor;

/**
 * Test that AutoConfiguration happens.  The code in this test class is based on the following docs:
 * 
 * https://spring.io/blog/2018/03/07/testing-auto-configurations-with-spring-boot-2-0
 * 
 */
class JerseyAutoConfigurationTest {
	
	private static final AutoConfigurations AUTO_CONFIG = AutoConfigurations.of(FluentFormsJerseyAutoConfiguration.class, AemProxyJerseyAutoConfiguration.class, FluentFormsAutoConfiguration.class);
	
	private static final AutoConfigurations LOCAL_SUBMIT_CONFIG = AutoConfigurations.of(FluentFormsJerseyAutoConfiguration.class, AemProxyJerseyAutoConfiguration.class, FluentFormsAutoConfiguration.class, DummyLocalSubmitHandler.class);
	
	private static final AutoConfigurations ALTERNATE_PROXY_CONFIG = AutoConfigurations.of(DummyProxyImplementation.class, FluentFormsJerseyAutoConfiguration.class, AemProxyJerseyAutoConfiguration.class, FluentFormsAutoConfiguration.class);

	// Tests to make sure that only the FluentFormsLibraries are loaded in a non-web application.
	private static final ContextConsumer<? super AssertableApplicationContext> FF_LIBRARIES_ONLY = (context) -> {
		assertAll(
				()->assertThat(context).hasSingleBean(FluentFormsJerseyAutoConfiguration.class),
				()->assertThat(context).getBean(FluentFormsJerseyAutoConfiguration.class.getName()).isSameAs(context.getBean(FluentFormsJerseyAutoConfiguration.class)),
				()->assertThat(context).hasSingleBean(OutputService.class),
				()->assertThat(context).getBean("outputService").isNotNull(),
				()->assertThat(context).doesNotHaveBean(AemProxyAutoConfiguration.class),
				()->assertThat(context).doesNotHaveBean(SpringAfSubmitProcessor.class),
				()->assertThat(context).doesNotHaveBean(AfSubmissionHandler.class),
				()->assertThat(context).doesNotHaveBean(AemProxyJerseyAutoConfiguration.class),
				()->assertThat(context).doesNotHaveBean(JerseyAfSubmitProcessor.class)
				);
		};

	// Tests to make sure that only the FluentFormsLibraries are loaded in a web application.
	private static final ContextConsumer<? super AssertableWebApplicationContext> WEB_FF_LIBRARIES_ONLY = (context) -> {
		assertAll(
				()->assertThat(context).hasSingleBean(FluentFormsJerseyAutoConfiguration.class),
				()->assertThat(context).getBean(FluentFormsJerseyAutoConfiguration.class.getName()).isSameAs(context.getBean(FluentFormsJerseyAutoConfiguration.class)),
				()->assertThat(context).hasSingleBean(OutputService.class),
				()->assertThat(context).getBean("outputService").isNotNull(),
				()->assertThat(context).doesNotHaveBean(AemProxyAutoConfiguration.class),
				()->assertThat(context).doesNotHaveBean(SpringAfSubmitProcessor.class),
				()->assertThat(context).doesNotHaveBean(AfSubmissionHandler.class),
				()->assertThat(context).doesNotHaveBean(AemProxyJerseyAutoConfiguration.class),
				()->assertThat(context).doesNotHaveBean(JerseyAfSubmitProcessor.class)
				);
	};

	// Tests to make sure that all beans are loaded by default in a web application.
	private static final ContextConsumer<? super AssertableWebApplicationContext> WEB_ALL_DEFAULT_SERVICES = (context) -> {
		assertAll(
				()->assertThat(context).hasSingleBean(FluentFormsJerseyAutoConfiguration.class),
				()->assertThat(context).getBean(FluentFormsJerseyAutoConfiguration.class.getName()).isSameAs(context.getBean(FluentFormsJerseyAutoConfiguration.class)),
				()->assertThat(context).hasSingleBean(OutputService.class),
				()->assertThat(context).getBean("outputService").isNotNull(),
				()->assertThat(context).hasSingleBean(AemProxyJerseyAutoConfiguration.class),
				()->assertThat(context).getBean(AemProxyJerseyAutoConfiguration.class.getName()).isSameAs(context.getBean(AemProxyJerseyAutoConfiguration.class)),
				()->assertThat(context).hasSingleBean(JerseyAfSubmitProcessor.class),
				()->assertThat(context).getBean(JerseyAfSubmitProcessor.class).isSameAs(context.getBean(AfSubmitAemProxyProcessor.class)),
				()->assertThat(context).doesNotHaveBean(AfSubmissionHandler.class)
				);
	};

	// Tests to make sure that all beans are loaded by default in a web application.
//	private static final ContextConsumer<? super AssertableWebApplicationContext> WEB_ALL_SPRINGMVC_SERVICES = (context) -> {
//		assertAll(
//				()->assertThat(context).hasSingleBean(FluentFormsJerseyAutoConfiguration.class),
//				()->assertThat(context).getBean(FluentFormsJerseyAutoConfiguration.class.getName()).isSameAs(context.getBean(FluentFormsJerseyAutoConfiguration.class)),
//				()->assertThat(context).hasSingleBean(OutputService.class),
//				()->assertThat(context).getBean("outputService").isNotNull(),
//				()->assertThat(context).hasSingleBean(AemProxyAutoConfiguration.class),
//				()->assertThat(context).getBean(AemProxyAutoConfiguration.class.getName()).isSameAs(context.getBean(AemProxyAutoConfiguration.class)),
//				()->assertThat(context).hasSingleBean(SpringAfSubmitProcessor.class),
//				()->assertThat(context).getBean(SpringAfSubmitProcessor.class).isSameAs(context.getBean(AemProxyAfSubmission.AfSubmitAemProxyProcessor.class)),
//				()->assertThat(context).doesNotHaveBean(AfSubmissionHandler.class),
//				()->assertThat(context).doesNotHaveBean(AemProxyJerseyAutoConfiguration.class),
//				()->assertThat(context).doesNotHaveBean(JerseyAfSubmitProcessor.class)
//				);
//	};

	// Tests to make sure that all beans are loaded in a web application that contains a local submit handler.
	private static final ContextConsumer<? super AssertableWebApplicationContext> WEB_LOCAL_SUBMIT_SERVICES = (context) -> {
		assertAll(
				()->assertThat(context).hasSingleBean(FluentFormsJerseyAutoConfiguration.class),
				()->assertThat(context).getBean(FluentFormsJerseyAutoConfiguration.class.getName()).isSameAs(context.getBean(FluentFormsJerseyAutoConfiguration.class)),
				()->assertThat(context).hasSingleBean(OutputService.class),
				()->assertThat(context).getBean("outputService").isNotNull(),
				()->assertThat(context).hasSingleBean(AemProxyJerseyAutoConfiguration.class),
				()->assertThat(context).getBean(AemProxyJerseyAutoConfiguration.class.getName()).isSameAs(context.getBean(AemProxyJerseyAutoConfiguration.class)),
				()->assertThat(context).hasSingleBean(JerseyAfSubmitProcessor.class),
				()->assertThat(context).getBean(JerseyAfSubmitProcessor.class).isSameAs(context.getBean(AfSubmitLocalProcessor.class)),
				()->assertThat(context).hasSingleBean(AfSubmissionHandler.class),
				()->assertThat(context).getBean(DummyLocalSubmitHandler.class).isSameAs(context.getBean(AfSubmissionHandler.class))
				);
	};

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		    .withConfiguration(AUTO_CONFIG);

	private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
		    .withConfiguration(AUTO_CONFIG);

	private final WebApplicationContextRunner webLocalSubmitContextRunner = new WebApplicationContextRunner()
		    .withConfiguration(LOCAL_SUBMIT_CONFIG);

	private final WebApplicationContextRunner webAlternateProxyContextRunner = new WebApplicationContextRunner()
		    .withConfiguration(ALTERNATE_PROXY_CONFIG);
	
	// Only the services that do not require a web server should be started.
	@Test
	void nonWebContext_StartNonWebServices() {
	    this.contextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password")
	    		.run(FF_LIBRARIES_ONLY);
	}
	
	// All services should start when a proper web context has been initialized.
	@Test
	void webContext_StartAllServices() {
	    this.webContextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password")
	    		.run(WEB_ALL_DEFAULT_SERVICES);
	}

	// Only the FluentForms libraries are instantiated when the proxy is explicitly disabled.
	@Test
	void webContext_ProxyDisabled_StartNonProxyServices() {
	    this.webContextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password",
	    							"fluentforms.rproxy.enabled=false")
	    		.run(WEB_FF_LIBRARIES_ONLY);
	}

	// Only the FluentForms libraries are instantiated when the proxy is not properly disabled.
	@Test
	void webContext_ProxyNotSpecifiedCorrectly_StartNonProxyServices() {
	    this.webContextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password",
	    							"fluentforms.rproxy.enabled=foobar")
	    		.run(WEB_FF_LIBRARIES_ONLY);
	}

	// All services should start when the proxy has been explicitly enabled.
	@Test
	void webContext_ProxyEnabled_StartNonProxyServices() {
	    this.webContextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password",
	    							"fluentforms.rproxy.enabled=true")
	    		.run(WEB_ALL_DEFAULT_SERVICES);
	}
	
	// All services should start when a proper web context has been initialized.
	@Test
	void webContext_StartAllServices_LocalSubmit() {
	    this.webLocalSubmitContextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password")
	    		.run(WEB_LOCAL_SUBMIT_SERVICES);
	}

	// Only the FluentForms libraries are instantiated by this autoconfiguration when an alternate proxy implementation is supplied.
	@Test
	void webContext_StartAllServices_AlternateProxySupplied() {
	    this.webAlternateProxyContextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password")
	    		.run(WEB_FF_LIBRARIES_ONLY);
	}

	// Only the FluentForms libraries are instantiated when an alternate proxy tyoe is configured.
	@Test
	void webContext_ProxyDisabled_AlternateProxyConfigured() {
	    this.webContextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password",
	    							"fluentforms.rproxy.type=someothertype")
	    		.run(WEB_FF_LIBRARIES_ONLY);
	}

	// All services should start when the jersey proxy type is configured.
	@Test
	void webContext_ProxyEnabled_DefaultProxyConfigured() {
	    this.webContextRunner
	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password",
	    							"fluentforms.rproxy.type=jersey")
	    		.run(WEB_ALL_DEFAULT_SERVICES);
	}
	
	// TODO: For now we are going to ignore mixed scenarios (where we are using one starter with a different rest client
//	// All services should start when the jersey proxy type is configured.
//	@Test
//	void webContext_ProxyEnabled_SpringMvcProxyConfigured() {
//	    this.webContextRunner
//	    		.withPropertyValues("fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
//	    							"fluentforms.aem.user=user", "fluentforms.aem.password=password",
//	    							"fluentforms.rproxy.type=springmvc")
//	    		.run(WEB_ALL_SPRINGMVC_SERVICES);
//	}
	

	public static class DummyLocalSubmitHandler implements AfSubmissionHandler {

		@Override
		public boolean canHandle(String formName) {
			return false;
		}

		@Override
		public SubmitResponse processSubmission(Submission submission) {
			return null;
		}
	}
	
	public static class DummyProxyImplementation implements AemProxyImplemention {
		
	}
}
