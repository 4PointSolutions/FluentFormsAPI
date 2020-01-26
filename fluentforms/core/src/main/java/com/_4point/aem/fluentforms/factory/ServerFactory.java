package com._4point.aem.fluentforms.factory;

import org.apache.sling.api.resource.ResourceResolver;

import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.docassurance.AdobeDocAssuranceServiceAdapter;
import com._4point.aem.fluentforms.impl.docassurance.DocAssuranceServiceImpl;
import com._4point.aem.fluentforms.impl.forms.AdobeFormsServiceAdapter;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com._4point.aem.fluentforms.impl.output.AdobeOutputServiceAdapter;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;

public class ServerFactory {
	
	public static DocAssuranceService createDocAssuranceService(com.adobe.fd.docassurance.client.api.DocAssuranceService adobeDocAssuranceService, ResourceResolver resourceRsolver) {
		return new DocAssuranceServiceImpl(new AdobeDocAssuranceServiceAdapter(adobeDocAssuranceService, resourceRsolver));
	}
	
	public static FormsService createFormsService(com.adobe.fd.forms.api.FormsService adobeformsService) {
		return new FormsServiceImpl(new AdobeFormsServiceAdapter(adobeformsService), UsageContext.SERVER_SIDE);
	}
	
	public static OutputService createOutputService(com.adobe.fd.output.api.OutputService adobeOutputService) {
		return new OutputServiceImpl(new AdobeOutputServiceAdapter(adobeOutputService), UsageContext.SERVER_SIDE);
	}
	
	public static DocumentFactory getDefaultDocumentFactory() {
		return AdobeDocumentFactoryImpl.getFactory();
	}
}
