package com._4point.aem.fluentforms.spring;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com._4point.aem.fluentforms.spring.AemProxyJerseyAfSubmission.AfSubmissionHandler;
import com._4point.aem.fluentforms.spring.AemProxyJerseyAfSubmission.AfSubmitAemProxyProcessor;
import com._4point.aem.fluentforms.spring.AemProxyJerseyAfSubmission.AfSubmitLocalProcessor;
import com._4point.aem.fluentforms.spring.AemProxyJerseyAfSubmission.AfSubmitLocalProcessor.InternalAfSubmitAemProxyProcessor;
import com._4point.aem.fluentforms.spring.AemProxyJerseyAfSubmission.AfSubmitProcessor;

/**
 * AutoConfiguration for the Reverse Proxy Library which reverse proxies secondary
 * resources (.css, .js, etc.) that the browser will request.  These requests are forwarded to AEM.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type=Type.SERVLET)
@ConditionalOnProperty(prefix="fluentforms.rproxy", name="enabled", havingValue="true", matchIfMissing=true )
@EnableConfigurationProperties({AemConfiguration.class, AemProxyConfiguration.class})
public class AemProxyJerseyAutoConfiguration {

	/**
	 * Configures the JAX-RS resources associated with reverse proxying resources and submissions from
	 * Adaptive Forms. 
	 * 
	 * @param aemConfig
	 * 		AEM configuration typically configured using application.properties files.  This is
	 * 		typically injected by the Spring Framework.
	 * @param aemProxyConfig
	 * 		AEM proxy-specific configuration typically configured using application.properties files.
	 * 		This is typically injected by the Spring Framework.
	 * @param aemProxyTaskExecutor 
	 * @return
	 * 		JAX-RS Resource configuration customizer that is used by the spring-jersey starter to configure
	 * 		JAX-RS Resources (i.e. endpoints)
	 */
	@Bean
	public ResourceConfigCustomizer afProxyConfigurer(AemConfiguration aemConfig, AemProxyConfiguration aemProxyConfig, @Autowired(required = false) SslBundles sslBundles, TaskExecutor aemProxyTaskExecutor) {
		return config->config.register(new AemProxyJerseyEndpoint(aemConfig, aemProxyConfig, sslBundles, aemProxyTaskExecutor))
					  		 .register(new AemProxyJerseyAfSubmission())
					  		 ;
	}
	
	/**
	 * Supply a TaskExecutor for use by the AemProxyEndpoint.  This is used to process csrf token requests because they are Chunked.
	 * 
	 * @return the taskeExecutor that will be used to process csrf token requests.
	 */
	@Bean
	public TaskExecutor aemProxyTaskExecutor() {
		var executor = new SimpleAsyncTaskExecutor("AemProxy-");
		// Use virtual threads if available.  This will be the default for Java 21 and later.
		executor.setVirtualThreads(JavaVersion.getJavaVersion().isEqualOrNewerThan(JavaVersion.TWENTY_ONE));
		return executor;
	}

	/**
	 * Supply a AfSubmitLocalProcessor if the user has not already supplied one *and* there is an 
	 * available AfSubmissionHandler
	 * 
	 * Basically, a user can supply their own AfSubmitProcessor if they want to process things
	 * at the JAX-RS Servlet level.  I expect this to be an unusual case, most users will want
	 * to either process things locally using a custom AfSubmissionHandler or the will
	 * want to forward things to AEM by *not* providing a custom AfSubmissionHandler.
	 * 
	 * @param submissionHandlers
	 * 		List of local submission handlers.  This is injected by the Spring Framework.
	 * @return
	 * 		Processor that will call the first submission handler that says that it can
	 * 		process this request.
	 */
	@ConditionalOnMissingBean(AfSubmitProcessor.class)
	@ConditionalOnBean(AfSubmissionHandler.class)
	@Bean
	public AfSubmitProcessor localSubmitProcessor(List<AfSubmissionHandler> submissionHandlers, InternalAfSubmitAemProxyProcessor aemProxyProcessor) {
		return new AfSubmitLocalProcessor(submissionHandlers, aemProxyProcessor);
	}
	
	/**
	 * Supply a AfSubmitAemProxyProcessor if the user has not supplied any of the AfSubmit beans.
	 * 
	 * This is the default processor and it will forward all submissions on to the configured AEM
	 * instance.
	 * 
	 * @param aemConfig
	 * 		AEM configuration typically configured using application.properties files.  This is
	 * 		typically injected by the Spring Framework.
	 * @return
	 * 		Processor that forwards all submissions on to AEM.
	 */
	@ConditionalOnMissingBean({AfSubmitProcessor.class, AfSubmissionHandler.class})
	@Bean()
	public AfSubmitProcessor aemSubmitProcessor(AemConfiguration aemConfig, @Autowired(required = false) SslBundles sslBundles) {
		return new AfSubmitAemProxyProcessor(aemConfig, sslBundles);
	}
	
	/**
	 * Supply a AfSubmitAemProxyProcessor for use by the localSubmitProcessor.
	 * 
	 * This is the a processor that will forward all submissions on to the configured AEM
	 * instance.  It is used by the localSubmitProcessor to proxy any requests that aren't 
	 * true submissions (e.g. an internalsubmit).
	 * 
	 * @param aemConfig
	 * 		AEM configuration typically configured using application.properties files.  This is
	 * 		typically injected by the Spring Framework.
	 * @return
	 * 		Processor that forwards all submissions on to AEM.
	 */
	@ConditionalOnMissingBean(InternalAfSubmitAemProxyProcessor.class)
	@ConditionalOnBean(AfSubmissionHandler.class)
	@Bean
	public InternalAfSubmitAemProxyProcessor aemProxyProcessor(AemConfiguration aemConfig, @Autowired(required = false) SslBundles sslBundles) {
		return ()->new AfSubmitAemProxyProcessor(aemConfig, sslBundles);
	}
}