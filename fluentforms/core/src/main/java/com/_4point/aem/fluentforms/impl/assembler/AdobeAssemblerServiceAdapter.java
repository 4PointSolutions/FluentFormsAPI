package com._4point.aem.fluentforms.impl.assembler;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com.adobe.fd.assembler.client.OperationException;

public class AdobeAssemblerServiceAdapter implements TraditionalDocAssemblerService {

	private static final Logger log = LoggerFactory.getLogger(AdobeAssemblerServiceAdapter.class);

	private final com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService;
    private final DocumentFactory documentFactory;
	
	public AdobeAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService, DocumentFactory documentFactory) {				
		super();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");		
		this.documentFactory = Objects.requireNonNull(documentFactory, "Document Factory cannot be null.");
	}
	
	public AdobeAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService) {		
		super();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");		
		this.documentFactory = DocumentFactory.getDefault();
	}
	
	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException {
		com.adobe.fd.assembler.client.AssemblerResult assemblerResult;
		try {
			assemblerResult = adobeDocAssemblerService.invoke(AdobeDocumentFactoryImpl.getAdobeDocument(ddx), toAdobeDocumentMap(sourceDocuments),
					toAdobeAssemblerOptionSpec(adobAssemblerOptionSpec));
		} catch (OperationException e) {
			 throw new AssemblerServiceException("Error while aasembling the documents ",e);
		}
		return toAssemblerResult(assemblerResult);
	}
	
	
	private Map<String, Object> toAdobeDocumentMap(Map<String, Object> sourceDocuments) {
		Map<String, Object> input = new HashMap<String, Object>();
		sourceDocuments.forEach((docName, doc) -> {
			input.put(docName, AdobeDocumentFactoryImpl.getAdobeDocument((Document)doc));
		});
		return input;
	}

	/*
	 * @Override public PDFAValidationResult isPDFA(Document inDoc,
	 * PDFAValidationOptionSpec options) throws AssemblerServiceException,
	 * ValidationException { return
	 * adobeDocAssemblerService.isPDFA(AdobeDocumentFactoryImpl.getAdobeDocument(
	 * inDoc), options); }
	 * 
	 * @Override public PDFAConversionResult toPDFA(Document inDoc,
	 * PDFAConversionOptionSpec options) throws AssemblerServiceException,
	 * ConversionException { return
	 * adobeDocAssemblerService.toPDFA(AdobeDocumentFactoryImpl.getAdobeDocument(
	 * inDoc), options); }
	 */
	
	public static com.adobe.fd.assembler.client.AssemblerOptionSpec toAdobeAssemblerOptionSpec(
			AssemblerOptionsSpec assemblerOptionSpec) {
		com.adobe.fd.assembler.client.AssemblerOptionSpec adobeAssemblerOptionSpec = new com.adobe.fd.assembler.client.AssemblerOptionSpec();
		setIfNotNull(adobeAssemblerOptionSpec::setFailOnError, assemblerOptionSpec.isFailOnError());
		setIfNotNull(adobeAssemblerOptionSpec::setDefaultStyle, assemblerOptionSpec.getDefaultStyle());
		setIfNotNull(adobeAssemblerOptionSpec::setFirstBatesNumber, assemblerOptionSpec.getFirstBatesNumber());
		setIfNotNull(adobeAssemblerOptionSpec::setLogLevel, assemblerOptionSpec.getLogLevel()!=null?assemblerOptionSpec.getLogLevel().toString():null);
		setIfNotNull(adobeAssemblerOptionSpec::setTakeOwnership, assemblerOptionSpec.isTakeOwnership());
		setIfNotNull(adobeAssemblerOptionSpec::setValidateOnly, assemblerOptionSpec.isValidateOnly());
		return adobeAssemblerOptionSpec;

	}

	private AssemblerResult toAssemblerResult(com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {
		log.info("AdobeAssembler result to fluentForm assembler result");
		AssemblerResultImpl assemblerResultImpl = new AssemblerResultImpl();
		setIfNotNull(assemblerResultImpl::setFailedBlockNames, assemblerResult.getFailedBlockNames());
		setIfNotNull(assemblerResultImpl::setJobLog, assemblerResult.getJobLog()!=null ? documentFactory.create(assemblerResult.getJobLog()):null);
		setIfNotNull(assemblerResultImpl::setLastBatesNumber, assemblerResult.getLastBatesNumber());
		setIfNotNull(assemblerResultImpl::setMultipleResultsBlocks, assemblerResult.getMultipleResultsBlocks());
		setIfNotNull(assemblerResultImpl::setNumRequestedBlocks, assemblerResult.getNumRequestedBlocks());
		setIfNotNull(assemblerResultImpl::setSuccessfulDocumentNames, assemblerResult.getSuccessfulDocumentNames());
		setIfNotNull(assemblerResultImpl::setThrowables, assemblerResult.getThrowables());
		setIfNotNull(assemblerResultImpl::setSuccessfulBlockNames, assemblerResult.getSuccessfulBlockNames());
		Map<String, Document> documents= new HashMap<String, Document>();
		if(MapUtils.isNotEmpty(assemblerResult.getDocuments())) {
			assemblerResult.getDocuments().forEach((docName, doc) -> {
				documents.put(docName, documentFactory.create(doc));
			});		
		}
		setIfNotNull(assemblerResultImpl::setDocuments, documents);
		return assemblerResultImpl;

	}


}
