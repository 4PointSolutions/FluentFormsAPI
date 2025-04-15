package com._4point.aem.fluentforms.spring;

import java.io.InputStream;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService;
import com._4point.aem.docservices.rest_services.client.assembler.RestServicesDocAssemblerServiceAdapter;
import com._4point.aem.docservices.rest_services.client.convertPdf.RestServicesConvertPdfServiceAdapter;
import com._4point.aem.docservices.rest_services.client.docassurance.RestServicesDocAssuranceServiceAdapter;
import com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter;
import com._4point.aem.docservices.rest_services.client.generatePDF.RestServicesGeneratePDFServiceAdapter;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.Builder.RestClientFactory;
import com._4point.aem.docservices.rest_services.client.helpers.FormsFeederUrlFilterBuilder;
import com._4point.aem.docservices.rest_services.client.helpers.StandardFormsFeederUrlFilters;
import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService;
import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.client.output.RestServicesOutputServiceAdapter;
import com._4point.aem.docservices.rest_services.client.pdfUtility.RestServicesPdfUtilityServiceAdapter;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com._4point.aem.fluentforms.impl.convertPdf.ConvertPdfServiceImpl;
import com._4point.aem.fluentforms.impl.docassurance.DocAssuranceServiceImpl;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com._4point.aem.fluentforms.impl.generatePDF.GeneratePDFServiceImpl;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;
import com._4point.aem.fluentforms.impl.pdfUtility.PdfUtilityServiceImpl;

import jakarta.ws.rs.client.Client;

/**
 * AutoConfiguration for the FluentForms Rest Services Client library.
 * 
 * This class automatically configures a set of beans (one for each AEM service) that can be injected
 * into any Spring Boot code. 
 *
 */
@Lazy
@AutoConfiguration
@EnableConfigurationProperties(AemConfiguration.class)
public class FluentFormsAutoConfiguration {
	//  // TODO: Either call JerseuRestClient.factory(JerseyClientFactory.createClient(sslBundles, aemConfig.sslBundle())) or create SpringRestClient
//	private static final BiFunction<SslBundles, String, RestClientFactory> restClientFactory = (b, s)->JerseyRestClient.factory(JerseyClientFactory.createClient(b, s)); 
	                        
	@SuppressWarnings("unchecked")
	private <T extends Builder> T setAemFields(T builder, AemConfiguration aemConfig) {
		return (T)(builder.machineName(aemConfig.servername())
						  .port(aemConfig.port())
						  .basicAuthentication(aemConfig.user(), aemConfig.password())
						  .useSsl(aemConfig.useSsl())
//						  .clientFactory(()->JerseyClientFactory.createClient(sslBundles, aemConfig.sslBundle()))
				  );
	}

	@ConditionalOnMissingBean
	@Bean
	public RestClientFactory restClientFactory(AemConfiguration aemConfig, @Autowired(required = false) SslBundles sslBundles) {
		Client jerseyClient = JerseyClientFactory.createClient(sslBundles, aemConfig.sslBundle());	// Create custom Jersey Client with SSL bundle
		return JerseyRestClient.factory(jerseyClient); // Create a RestClientFactory using JerseyClient implementation
	}
	
	@ConditionalOnMissingBean
	@Bean
	public AdaptiveFormsService adaptiveFormsService(AemConfiguration aemConfig, Function<InputStream, InputStream> afInputStreamFilter, RestClientFactory restClientFactory) {
		return setAemFields(AdaptiveFormsService.builder(restClientFactory), aemConfig)
				.addRenderResultFilter(afInputStreamFilter)
				.build();
	}

	@ConditionalOnMissingBean
	@Bean
	public Function<InputStream, InputStream> afInputStreamFilter(AemProxyConfiguration aemProxyConfig) {
		return buildInputFilter(aemProxyConfig.aemPrefix(), aemProxyConfig.clientPrefix());	
	}

	private Function<InputStream, InputStream> buildInputFilter(String aemPrefix, String clientPrefix) {
		FormsFeederUrlFilterBuilder builder = StandardFormsFeederUrlFilters.getUrlFilterBuilder();
		builder = aemPrefix.isBlank() ? builder : builder.aemPrefix(aemPrefix);
		builder = clientPrefix.isBlank() ? builder : builder.clientPrefix(clientPrefix);;
		return builder.buildInputStreamFn();
	}

	@ConditionalOnMissingBean
	@Bean
	public AssemblerService assemblerService(AemConfiguration aemConfig, RestClientFactory restClientFactory) {
		RestServicesDocAssemblerServiceAdapter adapter = setAemFields(RestServicesDocAssemblerServiceAdapter.builder(restClientFactory), aemConfig).build();
		return new AssemblerServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@ConditionalOnMissingBean
	@Bean
	public DocAssuranceService docAssuranceService(AemConfiguration aemConfig, RestClientFactory restClientFactory) {
		RestServicesDocAssuranceServiceAdapter adapter = setAemFields(RestServicesDocAssuranceServiceAdapter.builder(restClientFactory), aemConfig).build();
		return new DocAssuranceServiceImpl(adapter);
	}

	@ConditionalOnMissingBean
	@Bean
	public FormsService formsService(AemConfiguration aemConfig, RestClientFactory restClientFactory) {
		RestServicesFormsServiceAdapter adapter = setAemFields(RestServicesFormsServiceAdapter.builder(restClientFactory), aemConfig).build();
		return new FormsServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@ConditionalOnMissingBean
	@Bean
	public GeneratePDFService generatePDFService(AemConfiguration aemConfig, RestClientFactory restClientFactory) {
		RestServicesGeneratePDFServiceAdapter adapter = setAemFields(RestServicesGeneratePDFServiceAdapter.builder(restClientFactory), aemConfig).build();
		return new GeneratePDFServiceImpl(adapter);
	}

	@ConditionalOnMissingBean
	@Bean
	public Html5FormsService html5FormsService(AemConfiguration aemConfig, AemProxyConfiguration aemProxyConfig, RestClientFactory restClientFactory) {
		return setAemFields(Html5FormsService.builder(restClientFactory), aemConfig)
				.addRenderResultFilter(afInputStreamFilter(aemProxyConfig))
				.build();
	}

	@ConditionalOnMissingBean
	@Bean
	public OutputService outputService(AemConfiguration aemConfig, RestClientFactory restClientFactory) {
		RestServicesOutputServiceAdapter adapter = setAemFields(RestServicesOutputServiceAdapter.builder(restClientFactory), aemConfig).build();
		return new OutputServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}
	
	@ConditionalOnMissingBean
	@Bean
	public PdfUtilityService pdfUtilityService(AemConfiguration aemConfig, RestClientFactory restClientFactory) {
		RestServicesPdfUtilityServiceAdapter adapter = setAemFields(RestServicesPdfUtilityServiceAdapter.builder(restClientFactory), aemConfig).build();
		return new PdfUtilityServiceImpl(adapter);
	}

	@ConditionalOnMissingBean
	@Bean
	public ConvertPdfService convertPdfService(AemConfiguration aemConfig, RestClientFactory restClientFactory) {
		RestServicesConvertPdfServiceAdapter adapter = setAemFields(RestServicesConvertPdfServiceAdapter.builder(restClientFactory), aemConfig).build();
		return new ConvertPdfServiceImpl(adapter);
	}
	
	@ConditionalOnMissingBean
	@Bean 
	public DocumentFactory documentFactory() {
		return SimpleDocumentFactoryImpl.getFactory();
	}
}
