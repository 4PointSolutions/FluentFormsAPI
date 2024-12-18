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
	private <T extends Builder> T setAemFields(T builder, AemConfiguration aemConfig, SslBundles sslBundles) {
		return (T)(builder.machineName(aemConfig.servername())
						  .port(aemConfig.port())
						  .basicAuthentication(aemConfig.user(), aemConfig.password())
						  .useSsl(aemConfig.useSsl())
						  .clientFactory(()->JerseyClientFactory.createClient(sslBundles, aemConfig.sslBundle()))
				  );
	}

	@ConditionalOnMissingBean
	@Bean
	public AdaptiveFormsService adaptiveFormsService(AemConfiguration aemConfig, Function<InputStream, InputStream> afInputStreamFilter, @Autowired(required = false) SslBundles sslBundles) {
		return setAemFields(AdaptiveFormsService.builder(), aemConfig, sslBundles)
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
	public AssemblerService assemblerService(AemConfiguration aemConfig, @Autowired(required = false) SslBundles sslBundles) {
		RestServicesDocAssemblerServiceAdapter adapter = setAemFields(RestServicesDocAssemblerServiceAdapter.builder(), aemConfig, sslBundles).build();
		return new AssemblerServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@ConditionalOnMissingBean
	@Bean
	public DocAssuranceService docAssuranceService(AemConfiguration aemConfig, @Autowired(required = false) SslBundles sslBundles) {
		RestServicesDocAssuranceServiceAdapter adapter = setAemFields(RestServicesDocAssuranceServiceAdapter.builder(), aemConfig, sslBundles).build();
		return new DocAssuranceServiceImpl(adapter);
	}

	@ConditionalOnMissingBean
	@Bean
	public FormsService formsService(AemConfiguration aemConfig, @Autowired(required = false) SslBundles sslBundles) {
		RestServicesFormsServiceAdapter adapter = setAemFields(RestServicesFormsServiceAdapter.builder(), aemConfig, sslBundles).build();
		return new FormsServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@ConditionalOnMissingBean
	@Bean
	public GeneratePDFService generatePDFService(AemConfiguration aemConfig, @Autowired(required = false) SslBundles sslBundles) {
		RestServicesGeneratePDFServiceAdapter adapter = setAemFields(RestServicesGeneratePDFServiceAdapter.builder(), aemConfig, sslBundles).build();
		return new GeneratePDFServiceImpl(adapter);
	}

	@ConditionalOnMissingBean
	@Bean
	public Html5FormsService html5FormsService(AemConfiguration aemConfig, AemProxyConfiguration aemProxyConfig, @Autowired(required = false) SslBundles sslBundles) {
		return setAemFields(Html5FormsService.builder(), aemConfig, sslBundles)
				.addRenderResultFilter(afInputStreamFilter(aemProxyConfig))
				.build();
	}

	@ConditionalOnMissingBean
	@Bean
	public OutputService outputService(AemConfiguration aemConfig, @Autowired(required = false) SslBundles sslBundles) {
		RestServicesOutputServiceAdapter adapter = setAemFields(RestServicesOutputServiceAdapter.builder(), aemConfig, sslBundles).build();
		return new OutputServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}
	
	@ConditionalOnMissingBean
	@Bean
	public PdfUtilityService pdfUtilityService(AemConfiguration aemConfig, @Autowired(required = false) SslBundles sslBundles) {
		RestServicesPdfUtilityServiceAdapter adapter = setAemFields(RestServicesPdfUtilityServiceAdapter.builder(), aemConfig, sslBundles).build();
		return new PdfUtilityServiceImpl(adapter);
	}

	@ConditionalOnMissingBean
	@Bean
	public ConvertPdfService convertPdfService(AemConfiguration aemConfig, @Autowired(required = false) SslBundles sslBundles) {
		RestServicesConvertPdfServiceAdapter adapter = setAemFields(RestServicesConvertPdfServiceAdapter.builder(), aemConfig, sslBundles).build();
		return new ConvertPdfServiceImpl(adapter);
	}
	
	@ConditionalOnMissingBean
	@Bean 
	public DocumentFactory documentFactory() {
		return SimpleDocumentFactoryImpl.getFactory();
	}
}
