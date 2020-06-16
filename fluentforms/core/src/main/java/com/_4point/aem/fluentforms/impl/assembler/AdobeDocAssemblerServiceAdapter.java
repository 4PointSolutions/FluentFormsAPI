package com._4point.aem.fluentforms.impl.assembler;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com.adobe.fd.assembler.client.OperationException;

public class AdobeDocAssemblerServiceAdapter implements TraditionalDocAssemblerService {

	private static final Logger log = LoggerFactory.getLogger(AdobeDocAssemblerServiceAdapter.class);

	private final com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService;
	
	private final DocumentFactory documentFactory;

	public AdobeDocAssemblerServiceAdapter(DocumentFactory documentFactory,com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService) {
		super();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService is null.");
		this.documentFactory = DocumentFactory.getDefault();
		
	}


	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException {
		com.adobe.fd.assembler.client.AssemblerResult assemblerResult = adobeDocAssemblerService.invoke(AdobeDocumentFactoryImpl.getAdobeDocument(ddx), inputs,
				toAdobeAssemblerOptionSpec(adobAssemblerOptionSpec));
		return  toAdobeAssemblerResult(assemblerResult);
	}

	
	static com.adobe.fd.assembler.client.AssemblerOptionSpec toAdobeAssemblerOptionSpec(
			AssemblerOptionsSpec assemblerOptionSpec) {
		com.adobe.fd.assembler.client.AssemblerOptionSpec adobeAssemblerOptionSpec = new com.adobe.fd.assembler.client.AssemblerOptionSpec();
		/**
		 * to instruct the Assembler service to continue processing a job when an error
		 * occurs, invoke the AssemblerOptionSpec object’s setFailOnError method and
		 * pass false.
		 */
		setIfNotNull(adobeAssemblerOptionSpec::setFailOnError, assemblerOptionSpec.isFailOnError());
		log.info("FailonError=" + assemblerOptionSpec.isFailOnError());
		return adobeAssemblerOptionSpec;

	}	

	static AssemblerResult toAdobeAssemblerResult(com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {
        return new AdobeAssemblerResultImpl(assemblerResult);
	}

}
