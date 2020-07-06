package com._4point.aem.fluentforms.impl.assembler;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;

public class AdobeDocAssemblerServiceAdapter implements TraditionalDocAssemblerService, AssemblerResult {

	private static final Logger log = LoggerFactory.getLogger(AdobeDocAssemblerServiceAdapter.class);

	private final com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService;
    private final com.adobe.fd.assembler.client.AssemblerResult assemblerResult;
	
	public AdobeDocAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService, com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {		
		super();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");		
		this.assemblerResult = Objects.requireNonNull(assemblerResult, "assemblerResult cannot be null.");
		
	}

	public AdobeDocAssemblerServiceAdapter(com.adobe.fd.assembler.client.AssemblerResult assemblerResult ) {
		super();
		this.adobeDocAssemblerService = null;
		this.assemblerResult = Objects.requireNonNull(assemblerResult, "assemblerResult cannot be null.");
	}


	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException {
		com.adobe.fd.assembler.client.AssemblerResult assemblerResult = adobeDocAssemblerService.invoke(AdobeDocumentFactoryImpl.getAdobeDocument(ddx), inputs,
				toAdobeAssemblerOptionSpec(adobAssemblerOptionSpec));
		return toAssemblerResult(assemblerResult);
	}
	
	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) {
		return null;
	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) {
		return null;
	}
	
    public static com.adobe.fd.assembler.client.AssemblerOptionSpec toAdobeAssemblerOptionSpec(
			AssemblerOptionsSpec assemblerOptionSpec) {
		com.adobe.fd.assembler.client.AssemblerOptionSpec adobeAssemblerOptionSpec = new com.adobe.fd.assembler.client.AssemblerOptionSpec();
		setIfNotNull(adobeAssemblerOptionSpec::setFailOnError, assemblerOptionSpec.isFailOnError());
		log.info("FailonError= " + assemblerOptionSpec.isFailOnError());
		return adobeAssemblerOptionSpec;

	}	

	static AssemblerResult toAssemblerResult(com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {
		return new AdobeDocAssemblerServiceAdapter(assemblerResult);
	}

	@Override
	public Map<String, Document> getDocuments() {
		return null;
	}

	@Override
	public List<String> getFailedBlockNames() {
		return null;
	}

	@Override
	public Document etJobLog() {
		return null;
	}

	@Override
	public int getLastBatesNumber() {
		return 0;
	}

	@Override
	public Map<String, List<String>> getMultipleResultsBlocks() {
		return null;
	}

	@Override
	public int getNumRequestedBlocks() {
		return 0;
	}

	@Override
	public List<String> getSuccessfulBlockNames() {
		return null;
	}

	@Override
	public List<String> getSuccessfulDocumentNames() {
		return null;
	}

	@Override
	public Map<String, OperationException> getThrowables() {
		return null;
	}
	
	

	

}
