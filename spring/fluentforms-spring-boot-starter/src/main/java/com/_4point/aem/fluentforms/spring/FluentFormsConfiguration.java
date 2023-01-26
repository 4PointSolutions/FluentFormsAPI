package com._4point.aem.fluentforms.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService;
import com._4point.aem.docservices.rest_services.client.assembler.RestServicesDocAssemblerServiceAdapter;
import com._4point.aem.docservices.rest_services.client.docassurance.RestServicesDocAssuranceServiceAdapter;
import com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter;
import com._4point.aem.docservices.rest_services.client.generatePDF.RestServicesGeneratePDFServiceAdapter;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService;
import com._4point.aem.docservices.rest_services.client.output.RestServicesOutputServiceAdapter;
import com._4point.aem.docservices.rest_services.client.pdfUtility.RestServicesPdfUtilityServiceAdapter;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com._4point.aem.fluentforms.impl.docassurance.DocAssuranceServiceImpl;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com._4point.aem.fluentforms.impl.generatePDF.GeneratePDFServiceImpl;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;
import com._4point.aem.fluentforms.impl.pdfUtility.PdfUtilityServiceImpl;

@Lazy
@Configuration
public class FluentFormsConfiguration {
	
	@SuppressWarnings("unchecked")
	private <T extends Builder> T setAemFields(T builder, AemConfiguration aemConfig) {
		return (T)(builder.machineName(aemConfig.servername())
						  .port(aemConfig.port())
						  .basicAuthentication(aemConfig.user(), aemConfig.password())
						  .useSsl(aemConfig.useSsl())
				  );
	}

	@Bean
	public AdaptiveFormsService adaptiveFormsService(AemConfiguration aemConfig) {
		return setAemFields(AdaptiveFormsService.builder(), aemConfig).build();
	}

	@Bean
	public AssemblerService assemblerService(AemConfiguration aemConfig) {
		RestServicesDocAssemblerServiceAdapter adapter = setAemFields(RestServicesDocAssemblerServiceAdapter.builder(), aemConfig).build();
		return new AssemblerServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@Bean
	public DocAssuranceService docAssuranceService(AemConfiguration aemConfig) {
		RestServicesDocAssuranceServiceAdapter adapter = setAemFields(RestServicesDocAssuranceServiceAdapter.builder(), aemConfig).build();
		return new DocAssuranceServiceImpl(adapter);
	}

	@Bean
	public FormsService formsService(AemConfiguration aemConfig) {
		RestServicesFormsServiceAdapter adapter = setAemFields(RestServicesFormsServiceAdapter.builder(), aemConfig).build();
		return new FormsServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@Bean
	public GeneratePDFService generatePDFService(AemConfiguration aemConfig) {
		RestServicesGeneratePDFServiceAdapter adapter = setAemFields(RestServicesGeneratePDFServiceAdapter.builder(), aemConfig).build();
		return new GeneratePDFServiceImpl(adapter);
	}

	@Bean
	public Html5FormsService html5FormsService(AemConfiguration aemConfig) {
		return setAemFields(Html5FormsService.builder(), aemConfig).build();
	}

	@Bean
	public OutputService outputService(AemConfiguration aemConfig) {
		RestServicesOutputServiceAdapter adapter = setAemFields(RestServicesOutputServiceAdapter.builder(), aemConfig).build();
		return new OutputServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}
	
	@Bean
	public PdfUtilityService pdfUtilityService(AemConfiguration aemConfig) {
		RestServicesPdfUtilityServiceAdapter adapter = setAemFields(RestServicesPdfUtilityServiceAdapter.builder(), aemConfig).build();
		return new PdfUtilityServiceImpl(adapter);
	}
}
