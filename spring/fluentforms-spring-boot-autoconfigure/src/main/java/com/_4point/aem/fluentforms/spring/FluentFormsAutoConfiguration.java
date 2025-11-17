package com._4point.aem.fluentforms.spring;

import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.NoSuchSslBundleException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Fallback;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestClient;

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
import com._4point.aem.fluentforms.spring.rest_services.client.SpringRestClientRestClient;


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
	                        
	@SuppressWarnings("unchecked")
	private <T extends Builder> T setAemFields(T builder, AemConfiguration aemConfig) {
		return (T)(builder.machineName(aemConfig.servername())
						  .port(aemConfig.port())
						  .basicAuthentication(aemConfig.user(), aemConfig.password())
						  .useSsl(aemConfig.useSsl())
				  );
	}

	
	// matchIfMissing is set to true so that, by default (if nothing is specified in the properties file), then the SpringRestClient is used.
	@ConditionalOnProperty(prefix="fluentforms", name="restclient", havingValue="springrestclient", matchIfMissing=true )
	@ConditionalOnMissingBean
	@Fallback
	@Bean
	public RestClientFactory springRestClientFactory(AemConfiguration aemConfig, RestClient.Builder restClientBuilder, RestClientSsl restClientSsl) {
		return SpringRestClientRestClient.factory(aemConfig.useSsl() ? restClientBuilder.apply(getSslBundle(aemConfig.sslBundle(), restClientSsl))
																	 : restClientBuilder
												); // Create a RestClientFactory using Spring RestClient implementation
	}

	private static Consumer<org.springframework.web.client.RestClient.Builder> getSslBundle(String sslBundleName, RestClientSsl restClientSsl) {
		try {
			return restClientSsl.fromBundle(sslBundleName);
		} catch (NoSuchSslBundleException e) {
			// Default to normal SSL context (which includes the default trust store)
			// This is not ideal since misspelling the bundle name silently fails, but is required to avoid breaking existing code.  
			// At dome point it should probably be changed to let the exception pass and only use the default SSL context 
			// if the SSL bundle name is empty.
			return b->{}; // No-op;
		}
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
