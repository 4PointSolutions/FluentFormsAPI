package com._4point.aem.fluentforms.impl.assembler;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

public class AdobeAssemblerServiceAdapter implements TraditionalDocAssemblerService, AssemblerResult {

	private static final Logger log = LoggerFactory.getLogger(AdobeAssemblerServiceAdapter.class);

	private final com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService;
    private final com.adobe.fd.assembler.client.AssemblerResult assemblerResult;
    private final DocumentFactory documentFactory;
    private Map<String, Document> sourceDocuments;
	
	public AdobeAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService, com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {		
		super();
		this.documentFactory = DocumentFactory.getDefault();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");		
		this.assemblerResult = Objects.requireNonNull(assemblerResult, "assemblerResult cannot be null.");
		this.sourceDocuments = null;
		
	}

	public AdobeAssemblerServiceAdapter(com.adobe.fd.assembler.client.AssemblerResult assemblerResult ) {
		super();
		this.documentFactory = DocumentFactory.getDefault();
		this.adobeDocAssemblerService = null;
		this.assemblerResult = Objects.requireNonNull(assemblerResult, "assemblerResult cannot be null.");
		this.sourceDocuments = null;
	}
  
	
	public AdobeAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService, DocumentFactory documentFactory) {				
		super();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");		
		this.documentFactory = Objects.requireNonNull(documentFactory, "Document Factory cannot be null.");
		this.assemblerResult = null;
		this.sourceDocuments = null;
	}
	
	public AdobeAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService) {		
		super();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");		
		this.documentFactory = DocumentFactory.getDefault();
		this.assemblerResult = null;
		this.sourceDocuments = null;
	}
	
	public AdobeAssemblerServiceAdapter(Map<String, Document> sourceDocuments, com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {		
		super();
		this.adobeDocAssemblerService = null;		
		this.documentFactory = DocumentFactory.getDefault();
		this.assemblerResult =  Objects.requireNonNull(assemblerResult, "assemblerResult cannot be null.");
		this.sourceDocuments = sourceDocuments ;
		
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
		log.info("FailonError= " + assemblerOptionSpec.isFailOnError());
		return adobeAssemblerOptionSpec;

	}	

	static AssemblerResult toAssemblerResult(com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {
		return new AdobeAssemblerServiceAdapter(assemblerResult);
	}

	@Override
	public Map<String, Document> getDocuments() {
		if(sourceDocuments == null) {
			sourceDocuments = new HashMap<String, Document>();
			if (assemblerResult != null) {
				if (assemblerResult.getDocuments() != null) {
					assemblerResult.getDocuments().forEach((docName, doc) -> {
						sourceDocuments.put(docName, documentFactory.create(doc));
					});
				} 
			}
		}
		return sourceDocuments;
	}

	@Override
	public List<String> getFailedBlockNames() {
		return assemblerResult.getSuccessfulBlockNames();
	}

	@Override
	public Document getJobLog() {
		return documentFactory.create(assemblerResult.getJobLog());
	}

	@Override
	public int getLastBatesNumber() {
		return assemblerResult.getLastBatesNumber();
	}

	@Override
	public Map<String, List<String>> getMultipleResultsBlocks() {
		return assemblerResult.getMultipleResultsBlocks();
	}

	@Override
	public int getNumRequestedBlocks() {
		return assemblerResult.getNumRequestedBlocks();
	}

	@Override
	public List<String> getSuccessfulBlockNames() {
		return assemblerResult.getSuccessfulBlockNames();
	}

	@Override
	public List<String> getSuccessfulDocumentNames() {
		return assemblerResult.getSuccessfulDocumentNames();
	}

	@Override
	public Map<String, OperationException> getThrowables() {
		return assemblerResult.getThrowables();
	}

	@Override
	public void close() throws IOException {
	
	}

	
	

	

}
